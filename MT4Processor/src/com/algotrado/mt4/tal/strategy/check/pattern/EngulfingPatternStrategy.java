package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.Engulfing;
import com.algotrado.mt4.tal.strategy.Strategy;

public class EngulfingPatternStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new Engulfing().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new Engulfing().isBearishReversalPattern(previousCandles, index, pipsValue);
	}
}
