package com.algotrado.mt4.tal.patterns.continuos;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.Candle;
import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class ThreeLineStrikePattern extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 4) {
			return false;
		}
		return Candle.isBearishCandle(previousCandles[index]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index]) &&
				(previousCandles[index].getOpen() > previousCandles[index - 1].getClose()) && (previousCandles[index - 1].getClose() + (0.4 * Candle.getBodySize(previousCandles[index])) > previousCandles[index].getOpen()) &&
				(previousCandles[index].getClose() < previousCandles[index - 3].getOpen()) && 
				Candle.isBullishCandle(previousCandles[index - 1]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index - 1]) &&
				Candle.isBullishCandle(previousCandles[index - 2]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index - 2]) &&
				Candle.isBullishCandle(previousCandles[index - 3])&& Candle.isBigBodySmallThreadsCandle(previousCandles[index - 3]);
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 4) {
			return false;
		}
		return Candle.isBullishCandle(previousCandles[index]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index]) &&
				(previousCandles[index].getOpen() < previousCandles[index - 1].getClose()) && (previousCandles[index - 1].getClose()  < previousCandles[index].getOpen() + (0.4 * Candle.getBodySize(previousCandles[index])) ) &&
				(previousCandles[index].getClose() > previousCandles[index - 3].getOpen()) && 
				Candle.isBearishCandle(previousCandles[index - 1]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index - 1]) &&
				Candle.isBearishCandle(previousCandles[index - 2]) && Candle.isBigBodySmallThreadsCandle(previousCandles[index - 2]) &&
				Candle.isBearishCandle(previousCandles[index - 3])&& Candle.isBigBodySmallThreadsCandle(previousCandles[index - 3]);
	}

	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			bars.add(previousCandles[index - 2]);
			bars.add(previousCandles[index - 3]);
			return PriceUtil.getMaxHigh(bars);
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			bars.add(previousCandles[index - 2]);
			bars.add(previousCandles[index - 3]);
			return PriceUtil.getMinLow(bars);
		}
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}

}
