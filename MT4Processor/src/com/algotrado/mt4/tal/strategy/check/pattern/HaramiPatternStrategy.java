package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.HaramiPattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class HaramiPatternStrategy extends Strategy {

	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new HaramiPattern().isBullishReversalPattern(previousCandles, index, pipsValue) /*&& 
			 RSIIndicator.isBullishRSI(previousCandles, index, pipsValue)*/;
	}
	
	public boolean isShortStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new HaramiPattern().isBearishReversalPattern(previousCandles, index, pipsValue) /*&& 
				RSIIndicator.isBearishRSI(previousCandles, index, pipsValue)*/;
	}
}
