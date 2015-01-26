package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.ShootingStar;
import com.algotrado.mt4.tal.strategy.Strategy;

public class ShootingStarStrategy extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		// TODO Auto-generated method stub
		return new ShootingStar().isBullishReversalPattern(previousCandles, index, pipsValue);
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		// TODO Auto-generated method stub
		return new ShootingStar().isBearishReversalPattern(previousCandles, index, pipsValue);
	}

}
