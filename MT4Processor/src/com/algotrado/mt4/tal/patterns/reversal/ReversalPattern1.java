package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Candle;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class ReversalPattern1 extends Pattern {

	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 2 && previousCandles.length > index + 1) {
			return false;
		}
		
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		if ((previousCandles[index - 2].getLower20Bollinger() > previousCandles[index - 2].getLow() &&
				previousCandles[index - 2].getLower20Bollinger() < previousCandles[index - 2].getHigh())
				|| (previousCandles[index - 2].getLower10Bollinger() > previousCandles[index - 2].getLow() &&
						previousCandles[index - 2].getLower10Bollinger() < previousCandles[index - 2].getHigh())) {
			
		
		
			boolean isRedBodyFirstCandle = (previousCandles[index - 2].getOpen() > previousCandles[index - 2].getClose() + pipsValue) &&
							(previousCandles[index - 2].getHigh() -  previousCandles[index - 2].getOpen() < 
									previousCandles[index - 2].getOpen() - previousCandles[index - 2].getLow());
			
			boolean isGreenBodySecondCandle = Candle.isBullishCandle(previousCandles[index - 1]);
			
			boolean isGreenHammerSecondCandle = isGreenBodySecondCandle && ( ( (previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen()) >
							(previousCandles[index - 1].getHigh() - previousCandles[index - 1].getClose() ) ) );		
			
			boolean hammerBelowRedCandle = previousCandles[index - 1].getLow() < previousCandles[index - 2].getLow();
			boolean hammerclosedAboveRedCandleLow = previousCandles[index - 2].getLow() < previousCandles[index - 1].getClose();
			
			boolean thirdCandleClosedAboveSecondHammerCandle = 
					previousCandles[index - 1].getHigh() < previousCandles[index].getClose() && 
					previousCandles[index - 1].getLow() < previousCandles[index].getLow();
			
			boolean thirdBullCandle = previousCandles[index].getClose() > previousCandles[index].getOpen() && 
					thirdCandleClosedAboveSecondHammerCandle;
			
			return isRedBodyFirstCandle && isGreenHammerSecondCandle && hammerBelowRedCandle && hammerclosedAboveRedCandleLow && thirdBullCandle;
		}
		return false;
	}
	
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 2 && previousCandles.length > index + 1) {
			return false;
		}
		
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		if ((previousCandles[index - 2].getHigher20Bollinger() > previousCandles[index - 2].getLow() &&
				previousCandles[index - 2].getHigher20Bollinger() < previousCandles[index - 2].getHigh())
				|| (previousCandles[index - 2].getHigher10Bollinger() > previousCandles[index - 2].getLow() &&
						previousCandles[index - 2].getHigher10Bollinger() < previousCandles[index - 2].getHigh())) {
		
			boolean isGreenBodyFirstCandle = (previousCandles[index - 2].getClose() > previousCandles[index - 2].getOpen() + pipsValue) &&
					(previousCandles[index - 2].getHigh() -  previousCandles[index - 2].getOpen() > 
					previousCandles[index - 2].getOpen() - previousCandles[index - 2].getLow());

			boolean isRedBodySecondCandle = previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen();

			boolean isRedShootingStarSecondCandle =  isRedBodySecondCandle && ( ( (previousCandles[index - 1].getClose() - previousCandles[index - 1].getLow() )<
					(previousCandles[index - 1].getOpen() - previousCandles[index - 1].getClose()) ) );

			boolean shootingStarAboveGreenCandle = previousCandles[index - 1].getHigh() > previousCandles[index - 2].getHigh();
			boolean shootingStarclosedBelowGreenCandleHigh = previousCandles[index - 2].getHigh() > previousCandles[index - 1].getClose();

			boolean thirdCandleClosedBelowSecondShootingStarCandle = 
					previousCandles[index - 1].getLow() > previousCandles[index].getClose() && 
					previousCandles[index - 1].getHigh() > previousCandles[index].getHigh();

			boolean thirdBearCandle = previousCandles[index].getClose() < previousCandles[index].getOpen() && 
					thirdCandleClosedBelowSecondShootingStarCandle;

			return isGreenBodyFirstCandle && isRedShootingStarSecondCandle && shootingStarAboveGreenCandle && shootingStarclosedBelowGreenCandleHigh && thirdBearCandle;
		}
		return false;
	}
	
	public boolean isReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue){
		return isBullishReversalPattern(previousCandles, index, pipsValue) || isBearishReversalPattern(previousCandles, index, pipsValue);
	}
	
	/*public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh() - previousCandles[index - 1].getLow();
		} else if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getHigh() - previousCandles[index].getLow();
		}
		// This is not reversal pattern.
		return -1;
		
	}*/
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		} else if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getHigh();
		}
		// This is not reversal pattern.
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getLow();
		} else if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
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
		return 3;
	}
}
