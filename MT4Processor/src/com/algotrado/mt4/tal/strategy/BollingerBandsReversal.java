package com.algotrado.mt4.tal.strategy;

import com.algotrado.mt4.impl.Candle;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class BollingerBandsReversal extends Strategy {
	
//	private double risk;
	private double stopLoss;
	private double confirmationPoint;
	private int lowIndex, highIndex;

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] candles, int index, double pipsValue) {
		return getLongSimpleBollingerReversal(candles, index, pipsValue) && isBollinger10OutsideBollinger20(candles, index, pipsValue) //&&
//				candles[index - 1].getRsi() < 60 /*&& candles[index - 1].getHigher10Bollinger() > candles[index - 1].getSMA20() &&
//				candles[lowIndex].getHigher10Bollinger() < candles[lowIndex].getSMA20()*/
				/* candles[index - 1].getLower10Bollinger() < candles[index - 2].getLower10Bollinger() && 
				candles[index - 1].getHigher10Bollinger() > candles[index - 2].getHigher10Bollinger()*/;
	}

	public boolean getLongSimpleBollingerReversal(
			SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= index + 1 && candles.length <= 20 && index <= 20) {
			return false;
		}
		// Rules:
		// last candle touching Bollinger bands.
		// after that candle there should be a new high/low point "A".
		// after high/low point there should be a correction of the price.
		// after a correction of the price there should be a new low/high point but not touching Bollinger bands anymore.
		// After that there should be price structure (price confirmation), which is passed point "A".
//		boolean isInsideBarPattern = false;
		int firstCandleIndex = -1;
		if (Candle.isCandleTouchingBottomBollinger20Bands(candles[index])) {
			return false;
		}
		
		// trace last candle that did touch bollinger bands.
		for (int i = 1; index - i > 20; i++) {
			if (Candle.isCandleTouchingBottomBollinger20Bands(candles[index - i])) {
//				firstHigh = candles[index - i].getHigh();
				firstCandleIndex = index - i;
				if (firstCandleIndex <= 20) {
					return false;
				}
				break;
			}
		}
		
		if (firstCandleIndex == -1) {
			return false;
		}
		
		// Search for price structure.
		int lastHighIndex = firstCandleIndex, lowIndex = -1, prevHighIndex = -1;
		
		for (int i = firstCandleIndex + 1;  i <= index; i++) {
			if (candles[firstCandleIndex + 1].getLow() < candles[firstCandleIndex].getLow()) {
				return false;
			}
			
			if (candles[i].getHigh() > candles[lastHighIndex].getHigh()) {
				lastHighIndex = i;
			} 
			if (((lowIndex == -1) && (candles[i].getLow() < candles[i-1].getLow()) ) || 
						((lowIndex != -1) && (candles[i].getLow() < candles[lowIndex].getLow())) ){
				lowIndex = i;	
				prevHighIndex = lastHighIndex;
			}
			
		}
		
		if (lowIndex == index || lowIndex == -1 || prevHighIndex == -1 || prevHighIndex == lastHighIndex) {
			return false;
		}
		
		for (int i = lowIndex; (i < index) && (lowIndex != -1); i++) {
			if (candles[i].getHigh() > candles[prevHighIndex].getHigh()) {
				return false;// Pattern was already activated.
			}
		}
		
		boolean lastCandleCreatedConfirmedStrategy = (candles[index].getHigh() > candles[prevHighIndex].getHigh()) && lowIndex != -1 && 
				(lastHighIndex > (prevHighIndex + 1));
		
		if (!lastCandleCreatedConfirmedStrategy) {
			return false;
		} else {
			double highestHigh = candles[prevHighIndex + 1].getHigh();
			double lowestHigh = candles[prevHighIndex + 1].getHigh();
			for (int i = prevHighIndex + 1; i < lastHighIndex; i++) {
				if (candles[i].getHigh() > highestHigh) {
					highestHigh = candles[i].getHigh();
				} else if(candles[i].getHigh() < lowestHigh) {
					lowestHigh = candles[i].getHigh();
				} 
			}
			if (((highestHigh - lowestHigh) < (5 * pipsValue))  && ((lastHighIndex - prevHighIndex) > 8)) {
				return false;
			}
		}
		
		boolean secondRSIHigher = candles[lowIndex].getRsi() > candles[firstCandleIndex].getRsi();
		
//		this.risk = candles[prevHighIndex].getHigh() - candles[lowIndex].getLow();
		this.stopLoss = candles[lowIndex].getLow();
		this.confirmationPoint = candles[index].getClose();//candles[prevHighIndex].getHigh();
		
		
		
		boolean isLongReveral = lastCandleCreatedConfirmedStrategy && secondRSIHigher && !getLongSimpleBollingerReversal(candles, index - 1, pipsValue) &&
				!isBollinger10OutsideBollinger20(candles, firstCandleIndex, pipsValue);
		
		if (isLongReveral) {
			this.lowIndex = lowIndex;
		}
		
		return isLongReveral;
	}

	@Override
	public boolean isShortStrategyPattern(SingleCandleBarData[] candles, int index, double pipsValue) {
		return isShortSimpleBollingerReversal(candles, index, pipsValue)  && isBollinger10OutsideBollinger20(candles, index, pipsValue) /*&&
				candles[index - 1].getRsi() > 40  && candles[index - 1].getLower10Bollinger() < candles[index - 1].getSMA20() &&
				candles[lowIndex].getLower10Bollinger() > candles[lowIndex].getSMA20()
				/*candles[index - 1].getLower10Bollinger() < candles[index - 2].getLower10Bollinger() && 
				candles[index - 1].getHigher10Bollinger() > candles[index - 2].getHigher10Bollinger()*/ ;
	}

	public boolean isShortSimpleBollingerReversal(
			SingleCandleBarData[] candles, int index, double pipsValue) {
		if (candles.length <= index + 1 && candles.length <= 20 && index <= 20) {
			return false;
		}
		// Rules:
		// last candle touching Bollinger bands.
		// after that candle there should be a new high/low point "A".
		// after high/low point there should be a correction of the price.
		// after a correction of the price there should be a new low/high point but not touching Bollinger bands anymore.
		// After that there should be price structure (price confirmation), which is passed point "A".
		//				boolean isInsideBarPattern = false;
		int firstCandleIndex = -1;
		if (Candle.isCandleTouchingTopBollinger20Bands(candles[index])) {
			return false;
		}

		// trace last candle that did touch bollinger bands.
		for (int i = 1; index - i > 20; i++) {
			if (Candle.isCandleTouchingTopBollinger20Bands(candles[index - i])) {
				//						firstHigh = candles[index - i].getHigh();
				firstCandleIndex = index - i;
				if (firstCandleIndex <= 20) {
					return false;
				}
				break;
			}
		}
		
		if (firstCandleIndex == -1) {
			return false;
		}

		// Search for price structure.
		int lastLowIndex = firstCandleIndex, highIndex = -1, prevLowIndex = -1;

		for (int i = firstCandleIndex + 1;  i <= index; i++) {
			if (candles[firstCandleIndex + 1].getHigh() > candles[firstCandleIndex].getHigh()) {
				return false;
			}
			
			if (candles[i].getLow() < candles[lastLowIndex].getLow()) {
				lastLowIndex = i;
			} 
			if (((highIndex == -1) && (candles[i].getHigh() > candles[i - 1].getHigh()) ) || 
						((highIndex != -1) && (candles[i].getHigh() > candles[highIndex].getHigh())) ){
				highIndex = i;	
				prevLowIndex = lastLowIndex;
			}
			
		}
		
		if (highIndex == index || highIndex == -1 || prevLowIndex == -1 || prevLowIndex == lastLowIndex) {
			return false;
		}
		
		for (int i = highIndex; (i < index) && (highIndex != -1); i++) {
			if (candles[i].getLow() < candles[prevLowIndex].getLow()) {
				return false;// Pattern was already activated.
			}
		}

		boolean lastCandleCreatedConfirmedStrategy = (candles[index].getLow() < candles[prevLowIndex].getLow()) && highIndex != -1 && 
				(lastLowIndex > (prevLowIndex + 1));
		
		if (!lastCandleCreatedConfirmedStrategy) {
			return false;
		} else {
			double highestLow = candles[prevLowIndex + 1].getLow();
			double lowestLow = candles[prevLowIndex + 1].getLow();
			for (int i = prevLowIndex + 1; i < lastLowIndex; i++) {
				if (candles[i].getLow() > highestLow) {
					highestLow = candles[i].getLow();
				} else if(candles[i].getLow() < lowestLow) {
					lowestLow = candles[i].getLow();
				} 
			}
			if (((highestLow - lowestLow) < (5 * pipsValue)) && ((lastLowIndex - prevLowIndex) > 8)) {
				return false;
			}
		}
		
		boolean secondRSILower = candles[highIndex].getRsi() < candles[firstCandleIndex].getRsi();

//		this.risk = candles[highIndex].getHigh() - candles[prevLowIndex].getLow();
		this.stopLoss = candles[highIndex].getHigh();
		this.confirmationPoint = candles[index].getClose();//candles[prevLowIndex].getLow();

		boolean isShortReversal = lastCandleCreatedConfirmedStrategy && secondRSILower && !isShortSimpleBollingerReversal(candles, index - 1, pipsValue) &&
				!isBollinger10OutsideBollinger20(candles, firstCandleIndex, pipsValue);
		
		if (isShortReversal) {
			this.highIndex = highIndex;
		}
		
		return isShortReversal;
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
	
	public boolean isBollinger10OutsideBollinger20(SingleCandleBarData[] candles, int index, double pipsValue) {
		return (candles[index].getHigher10Bollinger() > candles[index].getHigher20Bollinger() + pipsValue) &&
				(candles[index].getLower10Bollinger() + pipsValue < candles[index].getLower20Bollinger());
	}

	

}
