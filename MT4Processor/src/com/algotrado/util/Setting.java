package com.algotrado.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Setting {
	private static String assetDirectoryPath = "C:\\Algo\\Asset History Date";
	private static String dateTimeFormat = "dd/MM/yyyy,HH:mm:ss";
	private static TimeZone fileTimeZone = TimeZone.getTimeZone("IST");
	
	public static String getDateTimeFormat(Date date) {
		SimpleDateFormat dateformatter = new SimpleDateFormat(dateTimeFormat);
//		dateformatter.setTimeZone(TimeZone.getTimeZone("IST"));
		return dateformatter.format(date);
	}

	public static String getDateTimeHeader(String addString) {
		return addString + "Date," + addString + "Time";
	}

	static public String getAssetDirectoryPath() {
		return assetDirectoryPath;
	}

	public static TimeZone getFileTimeZone() {
		return fileTimeZone;
	}
	
}
