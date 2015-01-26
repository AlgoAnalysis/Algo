package com.algotrado.mt4.tal.strategy.check.pattern;

import com.algotrado.mt4.impl.Doji;
import com.algotrado.mt4.tal.strategy.Strategy;

public class DojiStrategyTest extends Strategy {

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (previousCandles.length <= index + 1) {
			return false;
		}
		Doji doji = new Doji();
		return doji.isDragonflyDoji(previousCandles[index]) || /*(*/doji.isRickshawManDoji(previousCandles[index]) /*&& previousCandles[index].getHigh() < previousCandles[index + 1].getHigh())*/;
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= index + 1) {
			return false;
		}
		Doji doji = new Doji();
		return doji.isGraveStoneDoji(previousCandles[index]) /*|| (doji.isRickshawManDoji(previousCandles[index]) && previousCandles[index].getLow() > previousCandles[index + 1].getLow())*/;
	}

}
