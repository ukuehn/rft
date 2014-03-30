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


import java.io.*;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

import net.ukuehn.util.*;



public class XMLPatchCounter extends DefaultHandler {

	private final String DELIM = "\"";

	String currComputerName;
	StateCounter sc;
	RangeHistogram hist;
	PrintWriter outDetails;
	PrintWriter outSummary;
	int debug;


	public XMLPatchCounter(PrintWriter outWr,
			       StateCounter theSC,
			       RangeHistogram theHist) {
		outDetails = outWr;
		outSummary = outWr;
		sc = theSC;
		hist = theHist;
	}


	public XMLPatchCounter(PrintWriter outWr,
			       StateCounter theSC) {
		this(outWr, theSC, null);
	}


	public XMLPatchCounter(PrintWriter psDetails,
			       PrintWriter psSummary,
			       StateCounter theSC,
			       RangeHistogram theHist) {
		outDetails = psDetails;
		outSummary = psSummary;
		sc = theSC;
		hist = theHist;
	}


	public XMLPatchCounter(PrintWriter psDetails,
			       PrintWriter psSummary,
			       StateCounter theSC) {
		this(psDetails, psSummary, theSC, null);
	}


	public void setDebugLevel(int level) {
		debug = level;
	}


	protected int getIndex(Attributes attr, String aName) {
		for (int i = 0;  i < attr.getLength();  i++) {
			if (attr.getLocalName(i).equals(aName)) {
				return i;
			}
		}
		return -1;
	}


	public void startDocument() throws SAXException {
		// ok, nothing to do here
	}


	public void endDocument() throws SAXException {
		// Nothing to do here
	}


	public void startElement(String nsURI,
				 String locName,
				 String qualName,
				 Attributes attr)
		throws SAXException {

		if (debug > 0) {
			System.err.println("startElement("+nsURI+", "
					   +locName+", "+qualName+")");
		}
		if (qualName.equals("Computer")) {
			int idx = getIndex(attr, "Name");
			if (idx >= 0) {
				if (debug > 0) {
					System.err.println("Processing "
						      +attr.getValue(idx));
				}
				currComputerName = attr.getValue(idx);
				sc.resetCounts();
			} else {
				throw new SAXException("No `Name' "
						       +"attribute found.");
			}
		}

		if (qualName.equals("Update")) {
			int idx = getIndex(attr, "Status");
			if (idx >= 0) {
				if (debug > 0) {
					System.err.println("  "
						    +attr.getValue(idx));
				}
				String status = attr.getValue(idx);
				try {
					sc.updateStatusCount(status);
				} catch (IllegalArgumentException e) {
					System.err.println(e.getMessage());
				}
			} else {
				throw new SAXException("No `Status' "
						       +"attribute found.");
			}
		}
	}


	public void endElement(String nsURI,
			       String locName,
			       String qualName)
		throws SAXException {

		if (debug > 0) {
			System.err.println("EndElement("+nsURI+", "
					   +locName+", "+qualName+")");
		}
		if (qualName.equals("Computer")) {
			double d = sc.getInstPercentage();
			if (debug > 0) {
				System.err.println("Got inst "
						   +"percentage "
						   +String.valueOf(d));
			}
			if (hist != null) {
				hist.count(d);
			}
			printResultsCSV(currComputerName, sc.toStrings());
		}
	}


	public void characters(char buf[], int offset, int len)
		throws SAXException {
		// nothing to do here
	}


	public void printResultsCSV(String first,
				    String[] data) {
		StringBuilder sb = new StringBuilder();

		sb.append(DELIM);
		sb.append(first);
		sb.append(DELIM);
		for (int i = 0;  i < data.length;  i++) {
			sb.append(";");
			//sb.append(DELIM);
			sb.append(data[i]);
			//sb.append(DELIM);
		}
		if (outDetails != null) {
			outDetails.println(sb.toString());
		}
		outDetails.flush();
	}


	public void printHistogram() {
		long[] bin = null;
		double[] split = null;

		if (hist == null) {
			return;
		}
		bin = hist.getBins();
		split = hist.getSplitPoints();
		if (split.length != bin.length+1) {
			return;
		}

		if (outSummary != null) {
			outSummary.println(DELIM+"Low"+DELIM+";"
					   +DELIM+"High"+DELIM+";"
					   +DELIM+"Count"+DELIM);
			for (int i = 0;  i < bin.length;  i++) {
				outSummary.println(""+split[i]+";"
						   +split[i+1]+";"
						   +bin[i]);
			}
		}
		outSummary.flush();
	}


}

