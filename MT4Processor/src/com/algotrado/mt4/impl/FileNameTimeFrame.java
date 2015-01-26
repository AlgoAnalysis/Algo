package com.algotrado.mt4.impl;

public class FileNameTimeFrame extends StandartTimeframe {

	
	public static String getTimeFrame(AbstractCandleBar[] candles, String fileName) {
		if (fileName.contains("240")) {
			return JapaneseCandleBar._4_HOUR;
		} else if (fileName.contains("1440")) {
			return JapaneseCandleBar._1_DAY;
		} else if (fileName.contains("60")) {
			return JapaneseCandleBar._1_HOUR;
		} else if (fileName.contains("15")) {
			return JapaneseCandleBar._15_MINUTES;
		} else if (fileName.contains("5")) {
			return JapaneseCandleBar._5_MINUTES;
		} else {
			return StandartTimeframe.getTimeFrame(candles);
		}
	}
	
	public static int getTimeFrame(String fileName) {
		if (fileName.contains("240")) {
			return 240;
		} else if (fileName.contains("1440")) {
			return 1440;
		} else if (fileName.contains("60")) {
			return 60;
		} else if (fileName.contains("15")) {
			return 15;
		} else if (fileName.contains("5")) {
			return 5;
		} else {
			return -1;
		}
	}
}
