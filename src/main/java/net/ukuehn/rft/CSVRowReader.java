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
import java.lang.StringBuilder;

import java.util.LinkedList;

import net.ukuehn.csv.*;



public class CSVRowReader extends SimpleRowReader {

	CSVParser cr;
	String[] currRow;
	//int rowNum;


	public CSVRowReader(Reader r, char sepChar, char quoteChar) {
		cr = new CSVParser(r, sepChar, quoteChar);
		currRow = null;
		//rowNum = 0;
	}


	public CSVRowReader(Reader r, char sepChar) {
		this(r, sepChar, CSVParser.defaultQuoteChar);
	}


	public CSVRowReader(Reader r) {
		this(r, CSVParser.defaultSepChar, CSVParser.defaultQuoteChar);
	}


	public boolean hasNextRow() throws RowReaderException {
		if (currRow == null) {
			try {
				currRow = cr.nextRow();
			} catch (IOException e0) {
				throw new RowReaderException(e0);
			} catch (CSVParserException e1) {
				throw new RowReaderException(e1);
			}
		}
		return (currRow != null);
	}


	public String[] nextRow() throws RowReaderException {
		if (currRow == null) {
			try {
				currRow = cr.nextRow();
			} catch (IOException e0) {
				throw new RowReaderException(e0);
			} catch (CSVParserException e1) {
				throw new RowReaderException(e1);
			}
		}
		//if (currRow != null) {
		//	rowNum += 1;
		//}
		String[] res = currRow;
		currRow = null;
		return res;
	}
		

	public int getRowNumber() {
		return cr.getRowNumber();
	}

	
}

