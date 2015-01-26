package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.continuos.TasukiUpOrDownPattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class TasukiUpOrDownStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new TasukiUpOrDownPattern().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new TasukiUpOrDownPattern().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
