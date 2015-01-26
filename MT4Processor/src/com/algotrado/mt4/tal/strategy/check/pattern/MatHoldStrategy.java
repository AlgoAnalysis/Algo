package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.continuos.MatHoldPattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class MatHoldStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new MatHoldPattern().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new MatHoldPattern().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
