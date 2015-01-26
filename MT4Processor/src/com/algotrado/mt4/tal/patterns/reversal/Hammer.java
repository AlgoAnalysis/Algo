package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class Hammer extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 2 && previousCandles.length > index + 1) {
			return false;
		}
		
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		if ((previousCandles[index].getLower20Bollinger() > previousCandles[index].getLow() &&
				previousCandles[index].getLower20Bollinger() < previousCandles[index].getHigh())
				|| (previousCandles[index].getLower10Bollinger() > previousCandles[index].getLow() &&
						previousCandles[index].getLower10Bollinger() < previousCandles[index].getHigh()) ) {
			
			boolean isHammerPattern = false;
			boolean didPatternGetApproved = false;
			boolean candleCreateNewLow = previousCandles[index].getLow() < previousCandles[index - 1].getLow();
			
			if (candleCreateNewLow) {		
				isHammerPattern = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getOpen() - previousCandles[index].getLow())  >= 2 * (previousCandles[index].getClose() - previousCandles[index].getOpen()) : 
							(previousCandles[index].getClose() - previousCandles[index].getLow())  >= 2 * (previousCandles[index].getOpen() - previousCandles[index].getClose());
				boolean isUpperThread10OrLessPercentOfLowerThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) * 10 <= (previousCandles[index].getOpen() - previousCandles[index].getLow()) : 
							(previousCandles[index].getHigh() - previousCandles[index].getOpen()) * 10 <= (previousCandles[index].getClose() - previousCandles[index].getLow());
							
				isHammerPattern = isHammerPattern && isUpperThread10OrLessPercentOfLowerThread;
				for (int i = 1; i < (previousCandles.length - index); i++) {
					if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
						didPatternGetApproved = true;
						break;
					} else if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
						
						break;
					}
				}
			}
			
			return isHammerPattern && didPatternGetApproved;
		}
		return false;
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return false;
	}

//	@Override
//	public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
//		if (isReversalPattern(previousCandles, index, pipsValue))
//			return previousCandles[index].getHigh() - previousCandles[index].getLow();
//		// This is not reversal pattern.
//		return -1;
//		
//	}
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return previousCandles[index].getHigh();
	}
	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return previousCandles[index].getLow();
	}
	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return isBullishReversalPattern(previousCandles, index, pipsValue) ? 
				previousCandles[index].getHigh() : previousCandles[index].getLow();
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 2;
	}

}
