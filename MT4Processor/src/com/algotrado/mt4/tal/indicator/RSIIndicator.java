package com.algotrado.mt4.tal.indicator;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;


public class RSIIndicator extends Indicator {

	public static boolean isBullishRSI(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index >= 5) {
			return false;
		}
		if (previousCandles.length < index + 1 && index >= 5) {
			throw new RuntimeException("Array is out of bounds: " + previousCandles + " size " + previousCandles.length + " index " + index);
		}
		if (previousCandles[index].getRsi() > 20 && previousCandles[index].getRsi() < 65) {
			if (previousCandles[index].getRsi() > 35) {
				return true;
			} else {
				double rsiAverage = 0;
				for (int i = 5; i > 0; i--) {
					rsiAverage += previousCandles[index - i].getRsi();
				}
				rsiAverage /= 5;
				return previousCandles[index].getRsi() > rsiAverage;
			}
		} else {
			return false;
		}
	}
	
	public static boolean isBearishRSI(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 5) {
			return false;
		}
		if (previousCandles.length < index + 1 && index >= 5) {
			throw new RuntimeException("Array is out of bounds: " + previousCandles + " size " + previousCandles.length + " index " + index);
		}
		if (previousCandles[index].getRsi() > 30 && previousCandles[index].getRsi() < 80) {
			if (previousCandles[index].getRsi() < 45) {
				return true;
			} else {
				double rsiAverage = 0;
				for (int i = 5; i > 0; i--) {
					rsiAverage += previousCandles[index - i].getRsi();
				}
				rsiAverage /= 5;
				return previousCandles[index].getRsi() < rsiAverage;
			}
		} else {
			return false;
		}
		
	}
}
