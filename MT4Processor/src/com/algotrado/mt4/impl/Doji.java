package com.algotrado.mt4.impl;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class Doji extends Candle {

	public Doji(){
		
	}
	
	public boolean isDoji(JapaneseCandleBar bar) {
		return isDragonflyDoji(bar) || isGraveStoneDoji(bar) || isRickshawManDoji(bar);
	}
	
	public boolean isDragonflyDoji(JapaneseCandleBar bar) {
		return isBodyLessThan10Percent(bar) && ((bar.getOpen() > bar.getClose()) ? 
				(bar.getHigh() - bar.getOpen()) <= 0.3 * (bar.getClose() - bar.getLow()) :
					(bar.getHigh() - bar.getClose()) <= 0.3 * (bar.getOpen() - bar.getLow()));
	}
	
	public boolean isGraveStoneDoji(JapaneseCandleBar bar) {
		return isBodyLessThan10Percent(bar) && ((bar.getOpen() > bar.getClose()) ? 
				0.3 * (bar.getHigh() - bar.getOpen()) >= (bar.getClose() - bar.getLow()) :
					0.3 * (bar.getHigh() - bar.getClose()) >= (bar.getOpen() - bar.getLow()));
	}
	
	public boolean isRickshawManDoji(JapaneseCandleBar bar) {
		double topThread = (bar.getOpen() > bar.getClose()) ? bar.getHigh() - bar.getOpen() : bar.getHigh() - bar.getClose();
		double bottomThread = (bar.getOpen() > bar.getClose()) ? bar.getClose() - bar.getLow() : bar.getOpen() - bar.getLow();	
		if (topThread > bottomThread) {
			return isBodyLessThan10Percent(bar) && (topThread * 4 <= bottomThread * 6);
		} else {
			return isBodyLessThan10Percent(bar) && (bottomThread * 4 <= topThread * 6);
		}
	}
	
	private boolean isBodyLessThan10Percent(JapaneseCandleBar bar) {
		return Math.abs(bar.getOpen() - bar.getClose()) <= 0.1 * (bar.getHigh() - bar.getLow());
	}
	
	public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return previousCandles[index].getHigh() - previousCandles[index].getLow();
	}
	
}
