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


import net.ukuehn.util.*;

import java.io.*;



public class Report {

	protected int debugLevel;

	public Report() {
		debugLevel = 0;
	}


	public void setDebugLevel(int level) {
		debugLevel = level;
	}


	public String getHeaderString() {
		return "";
	}


	public void processData() throws RowReaderException {
	}


	public String getResultString() throws RowReaderException {
		return "";
	}

}
