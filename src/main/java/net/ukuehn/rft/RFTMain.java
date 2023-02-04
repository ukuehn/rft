/* -*- java -*-
 *
 * (C) 2013 Ulrich Kuehn <ukuehn@acm.org>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */


package net.ukuehn.rft;


import net.ukuehn.csv.*;
import net.ukuehn.util.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;




public class RFTMain {

	static final String version = "0.1";

	static final String usage =
		"ReportFiddlingTool version "+version+" by "
		+"Ulrich Kuehn <ukuehn@acm.org>\n\n"
		+"Usage:\n"
		+"java -jar rft-"+version+".jar <op> [options] "
		+"<file>\n\n"
		+"  where <op> is one of:\n\n"
		+"     csvparse     parse a CSV file\n"
		+"     ori          process ORI report\n"
		+"     patches      process XML patch report, taking also"
		+" multiple input files.\n"
		+"     combpat      process CSV pattern report where"
		+" pattern and\n"
		+"                  date are given in combined column (#11).\n"
		+"     seppat       process CSV pattern report where pattern"
		+" and its date\n"
		+"                  are in separate columns (#7 and #9).\n"
		+"                  Dates older than 32 days are cut off.\n"
		+"\n"
		+"  with [options] being\n"
		+"\n"
		+"     -q           suppress header output\n"
		+"     -d <delim>   use delim as csv field delimiter\n"
		+"     -r <refdate> set reference date to determine age\n"
		+"                  in format either yyyyMMdd, yyyy-MM-dd,\n"
		+"                  or dd.MM.yyyy\n"
		+"     -u           patches: include unkown state\n"
		+"     -H s0:...sn  patches: give split values for histogram\n"
		+"     -O <outfile> patches: send detailed output to file\n"
		+"\n";


	int debugLevel = 0;
	int verbLevel = 1;
	Date refDate;


	protected static double[] getSplits(String splitStr)
		throws NumberFormatException {
		StringTokenizer st = new StringTokenizer(splitStr, ":");
		int n = st.countTokens();

		double[] res = new double[n];
		for (int i = 0;  i < n;  i++) {
			String token = st.nextToken();
			try {
				res[i] = Double.parseDouble(token);
			} catch (NumberFormatException e) {
				throw new NumberFormatException(
					      "Split definition must be "
					      +"colon-separated doubles: "
					      +"'"+token+"' in "
					      +splitStr);
			}
		}
		return res;
	}


	protected void handlePatches(String[] fname,
				     String splitParm,
				     String ofile,
				     boolean includeUnknown)
		throws IOException {

		double[] defaultSplits = { 0.0, 0.9, 0.98, 1.0, 1.01 };

		double[] splits = defaultSplits;
		if (splitParm != null) {
			try {
				splits = getSplits(splitParm);
			} catch (NumberFormatException e) {
				System.err.println(e.getMessage());
			}
		}

		PrintWriter outDetail = new PrintWriter(System.out, true);
		PrintWriter outHist = new PrintWriter(System.out, true);
		if (ofile != null) {
			if (!ofile.equals("-")) {
				outDetail =
					new PrintWriter(new FileWriter(ofile));
			}
		}

		RangeHistogram hist = new RangeHistogram(splits);
		StateCounter sc = new StateCounter();
		sc.setIncludeUnknown(includeUnknown);
		XMLPatchCounter xpc = new XMLPatchCounter(outDetail,
							  outHist,
							  sc, hist);
		DefaultHandler handler = xpc;
		if (verbLevel > 0) {
			xpc.printResultsCSV("Computer", sc.headerToStrings());
		}
		for (int i = 0;  i < fname.length;  i++) {
			SAXParserFactory factory
				= SAXParserFactory.newInstance();
			try {
				SAXParser sp = factory.newSAXParser();
				File f = new File(fname[i]);
				sp.parse(f, handler);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
		xpc.printResultsCSV("Total", sc.totalToStrings());
		xpc.printHistogram();
		outDetail.flush();
		outDetail.close();
		outHist.flush();
		outHist.close();
	}


	protected void handleSeparatePattern(String fname) 
		throws IOException, RowReaderException {

		SeparatePatternDateReport pr
			= new SeparatePatternDateReport(fname, refDate);
		pr.setDebugLevel(debugLevel);
		if (verbLevel > 0) {
			System.out.println(pr.getHeaderString());
		}
		pr.processData();
		System.out.println(pr.getResultString());
	}


	protected void handleCombinedPattern(String fname) 
		throws IOException, RowReaderException {

		CombinedPatternDateReport pr
			= new CombinedPatternDateReport(fname, refDate);
		pr.setDebugLevel(debugLevel);
		if (verbLevel > 0) {
			System.out.println(pr.getHeaderString());
		}
		pr.processData();
		System.out.println(pr.getResultString());
	}


	protected void handleORI(String fname)
		throws IOException, RowReaderException {

		ORIReport or = new ORIReport(fname);
		or.setDebugLevel(debugLevel);
		if (verbLevel > 0) {
			System.out.println(or.getHeaderString());
		}
		or.processData();
		System.out.println(or.getResultString());
	}


	protected void handleCSVParse(String fname, char delim) {

		CSVParser cp;
		try {
			cp = new CSVParser(new FileReader(fname), delim);

			while (true) {
				String[] res = cp.nextRow();
				if (res == null) {
					break;
				}
				System.err.println("Got "
						   +res.length
						   +" records in row");
				for (int i = 0;  i < 5;  i++) {
					if (res.length > i) {
						System.err.println("Record "+i
								   +":"
								   +res[i]);
					}
				}
			}
		} catch (Exception e) {
			return;
		}
	}


	protected Date processRefDate(String sRef) {
		SimpleDateFormat fmt;

		if (sRef == null) {
			return null;
		}
		if (sRef.equals("auto")) {
			return null;
		}
		if (sRef.equals("current") || sRef.equals("today")) {
			return new Date();
		}
		if (sRef.indexOf(".") >= 0) {
			fmt = new SimpleDateFormat("dd.MM.yyyy");
		} else if (sRef.indexOf("-") >= 0) {
			fmt = new SimpleDateFormat("yyyy-MM-dd");
		} else {
			fmt = new SimpleDateFormat("yyyyMMdd");
		}
		Date res = fmt.parse(sRef, new ParsePosition(0));
		return res;
	}


	protected void usage() {
		System.err.println(usage);
		System.exit(1);
	}


	public RFTMain(String[] args)
		throws IOException, RowReaderException {

		int nextopt;
		int debug = 0;
		String optArgDebug = null;
		String optRefDate = null;
		int optVerbLevel = 1;
		String optArgDetailOutFile = null;
		String optArgHistSplits = null;
		String command = null;
		String optArgDelim = null;
		char delim = CSVParser.defaultSepChar;
		boolean optUnknownState = false;


		refDate = null;
		if (args.length > 1) {
			if (!args[0].startsWith("-")) {
				command = args[0];
			}
		}
		for (nextopt = 1;  nextopt < args.length;  nextopt++) {

			/* handle options here */
			if (!args[nextopt].startsWith("-")) {
				break;
			}
			//if (args[nextopt].equals("-D")) {
			//	nextopt++;
			//	if (nextopt < args.length) {
			//		optArgDebug = args[nextopt];
			//	} else {
			//		usage();
			//	}
			if (args[nextopt].equals("-D")) {
				debugLevel += 1;
			} else if (args[nextopt].equals("-d")) {
				nextopt++;
				if (nextopt < args.length) {
					optArgDelim = args[nextopt];
				} else {
					usage();
				}
			} else if (args[nextopt].equals("-O")) {
				nextopt++;
				if (nextopt < args.length) {
					optArgDetailOutFile = args[nextopt];
				} else {
					usage();
				}
			} else if (args[nextopt].equals("-r")) {
				nextopt++;
				if (nextopt < args.length) {
					optRefDate = args[nextopt];
				} else {
					usage();
				}
			} else if (args[nextopt].equals("-q")) {
				optVerbLevel = 0;
			} else if (args[nextopt].equals("-u")) {
				optUnknownState = true;
			} else if (args[nextopt].equals("-H")) {
				nextopt++;
				if (nextopt < args.length) {
					optArgHistSplits = args[nextopt];
				}
			} else {
				// wrong option given
				usage();
			}
		}
		if (nextopt >= args.length) {
			usage();
		}

		if (optRefDate != null) {
			refDate = processRefDate(optRefDate);
		}

		verbLevel = optVerbLevel;

		if ((optArgDelim != null) && (optArgDelim.length() > 0)){
			delim = optArgDelim.charAt(0);
		}

		if (command.equals("csvparse")) {
			handleCSVParse(args[nextopt], delim);
		} else if (command.equals("ori")) {
			handleORI(args[nextopt]);
		} else if (command.equals("combpat")) {
			handleCombinedPattern(args[nextopt]);
		} else if (command.equals("seppat")) {
			handleSeparatePattern(args[nextopt]);
		} else if (command.equals("patches")) {
			String[] fileArgs = new String[args.length-nextopt];
			System.arraycopy(args, nextopt,
					 fileArgs, 0, fileArgs.length);
			handlePatches(fileArgs, optArgHistSplits,
				      optArgDetailOutFile,
				      optUnknownState);
		} else {
			usage();
		}

	}



	public static void main(String[] args)
		throws IOException, RowReaderException {

		RFTMain rtfMain = new RFTMain(args);
	}

}

