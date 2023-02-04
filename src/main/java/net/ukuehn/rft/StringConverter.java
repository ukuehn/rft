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



/* Extract data from a string and return it as another string.
 * This is an identity transform, subclass it to do specific things
 * like extract dates from specific formats etc.
 */


package net.ukuehn.rft;

import java.text.SimpleDateFormat;


public class StringConverter {

	protected int debugLevel = 0;
	public static final SimpleDateFormat internalFmt
		= new SimpleDateFormat("yyyyMMdd");


	public StringConverter() {
	}


	public void setDebugLevel(int level) {
		debugLevel = level;
	}


	public String convert(String s) {
		if (debugLevel > 0) {
			
		}
		return s;
	}


}
