package com.algotrado.mt4.impl;

import java.io.Serializable;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public abstract class Pattern implements Serializable {
	protected int timeFrame;
	
	public Pattern () {
		timeFrame = 15;
	}
	
	public abstract boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public abstract boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public abstract double getPatternHigh(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public abstract double getPatternLow(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public abstract double getPatternApprovalPoint(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public int getNumOfCandlesInPattern() {
		return 2;
	}
	
	public boolean isReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue);
	}
	
	public double getRisk(SingleCandleBarData[] previousCandles, int index,
			double pipsValue) {
		if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return getPatternApprovalPoint(previousCandles, index, pipsValue) - getPatternLow(previousCandles, index, pipsValue);
		} else if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return getPatternHigh(previousCandles, index, pipsValue) - getPatternApprovalPoint(previousCandles, index, pipsValue);
		}
		return -1;
	}

	public boolean isAbsoluteGAPBetween2Candles(SingleCandleBarData candle1, SingleCandleBarData candle2) {
		return (candle1.getHigh() < candle2.getLow()) || (candle2.getHigh() < candle1.getLow());
	}

	public int getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(int timeFrame) {
		this.timeFrame = timeFrame;
	}
}
