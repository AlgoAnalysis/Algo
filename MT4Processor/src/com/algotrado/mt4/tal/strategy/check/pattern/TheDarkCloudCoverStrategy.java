package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.TheDarkCloudCover;
import com.algotrado.mt4.tal.strategy.Strategy;

public class TheDarkCloudCoverStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		return new TheDarkCloudCover().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new TheDarkCloudCover().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
