package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class ShootingStar extends Pattern {

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 2 && previousCandles.length > index + 1) {
			return false;
		}
		
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		if ((previousCandles[index].getHigher20Bollinger() > previousCandles[index].getLow() &&
				previousCandles[index].getHigher20Bollinger() < previousCandles[index].getHigh())
				|| (previousCandles[index].getHigher10Bollinger() > previousCandles[index].getLow() &&
						previousCandles[index].getHigher10Bollinger() < previousCandles[index].getHigh())) {
			
			boolean isShootingStarPattern = false;
			boolean didPatternGetApproved = false;
			boolean candleCreateNewHigh = previousCandles[index].getHigh() > previousCandles[index - 1].getHigh();
			
			if (candleCreateNewHigh) {		
				isShootingStarPattern = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose())  >= 2 * (previousCandles[index].getClose() - previousCandles[index].getOpen()) : 
							(previousCandles[index].getHigh() - previousCandles[index].getOpen())  >= 2 * (previousCandles[index].getOpen() - previousCandles[index].getClose());
				boolean isLowerThread10OrLessPercentOfUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose())  >= 10 * (previousCandles[index].getOpen() - previousCandles[index].getLow()) : 
							(previousCandles[index].getHigh() - previousCandles[index].getOpen())  >= 10 * (previousCandles[index].getClose() - previousCandles[index].getLow());
							
				isShootingStarPattern = isShootingStarPattern && isLowerThread10OrLessPercentOfUpperThread;
				for (int i = 1; i < (previousCandles.length - index); i++) {
					if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
						
						break;
					} else if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
						didPatternGetApproved = true;
						break;
					}
				}
			}
			
			return isShootingStarPattern && didPatternGetApproved;
		}
		return false;
	}

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return false;
	}

	@Override
	public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isReversalPattern(previousCandles, index, pipsValue))
			return previousCandles[index].getHigh() - previousCandles[index].getLow();
		// This is not reversal pattern.
		return -1;
		
	}
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue) || isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		} 
		// This is not reversal pattern.
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue) || isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		}
		// This is not reversal pattern.
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		} else if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		}
		// This is not reversal pattern.
		return -1;
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 2;
	}

}
