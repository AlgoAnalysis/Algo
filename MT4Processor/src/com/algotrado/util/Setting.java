package com.algotrado.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.ib.controller.NewContract;
import com.ib.controller.Types.SecType;

public class Setting {
	private static String assetDirectoryPath = "C:\\Algo\\Asset History Date";
	private static String dateTimeFormat = "dd/MM/yyyy,HH:mm:ss";
	private static TimeZone fileTimeZone = TimeZone.getTimeZone("UTC");
	
	private static double usOilTopSpread = 0.01;
	private static double usOilBottomSpread = 0.01;
	private static boolean needToPrint = true;
	
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
	
	
	public static void errShow(String err)
	{
		if(needToPrint)
			System.err.println(err);
	}
	
	
	public static void outShow(String out)
	{
		if(needToPrint)
			System.out.println(out);
	}
	
	public static NewContract getOilContractDetails() {
		NewContract qmContract = new NewContract();
		qmContract.currency("USD");
		qmContract.exchange("NYMEX");
		//	qmContract.primaryExch("ISLAND");
		qmContract.secType(SecType.FUT);
		qmContract.symbol("QM");
		qmContract.localSymbol("QMU5");
		qmContract.tradingClass("QM");
		String contractExpireDate = "20150921 12:00:00";
		qmContract.expiry(contractExpireDate);
		qmContract.multiplier("500");
		return qmContract;
	}
}
