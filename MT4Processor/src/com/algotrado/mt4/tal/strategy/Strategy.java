package com.algotrado.mt4.tal.strategy;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public abstract class Strategy {

	public abstract boolean isLongStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue);
	
	public abstract boolean isShortStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue);

	public boolean isStrategyPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		return isLongStrategyPattern(previousCandles, index, pipsValue) || isShortStrategyPattern(previousCandles, index, pipsValue);
	}
	
	public double getLongGain(JapaneseCandleBar[] candles, int index, double stopLoss, double confirmationPoint) {
		double highestPrice = candles[index].getHigh();
		for (int i = 1; i < candles.length - index; i++) {
			if (highestPrice < candles[index + i].getHigh()) {
				highestPrice = candles[index + i].getHigh();
			}
			if (stopLoss > candles[index + i].getLow()) {
				break; //we reached stop loss.
			}
		}
		double multiplierOfPips = getPipsMultiplier(candles, index);
		return Math.abs(highestPrice - confirmationPoint) * (double)multiplierOfPips;
	}

	public double getPipsMultiplier(JapaneseCandleBar[] candles, int index) {
		return candles[index].getCommodityName().contains("JPY")? (double)100: (double)10000;
	}

	public double getShortGain(JapaneseCandleBar[] candles, int index, double stopLoss, double confirmationPoint) {
		double lowestPrice = candles[index].getLow();
		for (int i = 1; i < candles.length - index; i++) {
			if (lowestPrice > candles[index + i].getLow()) {
				lowestPrice = candles[index + i].getLow();
			}
			if (stopLoss < candles[index + i].getHigh()) {
				break; //we reached stop loss.
			}
		}
		double multiplierOfPips = getPipsMultiplier(candles, index);
		return Math.abs(lowestPrice - confirmationPoint) * (double)multiplierOfPips;
	}
	
	public double getShortRiskGainRatio(JapaneseCandleBar[] candles, int index, double stopLoss, double confirmationPoint) {
		return (double)getShortGain(candles, index, stopLoss, confirmationPoint) / ((double)(stopLoss - confirmationPoint) * (double)getPipsMultiplier(candles, index));
	}

	public double getLongRiskGainRatio(JapaneseCandleBar[] candles, int index, double stopLoss, double confirmationPoint) {
		return (double)getLongGain(candles, index, stopLoss, confirmationPoint) / ((double)(confirmationPoint - stopLoss) * (double)getPipsMultiplier(candles, index));
	}
	
	public double getLongNumOfCandles(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double highestPrice = japaneseCandleBar.getHigh();
		int numOfHighestCandle = 0;
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (highestPrice < previousCandles[index + i].getHigh()) {
				highestPrice = previousCandles[index + i].getHigh();
				numOfHighestCandle = i;
			}
			if (previousCandles[index].getLow() > previousCandles[index + i].getLow()) {
				break; //we reached stop loss.
			}
		}
		return numOfHighestCandle;
	}

	public double getShortNumOfCandles(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double lowestPrice = japaneseCandleBar.getLow();
		int numOfLowestCandle = 0;
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (lowestPrice > previousCandles[index + i].getLow()) {
				lowestPrice = previousCandles[index + i].getLow();
				numOfLowestCandle = i;
			}
			if (previousCandles[index].getHigh() < previousCandles[index + i].getHigh()) {
				break; //we reached stop loss.
			}
		}
		return numOfLowestCandle;
	}
	
	public double getLongCorrectionBeforeHigh(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double highestPrice = japaneseCandleBar.getHigh();
		int indexOfHighest = 0;
		double lowestCorrection = japaneseCandleBar.getHigh(), newLowestCorrection = japaneseCandleBar.getHigh();
		int indexOfLowest = 0;
		int indexOfNewLowest = 0;
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (highestPrice < previousCandles[index + i].getHigh()) {
				highestPrice = previousCandles[index + i].getHigh();
				indexOfHighest = i;
			}
			
			if (newLowestCorrection > previousCandles[index + i].getLow()) {
				if (indexOfNewLowest > indexOfHighest && indexOfHighest > indexOfLowest) {
					
				} else if (indexOfNewLowest <= indexOfHighest) {
					indexOfLowest = indexOfNewLowest;
					lowestCorrection = previousCandles[index + indexOfNewLowest].getLow();
				}
				indexOfNewLowest = i;
				newLowestCorrection = previousCandles[index + i].getLow();
			}
			
			//exit the loop if we reached the stop loss.
			if (previousCandles[index].getLow() > previousCandles[index + i].getLow()) {
				break; //we reached stop loss.
			}
			
		}
		double multiplierOfPips = japaneseCandleBar.getCommodityName().contains("JPY")? (double)100: (double)10000;
		if (indexOfHighest >= indexOfNewLowest) return (japaneseCandleBar.getHigh() - newLowestCorrection)* (double)multiplierOfPips;
		
		return (japaneseCandleBar.getHigh() - lowestCorrection) * (double)multiplierOfPips;
	}

	public double getShortCorrectionBeforeLow(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double lowestPrice = japaneseCandleBar.getLow();
		int indexOfLowest = 0;
		double highestCorrection = japaneseCandleBar.getLow(), newHighestCorrection = japaneseCandleBar.getLow();
		int indexOfHighest = 0;
		int indexOfNewHighest = 0;
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (lowestPrice > previousCandles[index + i].getLow()) {
				lowestPrice = previousCandles[index + i].getLow();
				indexOfLowest = i;
			}
			
			if (newHighestCorrection < previousCandles[index + i].getHigh()) {
				if (indexOfNewHighest > indexOfLowest && indexOfLowest > indexOfHighest) {
					
				} else if (indexOfNewHighest <= indexOfLowest) {
					indexOfHighest = indexOfNewHighest;
					highestCorrection = previousCandles[index + indexOfNewHighest].getHigh();
				}
				indexOfNewHighest = i;
				newHighestCorrection = previousCandles[index + i].getHigh();
			}
			
			//exit the loop if we reached the stop loss.
			if (previousCandles[index].getHigh() < previousCandles[index + i].getHigh()) {
				break; //we reached stop loss.
			}
			
		}
		double multiplierOfPips = japaneseCandleBar.getCommodityName().contains("JPY")? (double)100: (double)10000;		
		if (indexOfLowest >= indexOfNewHighest) return (newHighestCorrection - japaneseCandleBar.getLow())* (double)multiplierOfPips;
		return (highestCorrection - japaneseCandleBar.getLow()) * (double)multiplierOfPips;
	}
}
