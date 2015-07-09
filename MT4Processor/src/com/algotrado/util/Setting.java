package com.algotrado.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Setting {
	private static String assetDirectoryPath = "C:\\Algo\\Asset History Date";
	private static String dateTimeFormat = "dd/MM/yyyy,HH:mm:ss";
	private static TimeZone fileTimeZone = TimeZone.getTimeZone("UTC");
	
	private static double usOilBrokerSpread = 0.05;
	private static double usOilTopSpread = 0.01;
	private static double usOilBottomSpread = 0.01;
	
	private static boolean printMessageInConsole = false;
	
	public static String getDateTimeFormat(Date date) {
		SimpleDateFormat dateformatter = new SimpleDateFormat(dateTimeFormat);
		dateformatter.setTimeZone(TimeZone.getTimeZone("UTC"));
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

	public static double getUsOilBrokerSpread() {
		return usOilBrokerSpread;
	}

	public static double getUsOilTopSpread() {
		return usOilTopSpread;
	}

	public static double getUsOilBottomSpread() {
		return usOilBottomSpread;
	}

	public static boolean isPrintMessageInConsole() {
		return printMessageInConsole;
	}

	public static void setPrintMessageInConsole(boolean printMessageInConsole) {
		Setting.printMessageInConsole = printMessageInConsole;
	}
	
	
}
