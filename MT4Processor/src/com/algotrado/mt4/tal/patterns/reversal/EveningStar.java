package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class EveningStar extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return false;
	}

	@Override
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		double firstCandleBody = Math.abs(previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		double firstCandleThreads = previousCandles[index - 2].getHigh() - previousCandles[index - 2].getLow() - firstCandleBody;
		boolean isFirstCandleBigBodySmallThreads = firstCandleBody >= 2 * firstCandleThreads;
		boolean isFirstCandleBullish = previousCandles[index - 2].getClose() > previousCandles[index - 2].getOpen();
		
		
		boolean isSecondCandleBullish = previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen();
		double secondCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double secondCandleThreads = previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow() - secondCandleBody;
		boolean secondCandleWithSmallBody = (isSecondCandleBullish) ? secondCandleBody < secondCandleThreads : secondCandleBody <= secondCandleThreads;
		boolean isSecondCandleWithGAP = previousCandles[index - 1].getClose() > previousCandles[index - 2].getClose() && previousCandles[index - 2].getClose() < previousCandles[index - 1].getOpen();
		
		double thirdCandleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double thirdCandleThreads = previousCandles[index].getHigh() - previousCandles[index].getLow() - thirdCandleBody;
		boolean isThirdCandleBigBodySmallThreads = thirdCandleBody >= 2 * thirdCandleThreads;
		boolean isThirdCandleBearish = previousCandles[index].getClose() < previousCandles[index].getOpen();
		boolean ThirdCandleClosedBelowFirstOne = previousCandles[index].getClose() < previousCandles[index - 2].getClose() &&
				(previousCandles[index - 2].getClose() - previousCandles[index].getClose()) >= 0.3 * (previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		
		
		boolean isSecondCandleHighest = Math.max(previousCandles[index - 2].getHigh(), previousCandles[index].getHigh()) < previousCandles[index - 1].getHigh();
		
		boolean didPatternGetApproved = false;
		
		for (int i = 1; i < (previousCandles.length - index); i++) {
			if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
				didPatternGetApproved = true;
				break;
			} else if (previousCandles[index + i].getHigh() > previousCandles[index - 1].getHigh()) {
//				didPatternGetApproved = false;
				break;
			}
		}
		
		return isFirstCandleBigBodySmallThreads && isFirstCandleBullish && 
				secondCandleWithSmallBody && isSecondCandleWithGAP && isSecondCandleHighest &&
				isThirdCandleBigBodySmallThreads && isThirdCandleBearish && ThirdCandleClosedBelowFirstOne && didPatternGetApproved;
	}

	/*@Override
	public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
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
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return Math.max(Math.max(previousCandles[index].getHigh(), previousCandles[index - 1].getHigh()), previousCandles[index - 2].getHigh());
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return Math.min(Math.min(previousCandles[index].getLow(), previousCandles[index - 1].getLow()), previousCandles[index - 2].getLow());
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
