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

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RelativeDateReport extends Report {

	protected SimpleRowReader srp;
	protected DiscreteHistogram h;
	protected RangeHistogram rh;
	protected Date refDate;
	protected StringConverter sc;
	protected boolean skipHead;
	protected int col;


	protected RelativeDateReport() {
	}


	public RelativeDateReport(SimpleRowReader r,
				  StringConverter se,
				  int columnOfInterest,
				  Date ref,
				  double[] histSplitPoints,
				  boolean skipHeadline) {
		init(r, se, columnOfInterest, ref,
		     histSplitPoints, skipHeadline);
	}


	protected void init(SimpleRowReader r,
				  StringConverter se,
				  int columnOfInterest,
				  Date ref,
				  double[] histSplitPoints,
				  boolean skipHeadline) {
		srp = r;
		refDate = ref;
		sc = se;
		rh = new RangeHistogram(histSplitPoints);
		h = new DiscreteHistogram();
		skipHead = skipHeadline;
		col = columnOfInterest;
	}


	public void setInput(SimpleRowReader r) {
		srp = r;
		h = new DiscreteHistogram();
	}


	public void setDebugLevel(int level) {
		debugLevel = level;
		srp.setDebugLevel(debugLevel);
		sc.setDebugLevel(debugLevel);
	}


	protected int dbgIdx[] = { 0, 7, 9 };
	protected void debugOutput(PrintStream out, String[] row) {
		if (row.length < col) {
			return;
		}
		out.print("Input: ");
		for (int i = 0;  i < dbgIdx.length;  i++) {
			if (dbgIdx[i] < row.length) {
				out.print("\""+row[dbgIdx[i]]+"\";");
			}
		}
		out.println("...");
	}


	public void processData() throws RowReaderException {

		String[] row;
		double val;

		// skip first row of headers
		if (skipHead) {
			row = srp.nextRow();
			if ((row == null) || (row.length < col)) {
				return;
			}
		}
		//for (row = srp.nextRow();
		//     row != null;  row = srp.nextRow()) {
		while (srp.hasNextRow()) {
			row = srp.nextRow();
			if (debugLevel > 2) {
				debugOutput(System.err, row);
			}
			if (row.length < col) {
				continue;
			}
			String sIn = row[col];
			String res = sc.convert(sIn);
			if (debugLevel > 2) {
				System.err.println("RelativeDateReport"
						   +".processData counting "
						   +res);
			}
			h.count(res);
		}

		// Now we have all the data extracted and a discrete histogram
		// of it, 
		processIntermediateHistogram();
	}


	protected final SimpleDateFormat patternDateFmt
		= new SimpleDateFormat("yyyyMMdd");

	protected void processIntermediateHistogram() {
		String[] dates = h.getKeys();
		long[] count = h.getCounts(dates);
		long[] age = new long[dates.length];

		if (dates.length == 0) {
			age[0] = 0;
		} else {
			if (refDate == null) {
				refDate = 
					patternDateFmt.parse(
						 dates[dates.length-1],
					         new ParsePosition(0));
			}
			for (int i = 0;  i < dates.length;  i++) {
				Date d = patternDateFmt.parse(dates[i],
							new ParsePosition(0));
				age[i] = dateDiffDays(d, refDate);
			}
		}
		if (debugLevel > 0) {
			System.err.println("Got "
					   +dates.length
					   +" different pattern dates");
			for (int i = 0;  i < dates.length;  i++) {
				System.err.println(dates[i]+";"
						   +age[i]+";"
						   +count[i]);
			}
		}

		for (int i = 0;  i < dates.length;  i++) {
			rh.count(age[i], count[i]);
		}
	}


	protected long dateDiffDays(Date earlier, Date later) {
		long t0, t1;

		t0 = earlier.getTime();
		t1 = later.getTime();

		return (long)Math.floor((double)(t1-t0)/86400/1000);
	}


	public String getHeaderString() {
		StringBuilder sb = new StringBuilder(50);
		double[] split = rh.getSplitPoints();
		
		for (int k = 0;  k < split.length-1;  k++) {
			sb.append("\""+split[k]
				  +"<=x<"
				  +split[k+1]+"\";");
		}
		return sb.toString();
	}


	public String getResultString() {
		StringBuilder sb = new StringBuilder(50);

		long count[] = rh.getBins();

		for (int k = 0;  k < count.length;  k++) {
			sb.append(count[k]);
			if (k < count.length-1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

}
