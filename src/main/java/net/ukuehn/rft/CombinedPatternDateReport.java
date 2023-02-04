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

/*
 * Handle data where a pattern and a date are given in in the same
 * column (here: 11) in CSV format. The report is created over the age
 * of the pattern relative to a given date of reference.
 */


package net.ukuehn.rft;


import net.ukuehn.csv.*;
import net.ukuehn.util.*;
import java.io.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CombinedPatternDateReport extends RelativeDateReport {

	public static final char delim = ';';
	public static final char quote = '"';

	public static final double[] repSplits = {
		0.0, 1.0, 2.0, 3.0, 10000.0
	};
	public static final int patternColumn = 11;

	public CombinedPatternDateReport(String fname, Date ref)
		throws IOException, RowReaderException {

		SimpleRowReader sr = new CSVRowReader(new FileReader(fname),
						      delim, quote);
		StringConverter sc = new PDateStringConverter();
		super.init(sr, sc, patternColumn, ref, repSplits, true);
	}


}
