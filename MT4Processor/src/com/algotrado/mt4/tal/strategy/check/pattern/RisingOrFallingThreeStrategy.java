package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.continuos.RisingOrFallingThreePattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class RisingOrFallingThreeStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new RisingOrFallingThreePattern().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new RisingOrFallingThreePattern().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
