package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.tal.patterns.reversal.ReversalPattern1;
import com.algotrado.mt4.tal.strategy.Strategy;


public class ReversalPattern1Strategy extends Strategy {

	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new ReversalPattern1().isBullishReversalPattern(previousCandles, index, pipsValue) /*&& 
			 RSIIndicator.isBullishRSI(previousCandles, index, pipsValue)*/;
	}
	
	public boolean isShortStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return new ReversalPattern1().isBearishReversalPattern(previousCandles, index, pipsValue) /*&& 
				RSIIndicator.isBearishRSI(previousCandles, index, pipsValue)*/;
	}
}
