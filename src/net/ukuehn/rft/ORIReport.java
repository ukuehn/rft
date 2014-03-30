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



public class ORIReport extends Report {

	public static final char defaultDelim = ';';
	public static double defaultSplits[] = {
		0.0,            // base value, oris are >= 0
		100.0,          // good if below this value
		250.0,          // OK if below this value
		500.0,          // Acceptable if below this value
		100000.0        // artifical upper bound
	};

	protected double[] split;
	protected RangeHistogram h;
	protected SimpleRowReader srp;


	public ORIReport(String fname, char delim)
		throws IOException, RowReaderException {
		srp = new CSVRowReader(new FileReader(fname), delim);
		split = defaultSplits;
		h = new RangeHistogram(split);
	}


	public ORIReport(String fname)
		throws IOException, RowReaderException {
		this(fname, defaultDelim);
	}


	public void processData()
		throws RowReaderException {

		String[] row;
		double val;

		while (srp.hasNextRow()) {
			row = srp.nextRow();
			//System.err.println("Row has "+row.length+" elems");
			if (row.length < 3) {
				continue;
			}

			//for (int i = 0;  i < row.length;  i++) {
			//	System.err.println("  "+row[i]);
			//}
			try {
				val = Double.valueOf(row[2]);
			} catch (NumberFormatException e) {
				// ignore cases where no parseable ORI value
				// is given...
				continue;
			}
			//System.err.println("Got value "+val);
			h.count(val);
		}
	}

	
	public String getHeaderString() {
		StringBuilder sb = new StringBuilder(50);
		for (int i = 1;  i < split.length;  i++) {
			sb.append("\"<"+split[i]+"\"");
			if (i < split.length-1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

	
	public String getResultString() throws RowReaderException {
		StringBuilder sb = new StringBuilder(50);
		long[] bin = h.getBins();
		for (int i = 0;  i < bin.length;  i++) {
			sb.append(bin[i]);
			if (i < bin.length-1) {
				sb.append(";");
			}
		}
		return sb.toString();
	}

}
