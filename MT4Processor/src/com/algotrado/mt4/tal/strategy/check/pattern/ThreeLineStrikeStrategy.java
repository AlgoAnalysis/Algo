package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.continuos.ThreeLineStrikePattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class ThreeLineStrikeStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new ThreeLineStrikePattern().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new ThreeLineStrikePattern().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
