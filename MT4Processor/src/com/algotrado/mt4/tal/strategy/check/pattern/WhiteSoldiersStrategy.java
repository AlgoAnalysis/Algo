package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.continuos.WhiteSoldiers;
import com.algotrado.mt4.tal.strategy.Strategy;

public class WhiteSoldiersStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new WhiteSoldiers().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new WhiteSoldiers().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
