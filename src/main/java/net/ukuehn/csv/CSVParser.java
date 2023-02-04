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


package net.ukuehn.csv;


import java.io.*;
import java.lang.StringBuilder;

import java.util.LinkedList;



public class CSVParser {

	BufferedReader br;

	char sepChar;
	char quoteChar;
	boolean quoted;
	int rowNum;
	boolean crSeen;
	boolean eof;


	public static final char defaultQuoteChar = '"';
	public static final char defaultSepChar = ',';


	public CSVParser(Reader r, char sepChar, char quoteChar) {
		this.sepChar = sepChar;
		this.quoteChar = quoteChar;
		rowNum = 0;
		quoted = false;
		crSeen = false;
		eof = false;
		br = new BufferedReader(r);
	}

	public CSVParser(Reader r, char sepChar) {
		this(r, sepChar, defaultQuoteChar);
	}

	public CSVParser(Reader r) {
		this(r, defaultSepChar, defaultQuoteChar);
	}


	public String[] nextRow()
		throws IOException, CSVParserException {

		LinkedList<String> recs = new LinkedList<String>();
		StringBuilder sb = new StringBuilder(10);
		boolean done;
		boolean elemDone;
		int charsInRow;
		char ch;
		int res;

		if (eof) {
			return null;
		}
		quoted = false;
		done = false;
		elemDone = false;
		charsInRow = 0;
		while (!done) {
			res = br.read();
			if (res < 0) {
				eof = true;
				if (quoted) {
					// hmm, got an structural error
					throw new
						CSVParserException(
						     "End of input in quoted "
						     +"element");
				} else {
					if (charsInRow > 0) {
						recs.add(sb.toString());
					}
				}
				break;
			}
			ch = (char)res;
			charsInRow += 1;

			//System.err.println("Read '"
			//		   +ch
			//		   +"' quoted = "
			//		   +quoted);

			if (quoted) {
				if (ch == quoteChar) {
					// do not add quote char
					// to record
					elemDone = true;
					quoted = false;
				} else {
					sb.append(ch);
				}
			} else {
				if (ch == '\r') {
					crSeen = true;
					recs.add(sb.toString());
					done = true;
				} else 	if (ch == '\n') {
					if (!crSeen) {
						// OK, row is done
						recs.add(sb.toString());
						done = true;
					} else {
						// if previous row ended
						// with \r\n, here we have
						// encountered the \n, so
						// skip it silently
						crSeen = false;
						charsInRow -= 1;
					}
				} else if (ch == sepChar) {
					recs.add(sb.toString());
					sb = new StringBuilder(10);
				} else if (ch == quoteChar) {
					quoted = true;
				} else {
					sb.append(ch);
				}
			}
		}
		if (eof && (recs.size() == 0)) {
			return null;
		} else {
			return recs.toArray(new String[0]);
		}
	}
		

	public int getRowNumber() {
		return rowNum;
	}

	
}

