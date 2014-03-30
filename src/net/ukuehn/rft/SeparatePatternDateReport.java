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


public class SeparatePatternDateReport extends RelativeDateReport {

	public static final char delim = ';';
	public static final char quote = '"';

	public static final double[] repSplits = {
		0.0, 1.0, 5.0, 10.0, 100000.0
	};
	public static final int patternColumn = 7;
	public static final int dateColumn = 9;
	public static final SimpleDateFormat dateFmt =
		new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat patternFmt =
		new SimpleDateFormat("yyyy-MM-dd");
	public static final int maxCountAge = 32;


	SimpleRowReader sprFilter;
	SimpleRowReader sprSr;


	public SeparatePatternDateReport(String fname, Date ref)
		throws IOException, RowReaderException {

		sprSr = new CSVRowReader(new FileReader(fname),
						      delim, quote);
		sprFilter = new DateFilteredRowReader(sprSr, ref, maxCountAge,
						    dateColumn, dateFmt);
		StringConverter sc = new DateStringConverter(patternFmt);
		super.init(sprFilter, sc, patternColumn, ref, repSplits, true);
	}


	public void setDebugLevel(int level) {
		debugLevel = level;
		sprFilter.setDebugLevel(level);
		sprSr.setDebugLevel(level);
	}		

}
