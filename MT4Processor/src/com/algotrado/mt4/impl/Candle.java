package com.algotrado.mt4.impl;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public abstract class Candle /*extends Pattern*/ {
	
	public static boolean isBearishCandle(JapaneseCandleBar candle) {
		return candle.getClose() < candle.getOpen();
	}
	
	public static boolean isBullishCandle(JapaneseCandleBar candle) {
		return candle.getClose() > candle.getOpen();
	}
	
	public static boolean isBigBodyOrEqualToThreadsCandle(JapaneseCandleBar candle) {
		double candleBody = getBodySize(candle);
		double candleThreads = candle.getHigh() - candle.getLow() - candleBody;
		return candleThreads <= candleBody;
	}
	
	public static boolean isBigBodySmallThreadsCandle(JapaneseCandleBar candle) {
		double candleBody = getBodySize(candle);
		double candleThreads = candle.getHigh() - candle.getLow() - candleBody;
		return candleThreads < candleBody;
	}
	
	public static double getBodySize(JapaneseCandleBar candle) {
		return Math.abs(candle.getClose() - candle.getOpen());
	}
	
	public static double getCandleSize(JapaneseCandleBar candle) {
		return candle.getHigh() - candle.getLow();
	}
	
	public static boolean isCandleTouchingTopBollinger20Bands(SingleCandleBarData candle) {
		return candle.getHigher20Bollinger() >= candle.getLow() && candle.getHigher20Bollinger() <= candle.getHigh();
	}
	
	public static boolean isCandleTouchingBottomBollinger20Bands(SingleCandleBarData candle) {
		return candle.getLower20Bollinger() >= candle.getLow() && candle.getLower20Bollinger() <= candle.getHigh();
	}
	
	public static boolean isCandleTouchingTopBollinger10Bands(SingleCandleBarData candle) {
		return candle.getHigher10Bollinger() >= candle.getLow() && candle.getHigher10Bollinger() <= candle.getHigh();
	}
	
	public static boolean isCandleTouchingBottomBollinger10Bands(SingleCandleBarData candle) {
		return candle.getLower10Bollinger() >= candle.getLow() && candle.getLower10Bollinger() <= candle.getHigh();
	}
	
	public static boolean isInsideBar(SingleCandleBarData candle, SingleCandleBarData insideBar) {
		return candle.getLow() < insideBar.getLow() && candle.getHigh() > insideBar.getHigh() ;
	}

}
