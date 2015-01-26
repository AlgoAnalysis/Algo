package com.algotrado.util;

import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;

public class PriceUtil {

	public static double getMinLow(List<JapaneseCandleBar> candles) {
		if (candles == null || candles.isEmpty()) {
			return -1;
		}
		double min = candles.get(0).getHigh();
		for(JapaneseCandleBar candle : candles) {
			if (candle.getLow() < min) {
				min = candle.getLow();
			}
		}
		return min;
	}
	
	public static double getMaxHigh(List<JapaneseCandleBar> candles) {
		if (candles == null || candles.isEmpty()) {
			return -1;
		}
		double max = candles.get(0).getLow();
		for(JapaneseCandleBar candle : candles) {
			if (candle.getHigh() > max) {
				max = candle.getHigh();
			}
		}
		return max;
	}
	
}
