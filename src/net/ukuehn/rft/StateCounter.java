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


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;


public class StateCounter {

	/*
	 * Update state according to
	 * http://msdn.microsoft.com/en-us/library/microsoft.updateservices.administration.updateinstallationstate%28v=vs.85%29.aspx
	 */

	protected final static String[] headerNames = {
		"Downloaded", "Failed", "Installed",
		"InstalledPendingReboot", "NotInstalled",
		"Unknown", "Total", "PercentageInstalled"
	};

	protected final int idxUnknown = 5;
	protected final String unknownNotCounted = "UnknownNotCounted";

	String currComputerName;
	int cDownloaded;
	int cFailed;
	int cInstalled;
	int cInstalledPendingReboot;
	int cNotInstalled;
	int cUnknown;
	int cNotApplicable;

	int totalDownloaded;
	int totalFailed;
	int totalInstalled;
	int totalInstalledPendingReboot;
	int totalNotInstalled;
	int totalUnknown;
	int totalNotApplicable;

	boolean includeUnknown;


	public void StateConter() {
		totalDownloaded = 0;
		totalFailed = 0;
		totalInstalled = 0;
		totalInstalledPendingReboot = 0;
		totalNotInstalled = 0;
		totalUnknown = 0;
		totalNotApplicable = 0;
		includeUnknown = false;
		resetCounts();
	}


	public void resetCounts() {
		cDownloaded = 0;
		cFailed = 0;
		cInstalled = 0;
		cInstalledPendingReboot = 0;
		cNotInstalled = 0;
		cUnknown = 0;
		cNotApplicable = 0;
	}


	public void setIncludeUnknown(boolean parm) {
		includeUnknown = parm;
	}


	public void updateStatusCount(String stateName)
		throws IllegalArgumentException {

		if (stateName.equals("Downloaded")) {
			cDownloaded += 1;
			totalDownloaded += 1;
		} else if (stateName.equals("Failed")) {
			cFailed += 1;
			totalFailed += 1;
		} else if (stateName.equals("Installed")) {
			cInstalled += 1;
			totalInstalled += 1;
		} else if (stateName.equals("InstalledPendingReboot")) {
			cInstalledPendingReboot += 1;
			totalInstalledPendingReboot += 1;
		} else if (stateName.equals("NotInstalled")) {
			cNotInstalled += 1;
			totalNotInstalled += 1;
		} else if (stateName.equals("Unknown")) {
			cUnknown += 1;
			if (includeUnknown) {
				totalUnknown += 1;
			}
		} else if (stateName.equals("NotApplicable")) {
			cNotApplicable += 1;
			totalNotApplicable += 1;
		} else {
			throw new IllegalArgumentException("Unknown status "
					   +"`"+stateName+"'");
		}
	}


	public String[] headerToStrings() {
		String[] res = headerNames.clone();
		if (!includeUnknown) {
			res[idxUnknown] = unknownNotCounted;
		}
		return res;
	}


	public int[] getValues() {
		int[] res = new int[8];
		res[0] = cDownloaded;
		res[1] = cFailed;
		res[2] = cInstalled;
		res[3] = cInstalledPendingReboot;
		res[4] = cNotInstalled;
		res[5] = cUnknown;
		return res;
	}


	public double getInstPercentage() {
		int total = cDownloaded+cInstalled+cInstalledPendingReboot
			+cNotInstalled+cFailed; // dont include cUnknown
		int instCount = cInstalled+cInstalledPendingReboot;
		double instPercentage = 0.0;
		if (includeUnknown) {
			total += cUnknown;
		}
		if (total > 0) {
			instPercentage = ((double)instCount)/total;
		}
		return instPercentage;
	}


	public String[] toStrings() {
		int total = cDownloaded+cInstalled+cInstalledPendingReboot
			+cNotInstalled+cFailed; // dont include cUnknown
		int instCount = cInstalled+cInstalledPendingReboot;
		double instPercentage = 0.0;
		if (includeUnknown) {
			total += cUnknown;
		}
		if (total > 0) {	
			instPercentage = ((double)instCount)/total;
		}
		String[] res = new String[8];
		res[0] = String.valueOf(cDownloaded);
		res[1] = String.valueOf(cFailed);
		res[2] = String.valueOf(cInstalled);
		res[3] = String.valueOf(cInstalledPendingReboot);
		res[4] = String.valueOf(cNotInstalled);
		res[5] = String.valueOf(cUnknown);
		res[6] = String.valueOf(total);
		res[7] = String.valueOf(instPercentage);
		return res;
	}


	public String[] totalToStrings() {
		int grandTotal
			= totalDownloaded + totalFailed + totalInstalled
			+ totalInstalledPendingReboot + totalNotInstalled
			+ totalUnknown;
		int instTotal = totalInstalled+totalInstalledPendingReboot;
		double instPercentage = 0.0;
		if (grandTotal > 0) {
			instPercentage = ((double)instTotal)/grandTotal;
		}
		String[] res;
		if (totalNotApplicable > 0) {
			res = new String[9];
			res[8] = String.valueOf(totalNotApplicable);
		} else {
			res = new String[8];
		}
		res[0] = String.valueOf(totalDownloaded);
		res[1] = String.valueOf(totalFailed);
		res[2] = String.valueOf(totalInstalled);
		res[3] = String.valueOf(totalInstalledPendingReboot);
		res[4] = String.valueOf(totalNotInstalled);
		res[5] = String.valueOf(totalUnknown);
		res[6] = String.valueOf(grandTotal);
		res[7] = String.valueOf(instPercentage);
		return res;
	}


}

