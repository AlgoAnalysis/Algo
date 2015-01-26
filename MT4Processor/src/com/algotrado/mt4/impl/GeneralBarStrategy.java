package com.algotrado.mt4.impl;

public abstract class GeneralBarStrategy {
	
	public abstract boolean isStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue);
	
	public abstract boolean isLongStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue);
	
	public abstract boolean isShortStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue);

	protected boolean didBearishBarCreateNewHigh(JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		return previousCandles[index].getHigh() > previousCandles[index - 1].getHigh() + pipsValue &&
				previousCandles[index].getHigh() > previousCandles[index - 2].getHigh() + pipsValue;
	}
	
	protected boolean didBullishBarCreateNewLow(JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		return previousCandles[index].getLow() < previousCandles[index - 1].getLow() - pipsValue &&
				previousCandles[index].getLow() < previousCandles[index - 2].getLow() - pipsValue;
	}
	
	public double getLongGain(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double highestPrice = japaneseCandleBar.getHigh();
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (highestPrice < previousCandles[index + i].getHigh()) {
				highestPrice = previousCandles[index + i].getHigh();
			}
			if (previousCandles[index].getLow() > previousCandles[index + i].getLow()) {
				break; //we reached stop loss.
			}
		}
		double multiplierOfPips = japaneseCandleBar.getCommodityName().contains("JPY")? (double)100: (double)10000;
		return Math.abs(highestPrice - japaneseCandleBar.getHigh()) * (double)multiplierOfPips;
	}

	public double getShortGain(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		double lowestPrice = japaneseCandleBar.getLow();
		for (int i = 1; i < previousCandles.length - index; i++) {
			if (lowestPrice > previousCandles[index + i].getLow()) {
				lowestPrice = previousCandles[index + i].getLow();
			}
			if (previousCandles[index].getHigh() < previousCandles[index + i].getHigh()) {
				break; //we reached stop loss.
			}
		}
		double multiplierOfPips = japaneseCandleBar.getCommodityName().contains("JPY")? (double)100: (double)10000;
		return Math.abs(lowestPrice - japaneseCandleBar.getLow()) * (double)multiplierOfPips;
	}
	
	public double getShortRiskGainRatio(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		return (double)getShortGain(japaneseCandleBar, previousCandles, index) / (double)japaneseCandleBar.getRisk();
	}

	public double getLongRiskGainRatio(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index) {
		return (double)getLongGain(japaneseCandleBar, previousCandles, index) / (double)japaneseCandleBar.getRisk();
	}
	
	public boolean didBullishPinbarGetActivated(JapaneseCandleBar[] previousCandles, int index, int numOfCandlesToActivate) {
		int i = 1;
		double pips = previousCandles[0].getCommodityName().contains("JPY") ? 0.01 : 0.0001;
		while (i + index < previousCandles.length) {
			if ((previousCandles[index].getHigh() + pips) < previousCandles[i + index].getHigh() && (numOfCandlesToActivate < 0 || i <= numOfCandlesToActivate)) {
				return true;
			} else if (previousCandles[index].getLow() > previousCandles[i + index].getLow() || (numOfCandlesToActivate > 0 && i > numOfCandlesToActivate)) {
				break;
			}
			i++;
		}
		return false;
	}
	
	public boolean didBearishPinbarGetActivated(JapaneseCandleBar[] previousCandles, int index, int numOfCandlesToActivate) {
		int i = 1;
		double pips = previousCandles[0].getCommodityName().contains("JPY") ? 0.01 : 0.0001;
		while (i + index < previousCandles.length) {
			if ((previousCandles[index].getLow() - pips) > previousCandles[i + index].getLow() && (numOfCandlesToActivate < 0 || i <= numOfCandlesToActivate)) {
				return true;
			} else if (previousCandles[index].getHigh() < previousCandles[i + index].getHigh() || (numOfCandlesToActivate > 0 && i > numOfCandlesToActivate)) {
				break;
			}
			i++;
		}
		return false;
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
