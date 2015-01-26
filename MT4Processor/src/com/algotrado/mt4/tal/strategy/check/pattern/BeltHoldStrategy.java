package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.BeltHold;
import com.algotrado.mt4.tal.strategy.Strategy;

public class BeltHoldStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new BeltHold().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new BeltHold().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
