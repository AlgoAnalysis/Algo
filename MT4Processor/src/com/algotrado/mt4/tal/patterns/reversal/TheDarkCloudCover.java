package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class TheDarkCloudCover extends Pattern {

	/**
	 * 
	 * This Pattern is called Piercing.
	 */
	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		double firstCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double firstCandleThreads = Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow()) - firstCandleBody;
		boolean isBigBodySmallThreadsInFisrtCandle = firstCandleThreads < firstCandleBody;
		boolean isFirstCandleBearish = previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen();
		
		double secondCandleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double secondCandleThreads = Math.abs(previousCandles[index].getHigh() - previousCandles[index].getLow()) - secondCandleBody;
		boolean isBigBodySmallThreadsInSecondCandle = secondCandleThreads <= secondCandleBody;
		boolean isSecondCandleBullish = previousCandles[index].getClose() > previousCandles[index].getOpen();
		
		boolean isSecondOpenBelowFirstCandleClose = previousCandles[index].getOpen() < previousCandles[index - 1].getClose();
		
		boolean isSecondCloseAboveFirstCandleClose = previousCandles[index].getClose() > previousCandles[index - 1].getClose();
		
		boolean didSecondCandleClosePass50PercentOfFirstCandleBody = (!isSecondCloseAboveFirstCandleClose) ? false :
			previousCandles[index - 1].getHigh() - previousCandles[index].getClose() <= previousCandles[index].getClose() - previousCandles[index - 1].getOpen();
		
		boolean didPatternGetApproved = false;
		
		for (int i = 1; i < (previousCandles.length - index); i++) {
			if (previousCandles[index + i].getLow() < Math.min(previousCandles[index - 1].getLow(), previousCandles[index].getLow())) {
				
				break;
			} else if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
				didPatternGetApproved = true;
				break;
			}
		}
		
		return isFirstCandleBearish && isBigBodySmallThreadsInFisrtCandle && isBigBodySmallThreadsInSecondCandle &&
				isSecondCandleBullish && isSecondOpenBelowFirstCandleClose && didPatternGetApproved &&
				didSecondCandleClosePass50PercentOfFirstCandleBody;
	}

	/**
	 * The Dark Cloud Cover Pattern
	 */
	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		double firstCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double firstCandleThreads = Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow()) - firstCandleBody;
		boolean isBigBodySmallThreadsInFisrtCandle = firstCandleThreads < firstCandleBody;
		boolean isFirstCandleBullish = previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen();
		
		double secondCandleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double secondCandleThreads = Math.abs(previousCandles[index].getHigh() - previousCandles[index].getLow()) - secondCandleBody;
		boolean isBigBodySmallThreadsInSecondCandle = secondCandleThreads <= secondCandleBody;
		boolean isSecondCandleBearish = previousCandles[index].getClose() < previousCandles[index].getOpen();
		
		boolean isSecondOpenAboveFirstCandleClose = previousCandles[index].getOpen() > previousCandles[index - 1].getClose();
		
		boolean isSecondCloseBelowFirstCandleClose = previousCandles[index].getClose() < previousCandles[index - 1].getClose();
		
		boolean didSecondCandleClosePass50PercentOfFirstCandleBody = (!isSecondCloseBelowFirstCandleClose) ? false :
			previousCandles[index - 1].getHigh() - previousCandles[index].getClose() >= previousCandles[index].getClose() - previousCandles[index - 1].getOpen();
		
		boolean didPatternGetApproved = false;
		
		for (int i = 1; i < (previousCandles.length - index); i++) {
			if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
				didPatternGetApproved = true;
				break;
			} else if (previousCandles[index + i].getHigh() > Math.max(previousCandles[index - 1].getHigh(), previousCandles[index].getHigh())) {
				
				break;
			}
		}
			
		return isFirstCandleBullish && isBigBodySmallThreadsInFisrtCandle && isBigBodySmallThreadsInSecondCandle &&
				isSecondCandleBearish && isSecondOpenAboveFirstCandleClose && didPatternGetApproved && 
				didSecondCandleClosePass50PercentOfFirstCandleBody;
	}

	/*@Override
	public double getRisk(SingleCandleBarData[] previousCandles, int index,
			double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return -1;
		}
		if (isBearishReversalPattern(previousCandles, index, pipsValue))
			return Math.abs(Math.max(previousCandles[index - 1].getHigh(), previousCandles[index].getHigh()) - 
								previousCandles[index].getLow());
		if (isBullishReversalPattern(previousCandles, index, pipsValue))
			return Math.abs(previousCandles[index].getHigh() - 
								Math.min(previousCandles[index - 1].getLow(), previousCandles[index].getLow()));
		
		return -1;
	}*/
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return Math.max(previousCandles[index - 1].getHigh(), previousCandles[index].getHigh());
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return Math.min(previousCandles[index - 1].getLow(), previousCandles[index].getLow());
		}
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 3;
	}

}
