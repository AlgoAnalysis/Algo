package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class BeltHold extends Pattern {
	
	double patternHigh, patternLow, patternApproval;

	@Override
	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		boolean isMiddleBearCandle = previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen();
		boolean isLastBullCandle = previousCandles[index].getClose() > previousCandles[index].getOpen();
		
		boolean isSameFirstCandleOpenAndSecondCandleClosePrice = 
				Math.abs(previousCandles[index - 1].getOpen() - previousCandles[index].getClose()) <= 2 * pipsValue;
		boolean isSameLastCandleOpenAndMiddleCandleClosePrice = 
				Math.abs(previousCandles[index].getOpen() - previousCandles[index - 1].getClose()) <= 2 * pipsValue;
		
		boolean isSameFirstCandleHighAndSecondCandleHighPrice = 
				Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index].getHigh()) <= 2 * pipsValue;
		boolean isSameLastCandleLowAndMiddleCandleLowPrice = 
				Math.abs(previousCandles[index].getLow() - previousCandles[index - 1].getLow()) <= 2 * pipsValue;
		
		double candleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double candleThreads = Math.abs(previousCandles[index].getHigh() - previousCandles[index].getLow()) - candleBody;
		boolean isBigBodySmallThreads = candleThreads <= candleBody;
		
		boolean isEverythingEqualsCase = isMiddleBearCandle && isSameLastCandleLowAndMiddleCandleLowPrice && isSameFirstCandleHighAndSecondCandleHighPrice && 
				isSameLastCandleOpenAndMiddleCandleClosePrice && isSameFirstCandleOpenAndSecondCandleClosePrice && isBigBodySmallThreads;
		
		boolean higherSecondBody = (previousCandles[index].getClose() - previousCandles[index - 1].getOpen()) > 2 * pipsValue;
		boolean higherSecondCandle = (previousCandles[index].getHigh() - previousCandles[index - 1].getHigh()) > 2 * pipsValue;
		
		
		boolean isSecondCandleHigherCase = isMiddleBearCandle && higherSecondBody && higherSecondCandle && isSameLastCandleOpenAndMiddleCandleClosePrice && 
				isSameLastCandleLowAndMiddleCandleLowPrice && isBigBodySmallThreads;
		
		boolean isSecondCandleWithHigherLow =  (previousCandles[index].getLow() - previousCandles[index - 1].getLow()) > 2 * pipsValue;
		boolean isSecondCandleWithHigherLowCase = isMiddleBearCandle && isSecondCandleWithHigherLow && isSameLastCandleOpenAndMiddleCandleClosePrice && 
				(higherSecondBody || isSameFirstCandleOpenAndSecondCandleClosePrice) && (higherSecondCandle || isSameFirstCandleHighAndSecondCandleHighPrice) && isBigBodySmallThreads;
		
		/**
		 * 3 Candles pattern.
		 */
		double firstCandleBody = Math.abs(previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		double firstCandleThreads = Math.abs(previousCandles[index - 2].getHigh() - previousCandles[index - 2].getLow()) - firstCandleBody;
		boolean isBigBodySmallThreadsInFisrtCandle = firstCandleThreads <= firstCandleBody;
		boolean isFirstBearCandleWithSmallThreads = (previousCandles[index - 2].getClose() < previousCandles[index - 2].getOpen()) && 
					isBigBodySmallThreadsInFisrtCandle;
		
		double middleCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double middleCandleThreads = Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow()) - middleCandleBody;
		
		boolean isMiddleCandleWithBigThreadsSmallBody = (isMiddleBearCandle) ?  middleCandleBody < middleCandleThreads : 
															middleCandleBody <= middleCandleThreads;
		
		boolean isLastCandleHighBiggerThanMiddleCandleHighPrice = 
				previousCandles[index].getHigh() - previousCandles[index - 1].getHigh() >= (-2) * pipsValue;
				
		boolean isLastCandleCloseBiggerThanMiddleCandleOpenPrice = 
				previousCandles[index].getClose() - previousCandles[index - 1].getOpen() >= (-2) * pipsValue;
		
		boolean legalThirdCandle = isSameLastCandleOpenAndMiddleCandleClosePrice && isSameLastCandleLowAndMiddleCandleLowPrice && 
				isLastCandleHighBiggerThanMiddleCandleHighPrice && isLastCandleCloseBiggerThanMiddleCandleOpenPrice;
		
		boolean is3CandlesCase = isMiddleCandleWithBigThreadsSmallBody && isFirstBearCandleWithSmallThreads && legalThirdCandle;
		
		caclulatePatternHighLowValues(previousCandles, index, is3CandlesCase, true);
		
		return isLastBullCandle && (isEverythingEqualsCase || isSecondCandleHigherCase || isSecondCandleWithHigherLowCase || is3CandlesCase);
	}

	public void caclulatePatternHighLowValues(
			SingleCandleBarData[] previousCandles, int index,
			boolean is3CandlesCase, boolean isLongPattern) {
		patternApproval = isLongPattern ? previousCandles[index].getHigh() : previousCandles[index].getLow();
		if (is3CandlesCase) {
			patternHigh = Math.max(Math.max(previousCandles[index].getHigh(), previousCandles[index - 1].getHigh()), previousCandles[index - 2].getHigh());
			patternLow = Math.min(Math.min(previousCandles[index].getLow(), previousCandles[index - 1].getLow()), previousCandles[index - 2].getLow());
		} else {
			patternHigh = Math.max(previousCandles[index].getHigh(), previousCandles[index - 1].getHigh());
			patternLow = Math.min(previousCandles[index].getLow(), previousCandles[index - 1].getLow());
		}
	}

	@Override
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		boolean isMiddleBullCandle = previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen();
		boolean isLastBearCandle = previousCandles[index].getClose() < previousCandles[index].getOpen();
		
		boolean isSameFirstCandleOpenAndSecondCandleClosePrice = 
				Math.abs(previousCandles[index - 1].getOpen() - previousCandles[index].getClose()) <= 2 * pipsValue;
		boolean isSameLastCandleOpenAndMiddleCandleClosePrice = 
				Math.abs(previousCandles[index].getOpen() - previousCandles[index - 1].getClose()) <= 2 * pipsValue;
		
		boolean isSameFirstCandleHighAndSecondCandleHighPrice = 
				Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index].getHigh()) <= 2 * pipsValue;
		boolean isSameLastCandleLowAndMiddleCandleLowPrice = 
				Math.abs(previousCandles[index].getLow() - previousCandles[index - 1].getLow()) <= 2 * pipsValue;
		
		double candleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double candleThreads = Math.abs(previousCandles[index].getHigh() - previousCandles[index].getLow()) - candleBody;
		boolean isBigBodySmallThreads = candleThreads <= candleBody;
		
		boolean isEverythingEqualsCase = isMiddleBullCandle && isSameLastCandleLowAndMiddleCandleLowPrice && isSameFirstCandleHighAndSecondCandleHighPrice && 
				isSameLastCandleOpenAndMiddleCandleClosePrice && isSameFirstCandleOpenAndSecondCandleClosePrice && isBigBodySmallThreads;
		
		boolean lowerSecondBody = (previousCandles[index - 1].getOpen() - previousCandles[index].getClose()) > 2 * pipsValue;
		boolean lowerSecondCandle = (previousCandles[index - 1].getLow() - previousCandles[index].getLow()) > 2 * pipsValue;
		
		boolean isSecondCandleHigherCase = isMiddleBullCandle && lowerSecondBody && lowerSecondCandle && isSameLastCandleOpenAndMiddleCandleClosePrice && 
				isSameFirstCandleHighAndSecondCandleHighPrice && isBigBodySmallThreads;
		
		boolean isSecondCandleWithLowerHigh =  (previousCandles[index - 1].getHigh() - previousCandles[index].getHigh()) > 2 * pipsValue;
		boolean isSecondCandleWithLowerHighCase = isMiddleBullCandle && isSecondCandleWithLowerHigh && isSameLastCandleOpenAndMiddleCandleClosePrice && 
				(lowerSecondBody || isSameFirstCandleOpenAndSecondCandleClosePrice) && (lowerSecondCandle || isSameFirstCandleHighAndSecondCandleHighPrice) && isBigBodySmallThreads;
		
		
		/**
		 * 3 Candles pattern.
		 */
		double firstCandleBody = Math.abs(previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		double firstCandleThreads = Math.abs(previousCandles[index - 2].getHigh() - previousCandles[index - 2].getLow()) - firstCandleBody;
		boolean isBigBodySmallThreadsInFisrtCandle = firstCandleThreads <= firstCandleBody;
		boolean isFirstBullCandleWithSmallThreads = (previousCandles[index - 2].getClose() > previousCandles[index - 2].getOpen()) && 
					isBigBodySmallThreadsInFisrtCandle;
		
		double middleCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double middleCandleThreads = Math.abs(previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow()) - middleCandleBody;
		
		boolean isMiddleCandleWithBigThreadsSmallBody = (isMiddleBullCandle) ?  middleCandleBody < middleCandleThreads : 
															middleCandleBody <= middleCandleThreads;
		
		boolean isLastCandleLowSmallerThanMiddleCandleLowPrice = 
				previousCandles[index - 1].getLow() - previousCandles[index].getLow() >= (-2) * pipsValue;
				
		boolean isLastCandleCloseSmallerThanMiddleCandleOpenPrice = 
				previousCandles[index - 1].getOpen() - previousCandles[index].getClose() >= (-2) * pipsValue;
		
		boolean legalThirdCandle = isSameLastCandleOpenAndMiddleCandleClosePrice && isSameFirstCandleHighAndSecondCandleHighPrice && 
				isLastCandleLowSmallerThanMiddleCandleLowPrice && isLastCandleCloseSmallerThanMiddleCandleOpenPrice;
		
		boolean is3CandlesCase = isMiddleCandleWithBigThreadsSmallBody && isFirstBullCandleWithSmallThreads && legalThirdCandle;
		
		caclulatePatternHighLowValues(previousCandles, index, is3CandlesCase, false);
		
		return isLastBearCandle && (isEverythingEqualsCase || isSecondCandleHigherCase || isSecondCandleWithLowerHighCase || is3CandlesCase);
	}

//	@Override
//	public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
//		if (isBearishReversalPattern(previousCandles, index, pipsValue))
//			return Math.abs(Math.max(previousCandles[index - 1].getHigh(), previousCandles[index].getHigh()) - 
//								previousCandles[index].getLow());
//		if (isBullishReversalPattern(previousCandles, index, pipsValue))
//			return Math.abs(previousCandles[index].getHigh() - 
//								Math.min(previousCandles[index - 1].getLow(), previousCandles[index].getLow()));
//		
//		return -1;
//	}

	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue))
			return patternHigh;
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue))
			return patternLow;
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue))
			return patternApproval;
		return -1;
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 2;
	}

}
