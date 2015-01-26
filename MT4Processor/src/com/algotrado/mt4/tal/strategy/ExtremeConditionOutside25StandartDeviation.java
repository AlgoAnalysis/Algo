package com.algotrado.mt4.tal.strategy;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class ExtremeConditionOutside25StandartDeviation extends Strategy {

	private double stopLoss;
	private double confirmationPoint;
	
	
	@Override
	public boolean isShortStrategyPattern(SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= index + 1 && candles.length <= 20 && index <= 20) {
			return false;
		}
		
		if (candles[index].getLow() > candles[index].getHigher20Bollinger()/*This will store high 50 bollinger with SD 2.5*/) {
			return false;
		} else if (candles[index].getLow() < candles[index].getSMA20()/*Will Store midline 50*/) {//candle passed TP point.
			return false;
		} else if (!(candles[index].getClose() < candles[index].getHigher20Bollinger() && 
				candles[index].getOpen() < candles[index].getHigher20Bollinger())) {// Should be full body under bollinger 50.
			return false;
		}
		
		int firstCandleIndex = -1;
		
		for (int i = 0 ; index - i > 20 ; i++) {
			if (i > 0 && (candles[index - i].getClose() < candles[index - i].getHigher20Bollinger() && 
					candles[index - i].getOpen() < candles[index - i].getHigher20Bollinger())) {// Candle we found is not the first to close inside bands.
				return false;
			}
			
			if (candles[index-i].getLow() > candles[index-i].getHigher20Bollinger()/*This will store high 50 bollinger with SD 2.5*/) {
				firstCandleIndex = index - i;
				break;
			} else if (candles[index-i].getLow() < candles[index-i].getSMA20()) {
				return false;
			}
		}
		
		if (firstCandleIndex == -1) {
			return false;
		}
		
		int i = 0;
		while (index + i < candles.length) {
			if(candles[index+i].getLow() < candles[index].getLow()) {
				this.stopLoss = candles[firstCandleIndex].getHigh();
				this.confirmationPoint = candles[index].getLow();
				return true;
			} else if (candles[index+i].getHigh() > candles[firstCandleIndex].getHigh() + pipsValue) {
				return false;
			}
			i++;
		}
		
		
		
		return false;
	}

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= index + 1 && candles.length <= 20 && index <= 20) {
			return false;
		}
		
		if (candles[index].getHigh() < candles[index].getLower20Bollinger()/*This will store high 50 bollinger with SD 2.5*/) {
			return false;
		} else if (candles[index].getHigh() > candles[index].getSMA20()/*Will Store midline 50*/) {//candle passed TP point.
			return false;
		} else if (!(candles[index].getClose() > candles[index].getLower20Bollinger() && 
				candles[index].getOpen() > candles[index].getLower20Bollinger())) {// Should be full body above bollinger 50.
			return false;
		}
		
		int firstCandleIndex = -1;
		
		for (int i = 0 ; index - i > 20 ; i++) {
			if (i > 0 && (candles[index - i].getClose() > candles[index - i].getLower20Bollinger() && 
					candles[index - i].getOpen() > candles[index - i].getLower20Bollinger())) {// Candle we found is not the first to close inside bands.
				return false;
			}
			if (candles[index-i].getHigh() < candles[index-i].getLower20Bollinger()/*This will store high 50 bollinger with SD 2.5*/) {
				firstCandleIndex = index - i;
				break;
			} else if (candles[index-i].getHigh() > candles[index-i].getSMA20()) {
				return false;
			}
		}
		
		if (firstCandleIndex == -1) {
			return false;
		}
		
		int i = 0;
		while (index + i < candles.length) {
			if(candles[index+i].getHigh() > candles[index].getHigh()) {
				this.stopLoss = candles[firstCandleIndex].getLow();
				this.confirmationPoint = candles[index].getHigh();
				return true;
			} else if (candles[index+i].getLow() < candles[firstCandleIndex].getLow() - pipsValue) {
				return false;
			}
			i++;
		}
		
		return false;
	}
	
	public double getRisk() {
		return Math.abs(confirmationPoint - stopLoss);
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getConfirmationPoint() {
		return confirmationPoint;
	}
	
	public boolean didReachMidLineLong(SingleCandleBarData[] candles, int index, double pipsValue) {
		int i = 1;
		while (index + i < candles.length) {
			if(candles[index+i].getHigh() > candles[index].getSMA20() && !(candles[index+i].getLow() < this.stopLoss)) {
				return true;
			} else if (candles[index+i].getLow() < this.stopLoss - pipsValue) {
				return false;
			}
			i++;
		}
		return false;
	}
	
	public boolean didReachMidLineShort(SingleCandleBarData[] candles, int index, double pipsValue) {
		int i = 1;
		while (index + i < candles.length) {
			if(candles[index+i].getLow() < candles[index].getSMA20() && !(candles[index+i].getHigh() > this.stopLoss)) {
				return true;
			} else if (candles[index+i].getHigh() > this.stopLoss + pipsValue) {
				return false;
			}
			i++;
		}
		return false;
	}

}
