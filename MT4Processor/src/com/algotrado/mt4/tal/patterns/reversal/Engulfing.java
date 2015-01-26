package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class Engulfing extends Pattern {
	@Override
	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
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
			
			boolean firstCandleRedWithSmallBody = (previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen()) ?
					(((previousCandles[index - 1].getHigh() - previousCandles[index - 1].getOpen()) +  (previousCandles[index - 1].getClose() - previousCandles[index - 1].getLow())) >= (previousCandles[index - 1].getOpen() - previousCandles[index - 1].getClose())) : false;
			
					
			boolean isEngulfingPattern = false;
			boolean didPatternGetApproved = false;
			boolean secondCandleCreateNewLow = previousCandles[index].getLow() < previousCandles[index - 1].getLow();
			boolean secondCandleCreateNewHigh = previousCandles[index].getHigh() > previousCandles[index - 1].getHigh();
			
			if (firstCandleRedWithSmallBody && secondCandleCreateNewLow && secondCandleCreateNewHigh) {		
				isEngulfingPattern = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose())  +  (previousCandles[index].getOpen() - previousCandles[index].getLow()) < (previousCandles[index].getClose() - previousCandles[index].getOpen()) : false;
						
				for (int i = 1; i < (previousCandles.length - index); i++) {
					if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
						didPatternGetApproved = true;
						break;
					} else if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
	//					didPatternGetApproved = false;
						break;
					}
				}
			}
			
			return isEngulfingPattern && didPatternGetApproved;
		}
		return false;
	}
	@Override
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 2 && previousCandles.length > index + 1) {
			return false;
		}
		
		boolean firstCandleGreenWithSmallBody = (previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen()) ?
				(((previousCandles[index - 1].getHigh() - previousCandles[index - 1].getClose()) +  (previousCandles[index - 1].getOpen() - previousCandles[index - 1].getLow())) >= (previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen())) : false;
		
		boolean secondCandleCreateNewLow = previousCandles[index].getLow() < previousCandles[index - 1].getLow();
		boolean secondCandleCreateNewHigh = previousCandles[index].getHigh() > previousCandles[index - 1].getHigh();
				
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		boolean isEngulfingPattern = false;
		boolean didPatternGetApproved = false;
		if (firstCandleGreenWithSmallBody && secondCandleCreateNewLow && secondCandleCreateNewHigh) {
			if ((previousCandles[index].getHigher20Bollinger() > previousCandles[index].getLow() &&
					previousCandles[index].getHigher20Bollinger() < previousCandles[index].getHigh())
					|| (previousCandles[index].getHigher10Bollinger() > previousCandles[index].getLow() &&
							previousCandles[index].getHigher10Bollinger() < previousCandles[index].getHigh())) {
				isEngulfingPattern = (previousCandles[index].getClose() < previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getOpen())  +  (previousCandles[index].getClose() - previousCandles[index].getLow()) < (previousCandles[index].getOpen() - previousCandles[index].getClose()) : false;
				
				for (int i = 1; i < (previousCandles.length - index); i++) {
					if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
						didPatternGetApproved = true;
						break;
					} else if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
						//				didPatternGetApproved = false;
						break;
					}
				}
			}
			
		}
		
		return isEngulfingPattern && didPatternGetApproved;
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
