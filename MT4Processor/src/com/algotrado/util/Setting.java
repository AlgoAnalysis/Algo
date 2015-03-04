package com.algotrado.util;

public class Setting {
	private static String assetDirectoryPath = "C:\\Algo\\Asset History Date";
	private static String dateTimeFormat = "dd/MM/yyyy,HH:mm:ss";
	
	public static String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public static String getDateTimeHeder(String addString) {
		return addString + "Date," + addString + "Time";
	}

	static public String getAssetDirectoryPath(String addString) {
		return assetDirectoryPath;
	}
}
