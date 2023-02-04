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



public class RangeHistogram {

	static final double[] defaultSplit = { 0.0, 0.9, 0.98, 1.0, 1.01 };

	static final boolean STRICT = true;
	static final boolean PERMISSIVE = false;



	/*
	 * bin[i] is defined as [ splitVal[i], splitVal[i+1] )
	 */

	double[] splitVal;
	double highVal;
	long[] bin;
	int nBins;
	boolean checkFringe;


	public RangeHistogram() {
		if (splitVal.length < 2) {
			throw new IllegalArgumentException("No bins");
		}
		splitVal = defaultSplit.clone();
		nBins = splitVal.length-1;
		bin = new long[nBins];
		reset();
	}


	public RangeHistogram(double[] split) {
		if (split.length < 2) {
			throw new IllegalArgumentException("No bins defined");
		}
		for (int i = 1;  i < split.length;  i++) {
			if (split[i-1] >= split[i]) {
				throw new IllegalArgumentException(
				   "Split points must be strictly monotonic");
			}
		}
		splitVal = split.clone();
		nBins = splitVal.length-1;
		bin = new long[nBins];
		reset();

		//
		//System.err.println("RangeHistogram: "+nBins+" bins");
		//System.err.println("Split points:");
		//for (int i = 0;  i < splitVal.length;  i++) {
		//	System.err.println("  "+splitVal[i]);
		//}
	}


	public void reset() {
		for (int i = 0;  i < bin.length;  i++) {
			bin[i] = 0;
		}
	}


	public void setFringeMode(boolean doCheck) {
		checkFringe = doCheck;
	}


	public void setPermissive() {
		setFringeMode(PERMISSIVE);
	}


	public void setStrict() {
		setFringeMode(STRICT);
	}


	public void count(double x) {
		count(x, 1);
	}


	public void count(double x, long times) {

		int h, l, m;

		//System.err.println("Searching bin for "+x);

		if (checkFringe) {
			if (x < splitVal[0]) {
				throw new IllegalArgumentException(
							"Value too low");
			} else if (x >= splitVal[nBins]) {
				throw new IllegalArgumentException(
							"Value too high");
			} else if (x == Double.NaN) {
				throw new IllegalArgumentException(
					   "NaN is not a countable value");
			}
		} else {
			if (x < splitVal[0]) {
				bin[0] += times;
				return;
			} else if (x >= splitVal[nBins]) {
				bin[nBins-1] += times;
				return;
			}
		}
		//System.err.println("splitVal.length = "+splitVal.length);
		
		//if ((x >= splitVal[nBins-1]) && (x <= splitVal[nBins])) {
		//	bin[nBins-1] += 1;
		//	//System.err.println("Adding "+x+" to high bin "
		//	//		   +(nBins-1));
		//	return;
		//}

		//System.err.println("nBins = "+nBins);
		//System.err.println("splitVal.length = "+splitVal.length);

		l = 0; h = nBins+1;

		//System.err.println("l = "+l+", h = "+h);
		
		while (l < h-1) {
			m = (l+h)/2;
			//System.err.println("l = "+l+", h = "+h+", m = "+m);
			if (x >= splitVal[m]) {
				l = m;
			} else if (x < splitVal[m]) {
				h = m;
			}
		}
		//System.err.println("Adding "+x+" to bin "+l);
		bin[l] += times;
	}


	public long[] getBins() {
		long[] res = bin.clone();
		return res;
	}


	public double[] getSplitPoints() {
		return splitVal.clone();
	}


	static final double testSplit0[] = { 0.0, 1.0 };
	static final double testSplit1[] = { 0.0, 0.5, 1.0 };
	static final double testSplit2[] = { 0.0, 0.3, 0.7, 1.0, 1.01 };
	static final double testSplit3[] = { 0.0, 0.1, 0.2, 0.3, 0.4,
					     0.5, 0.8, 0.9, 1.0, 1.01 };

	public static void main(String[] args) {
	
		RangeHistogram h;

		h = new RangeHistogram(testSplit0);
		h.count(0.0);
		h.count(0.5);
		h.count(0.99);

		System.err.println();
		h = new RangeHistogram(testSplit1);
		h.count(0.0);
		h.count(0.5);
		h.count(0.99);

		System.err.println();
		h = new RangeHistogram(testSplit2);
		h.count(0.0);
		h.count(0.2);
		h.count(0.3);
		h.count(0.5);
		h.count(0.7);
		h.count(0.8);
		h.count(1.0);

		System.err.println();
		h = new RangeHistogram(testSplit3);
		h.count(0.0);
		h.count(0.1);
		h.count(0.2);
		h.count(0.45);
		h.count(0.7);
		h.count(0.8);
		h.count(1.0);

	}

}
