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


/* Extract a date from a format xxxx (yyyyMMdd) and return it in
 * form yyyyMMdd
 */


package net.ukuehn.rft;


import net.ukuehn.csv.*;
import net.ukuehn.util.*;
import java.io.*;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PDateStringConverter extends StringConverter {

	static protected final SimpleDateFormat patternDateFmt
		= new SimpleDateFormat("yyyyMMdd");
	protected int debugLevel = 0;

	public PDateStringConverter() {
	}

	public void setDebugLevel(int level) {
		debugLevel = level;
	}

	protected Date extractDateFromPar(String s) {
		int openPar;
		Date date = null;
		try {
			openPar = s.indexOf("(");
			date = patternDateFmt.parse(s,
				       new ParsePosition(openPar+1));
		} catch (NullPointerException e) {
			// ignore
		}
		return date;
	}

	public String convert(String s) {
		Date date = extractDateFromPar(s);
		String res = internalFmt.format(date);
		return res;
	}

}
