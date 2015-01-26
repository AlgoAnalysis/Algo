package com.algotrado.mt4.tal.patterns.reversal;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class SwingPattern extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= 4 || candles.length <= index + 1 || index < 20) {
			return false;
		}
		boolean isReversalPattern = candles[index].getLow() > candles[index - 1].getLow() && candles[index - 2].getLow() > candles[index - 1].getLow();
		
		for (int i = 0; i < 2; i++) {
			if ((candles[index - i].getLow() <= candles[index - i].getLower10Bollinger()) ||
					(candles[index - i].getLow() <= candles[index - i].getLower20Bollinger())) {
				return isReversalPattern;
			}
		}
		
		return false;
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= 4 || candles.length <= index + 1 || index < 20) {
			return false;
		}
		boolean isReversalPattern = candles[index].getHigh() < candles[index - 1].getHigh() && candles[index - 2].getHigh() < candles[index - 1].getHigh();
		
		for (int i = 0; i < 2; i++) {
			if ((candles[index - i].getHigh() >= candles[index - i].getHigher10Bollinger()) ||
					(candles[index - i].getHigh() >= candles[index - i].getHigher20Bollinger())) {
				return isReversalPattern;
			}
		}
		
		return false;
	}

	@Override
	public double getPatternHigh(SingleCandleBarData[] candles,
			int index, double pipsValue) {
		if (isBullishReversalPattern(candles, index, pipsValue)) {
			return Math.max(Math.max(candles[index - 1].getHigh(), candles[index - 2].getHigh()) , candles[index].getHigh());
		} else if (isBearishReversalPattern(candles, index, pipsValue)) {
			return candles[index - 1].getHigh();
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] candles,
			int index, double pipsValue) {
		if (isBullishReversalPattern(candles, index, pipsValue)) {
			return candles[index - 1].getLow();
		} else if (isBearishReversalPattern(candles, index, pipsValue)) {
			return Math.min(Math.min(candles[index - 1].getLow(), candles[index - 2].getLow()) , candles[index].getLow());
		}
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return -1;
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 3;
	}

}
