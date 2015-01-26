package com.algotrado.mt4.tal.patterns.range;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

/**
 * For now this pattern is only supported in 15 minutes time frame.
 * @author ohad
 *
 */
public class RangePattern extends Pattern {
	public RangePattern () {
		super();
	}

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 15 || previousCandles.length <= index + 1 || index < 12) {
			return false;
		} else if (timeFrame == 15) {
			boolean isRangePattern = isRangePattern(previousCandles, index, pipsValue);
			return isRangePattern;
		}
		return false;
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 15 || previousCandles.length <= index + 1 || index < 12) {
			return false;
		} else if (timeFrame == 15) {
			boolean isRangePattern = isRangePattern(previousCandles, index, pipsValue);
			return isRangePattern;
		}
		
		return false;
	}

	public boolean isRangePattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		double maxLow  = previousCandles[index].getLow();
		double maxHigh = previousCandles[index].getHigh();
		double minLow  = previousCandles[index].getLow();
		double minHigh = previousCandles[index].getHigh();
		if ((previousCandles[index].getHigher10Bollinger() > previousCandles[index].getHigher20Bollinger()) || 
				(previousCandles[index].getLower10Bollinger() < previousCandles[index].getLower20Bollinger())) {
			return false;
		}
		boolean isRangePattern = false;
		for (int i = 1; i < 10; i++) {
			if ((previousCandles[index - i].getHigher10Bollinger() > previousCandles[index - i].getHigher20Bollinger()) || 
					(previousCandles[index - i].getLower10Bollinger() < previousCandles[index - i].getLower20Bollinger())) {
				return false;
			}
			if (previousCandles[index - i].getLow() > maxLow) {
				maxLow = previousCandles[index - i].getLow();
			} else if (previousCandles[index - i].getLow() < minLow) {
				minLow = previousCandles[index - i].getLow();
			} else if (previousCandles[index - i].getHigh() > maxHigh) {
				maxHigh = previousCandles[index - i].getHigh();
			} else if (previousCandles[index - i].getHigh() < minHigh) {
				minHigh = previousCandles[index - i].getHigh();
			}
		}
		isRangePattern = ((maxHigh - minHigh) < (5 * pipsValue)) && ((maxLow - minLow) < (5 * pipsValue));
		return isRangePattern;
	}
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
//		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
//			return previousCandles[index].getHigh();
//		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
//		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
//			return previousCandles[index].getLow();
//		} 
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
//		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
//			return previousCandles[index].getLow();
//		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
//			return previousCandles[index].getHigh();
//		}
		return -1;
	}


}
