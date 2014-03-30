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


package net.ukuehn.util;


import java.lang.IllegalArgumentException;
import java.util.TreeMap;
import java.util.Set;
import java.util.Iterator;


public class DiscreteHistogram {

	TreeMap<String,Long> map;
	long total;


	public DiscreteHistogram() {
		reset();
	}


	public void reset() {
		map = new TreeMap<String,Long>();
		total = 0;
	}


	public void count(String s) {

		Long val = map.get(s);

		if (val == null) {
			// No such key s so far in map, so
			// insert it with count 1
			val = new Long(1);
			map.put(s, val);
		} else {
			val = new Long(val.longValue()+1);
			map.put(s, val);
		}
	}


	public String[] getKeys() {
		Set<String> keySet = map.keySet();
		Iterator<String> it;
		int i;

		String[] res = new String[keySet.size()];
		for (i = 0,it = keySet.iterator();  it.hasNext();  i++) {
			res[i] = it.next();
		}
		return res;
	}


	public long[] getCounts(String[] keys) {
		long[] res;
		Long val;
		try {
			res = new long[keys.length];
			for (int i = 0;  i < keys.length;  i++) {
				val = map.get(keys[i]);
				if (val != null) {
					res[i] = val.longValue();
				} else {
					res[i] = 0;
				}
			}
			return res;
		} catch (NullPointerException e) {
			return null;
		}
	}

}
