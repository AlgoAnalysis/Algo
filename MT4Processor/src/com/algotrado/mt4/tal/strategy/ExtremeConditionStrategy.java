package com.algotrado.mt4.tal.strategy;

import com.algotrado.mt4.impl.Candle;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class ExtremeConditionStrategy extends Strategy {
	
	private int timeFrame; 
	
	public ExtremeConditionStrategy (int timeFrame) {
		this.timeFrame = timeFrame;
	}

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) { 
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		if (isStopInTradeTimes(previousCandles, index)) {
			return false;
		}
		boolean areBandsAllignedCorrect =
				previousCandles[index].getLower10Bollinger() < previousCandles[index].getLower20Bollinger() &&
				previousCandles[index].getHigher10Bollinger() > previousCandles[index].getHigher20Bollinger() &&
				previousCandles[index].getSma10() < previousCandles[index].getSMA20();
		return Candle.isBullishCandle(previousCandles[index]) && 
				previousCandles[index].getHigh() < previousCandles[index].getLower10Bollinger() &&
				previousCandles[index].getHigh() < previousCandles[index].getLower20Bollinger() && 
				areBandsAllignedCorrect && previousCandles[index].getRsi() < 20;
	}

	public boolean isStopInTradeTimes(SingleCandleBarData[] previousCandles,
			int index) {
		long dateDiff =  previousCandles[index].getTime().getTime() - previousCandles[index - 1].getTime().getTime();
		double dateDiffInMinutes = (double)dateDiff/(1000 * 60);
		boolean isStopInTradeTime = dateDiffInMinutes > timeFrame;
		return isStopInTradeTime;
	}

	@Override
	public boolean isShortStrategyPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		if (isStopInTradeTimes(previousCandles, index)) {
			return false;
		}
		boolean areBandsAllignedCorrect =
				previousCandles[index].getLower10Bollinger() < previousCandles[index].getLower20Bollinger() &&
				previousCandles[index].getHigher10Bollinger() > previousCandles[index].getHigher20Bollinger() &&
				previousCandles[index].getSma10() > previousCandles[index].getSMA20();
		return Candle.isBearishCandle(previousCandles[index]) && 
				previousCandles[index].getLow() > previousCandles[index].getHigher10Bollinger() &&
				previousCandles[index].getLow() > previousCandles[index].getHigher20Bollinger() && 
				areBandsAllignedCorrect && previousCandles[index].getRsi() > 80;
	}

}
