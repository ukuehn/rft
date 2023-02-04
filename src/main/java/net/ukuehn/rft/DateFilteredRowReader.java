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
import java.text.SimpleDateFormat;
import java.util.Date;



public class DateFilteredRowReader extends SimpleRowReader {

	public final static SimpleDateFormat defaultFmt
		= new SimpleDateFormat("yyyy-MM-dd");

	SimpleRowReader chain;
	String[] currRow;
	int rowNum;
	
	Date refDate;
	int maxAge;
	SimpleDateFormat dateFmt;
	int col;

	int debugLevel;


	public DateFilteredRowReader(SimpleRowReader sr,
				     Date ref, int maxAge,
				     int colOfInterest,
				     SimpleDateFormat fmt) {
		chain = sr;
		currRow = null;
		rowNum = 0;
		refDate = ref;
		this.maxAge = maxAge;
		col = colOfInterest;
		if (fmt == null) {
			dateFmt = defaultFmt;
		} else {
			dateFmt = fmt;
		}
		debugLevel = 0;
	}


	public void setDebugLevel(int level) {
		debugLevel = level;
	}


	protected long dateDiffDays(Date earlier, Date later) {
		long t0, t1;

		t0 = earlier.getTime();
		t1 = later.getTime();

		return (long)Math.floor((double)(t1-t0)/86400/1000);
	}
	

	protected boolean passesFilter() {
		if (currRow == null) {
			if (debugLevel > 3) {
				System.err.println("Filtering date-> "
						   +"no data");
			}
			return false;
		}
		if (currRow.length < col) {
			if (debugLevel > 3) {
				System.err.println("Filtering date-> "
						   +"not enough data");
			}
			return false;
		}

		Date d = null;
		long age = 0;
		try {
			if (debugLevel > 3) {
				System.err.println("Filtering date on '"
						   +currRow[col]+"'");
			}
			d = dateFmt.parse(currRow[col]);
			age = dateDiffDays(d, refDate);
		} catch (Exception e) {
			// ignore
			if (debugLevel > 3) {
				System.err.println("Filtering date -> "
						   +"exception");
			}
			return false;
		}
		if (debugLevel > 0) {
			System.err.println("Filtering date "
					   +currRow[col]
					   +" -> age "+age
					   +" -> "+((age<maxAge)?"OK":"out"));
		}
		return (age < maxAge);
	}


	public boolean hasNextRow() throws RowReaderException {
		if (chain == null) {
			return false;
		}
		while (currRow == null) {
			if (!chain.hasNextRow()) {
				return false;
			}
			currRow = chain.nextRow();

			// Now do the filtering
			if (!passesFilter()) {
				// throw away, does not fit
				currRow = null;
			}
		}
		return (currRow != null);
	}


	public String[] nextRow() throws RowReaderException {
		if (chain == null) {
			return null;
		}
		if (currRow == null) {
			if (hasNextRow()) {
				rowNum += 1;
				return currRow;
			} else {
				return null;
			}
		} else {
			if (currRow != null) {
				rowNum += 1;
			}
			String[] res = currRow;
			currRow = null;
			return res;
		}
	}


	public int getRowNumber() {
		if (chain == null) {
			return 0;
		}
		return rowNum;
	}


}
