package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.range.RangePattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class RangeStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		RangePattern rangePattern = new RangePattern();
		return rangePattern.isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		RangePattern rangePattern = new RangePattern();
		return rangePattern.isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
