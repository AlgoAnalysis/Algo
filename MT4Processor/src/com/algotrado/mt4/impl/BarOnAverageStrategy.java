package com.algotrado.mt4.impl;


public class BarOnAverageStrategy extends GeneralBarStrategy {
	
	private static final double PINBAR_SIZE_RATE = 1;
	private static final double MAX_SQUARE_PART_ANTI_DIRECTION = 6;
	private static final int MAX_BARS_TO_ACTIVATE_STRATEGY = 5;
	private static final double MAX_SQUARE_PART_PRO_DIRECTION = 2;
	private static final double MAX_SQUARE_PART_NO_DIRECTION = 2.5;
	private static final double SQUARE_SIZE_CONSTANT = 15.9;
	private double NUM_OF_CANDLES_IN_SCREEN = 164;

	@Override
	public boolean isStrategyPattern(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		return (isLongStrategyPattern(japaneseCandleBar, previousCandles, index, pipsValue) || 
				isShortStrategyPattern(japaneseCandleBar, previousCandles, index, pipsValue)) /*&& 
				areBollingerWidening(japaneseCandleBar, previousCandles, index, pipsValue)*/;
	}
	
	public boolean areBollingerWidening(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		return false;
	}
	
	public boolean isShortStrategyPattern(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		if (index < 2) return false;
		boolean isShortPinbar = PinbarCandle.isBearishPinbar(japaneseCandleBar, PINBAR_SIZE_RATE);
		boolean isShortOKR = OKRCandle.isBearishBar(japaneseCandleBar, previousCandles[index - 1], pipsValue);
		return (isShortPinbar || isShortOKR) && didBearishBarCreateNewHigh(previousCandles, index, pipsValue) && 
				isMAShortCurve(japaneseCandleBar, previousCandles, index) && didBearishPinbarGetActivated(previousCandles, index, MAX_BARS_TO_ACTIVATE_STRATEGY);
	}
	
	public boolean isLongStrategyPattern(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		if (index < 2) return false;
		boolean isLongPinbar = PinbarCandle.isBullishPinbar(japaneseCandleBar, PINBAR_SIZE_RATE) ;
		boolean isLongOKR = OKRCandle.isBullishBar(japaneseCandleBar, previousCandles[index - 1], pipsValue);
		return (isLongPinbar || isLongOKR) && didBullishBarCreateNewLow(previousCandles, index, pipsValue) && 
				isMALongCurve(japaneseCandleBar, previousCandles, index) && didBullishPinbarGetActivated(previousCandles, index, MAX_BARS_TO_ACTIVATE_STRATEGY);
	}
	
	/**
	 * Idea for ma long slope/curve get a curve that is from at least 5 last candles + last candle toucing MA curve.
	 * @param japaneseCandleBar
	 * @param previousCandles
	 * @param index
	 * @return
	 */
	public boolean isMALongCurve(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index) {
		if (previousCandles.length < NUM_OF_CANDLES_IN_SCREEN || index <= NUM_OF_CANDLES_IN_SCREEN) {
			return false;
		}
		
		double squareSize = getScreenSquareSizeInPips(japaneseCandleBar, previousCandles, index);
		
		double highest = previousCandles[index].getSMA20(), lowest = previousCandles[index].getSMA20();
		
		for (int i = 1; i < NUM_OF_CANDLES_IN_SCREEN; i++) {
			if (i <= 12) {
				highest = (highest < previousCandles[index - i].getSMA20()) ? previousCandles[index - i].getSMA20() : highest;
				lowest = (lowest > previousCandles[index - i].getSMA20()) ? previousCandles[index - i].getSMA20() : lowest;
			} else if ((highest - lowest) < (squareSize / MAX_SQUARE_PART_NO_DIRECTION)) {
				return false;//Not short or long
			}
			if (previousCandles[index - i].getSMA20() - previousCandles[index].getSMA20() > ((double)squareSize/MAX_SQUARE_PART_ANTI_DIRECTION)) {
				return false;
			} else if (previousCandles[index].getSMA20() - previousCandles[index - i].getSMA20() > ((double)squareSize/MAX_SQUARE_PART_PRO_DIRECTION)) {
				boolean isCandleTouchingAverage = japaneseCandleBar.getHigh() >= japaneseCandleBar.getSMA20() && 
						japaneseCandleBar.getLow() <= japaneseCandleBar.getSMA20();
				return isCandleTouchingAverage && arePrevCandlesFarFromAverage(previousCandles, index, true) ;
			}
		}
		
		return false;//isCandleTouchingAverage && arePrevCandlesFarFromAverage(previousCandles, index, true);
	}
	
	/**
	 * Checks if there are at least 2 candles disconnected from average, in the correct direction of the trend.
	 * @param previousCandles
	 * @param index
	 * @param isLongTrend
	 * @return
	 */
	private boolean arePrevCandlesFarFromAverage(JapaneseCandleBar[] previousCandles, int index, boolean isLongTrend) {
		if (index < 2) {
			return false;
		}
		boolean prevCandleAboveAverage;
		boolean twoBeforeCandleAboveAverage;
		if (isLongTrend) {
			prevCandleAboveAverage = previousCandles[index - 1].getLow() > previousCandles[index - 1].getSMA20();
			twoBeforeCandleAboveAverage = previousCandles[index - 2].getLow() > previousCandles[index - 2].getSMA20();
		} else {
			prevCandleAboveAverage = previousCandles[index - 1].getHigh() < previousCandles[index - 1].getSMA20();
			twoBeforeCandleAboveAverage = previousCandles[index - 2].getHigh() < previousCandles[index - 2].getSMA20();
		}
		
		if (prevCandleAboveAverage && twoBeforeCandleAboveAverage) {
			return true;
		} else if (!prevCandleAboveAverage && !twoBeforeCandleAboveAverage) {
			return false;
		} else {
			return arePrevCandlesFarFromAverage(previousCandles, index - 1, isLongTrend);
		}
	}
	
	/**
	 * Idea for ma short slope/curve get a curve that is from at least 5 last candles + last candle toucing MA curve.
	 * @param japaneseCandleBar
	 * @param previousCandles
	 * @param index
	 * @return
	 */
	public boolean isMAShortCurve(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index) {
		if (previousCandles.length < NUM_OF_CANDLES_IN_SCREEN || index <= NUM_OF_CANDLES_IN_SCREEN) {
			return false;
		}
		
		double squareSize = getScreenSquareSizeInPips(japaneseCandleBar, previousCandles, index);
		
		double highest = previousCandles[index].getSMA20(), lowest = previousCandles[index].getSMA20();
		
		for (int i = 1; i < NUM_OF_CANDLES_IN_SCREEN; i++) {
			if (i <= 12) {
				highest = (highest < previousCandles[index - i].getSMA20()) ? previousCandles[index - i].getSMA20() : highest;
				lowest = (lowest > previousCandles[index - i].getSMA20()) ? previousCandles[index - i].getSMA20() : lowest;
			} else if ((highest - lowest) < (squareSize / MAX_SQUARE_PART_NO_DIRECTION)) {
				return false;//Not short or long
			}
			if (previousCandles[index].getSMA20() - previousCandles[index - i].getSMA20() > ((double)squareSize/MAX_SQUARE_PART_ANTI_DIRECTION)) {
				return false;
			} else if (previousCandles[index - i].getSMA20() - previousCandles[index].getSMA20() > ((double)squareSize/MAX_SQUARE_PART_PRO_DIRECTION)) {
				boolean isCandleTouchingAverage = japaneseCandleBar.getHigh() >= japaneseCandleBar.getSMA20() && 
						japaneseCandleBar.getLow() <= japaneseCandleBar.getSMA20();
				return isCandleTouchingAverage && arePrevCandlesFarFromAverage(previousCandles, index, false);
			}
		}
		
		return false;
//		if (previousCandles.length < 5 || index <= 5) {
//			return false;
//		}
//		for (int i = index; i > index - 5; i--) {
//			if (previousCandles[i].getMa() >= previousCandles[i - 1].getMa()) {
//				return false;
//			}
//		}
//		boolean isCandleTouchingAverage = japaneseCandleBar.getHigh() >= japaneseCandleBar.getMa() && 
//				japaneseCandleBar.getLow() <= japaneseCandleBar.getMa();
//		return isCandleTouchingAverage && arePrevCandlesFarFromAverage(previousCandles, index, false);
	}
	
	/**
	 * 
	 * @param japaneseCandleBar
	 * @param previousCandles
	 * @param index
	 * @return calculate and return the square size in MT4 screen.
	 */
	public double getScreenSquareSizeInPips(JapaneseCandleBar japaneseCandleBar,
			JapaneseCandleBar[] previousCandles, int index) {
		double highestValue = previousCandles[index - 1].getHigh(), lowestValue = previousCandles[index - 1].getLow();
		if (previousCandles.length >= NUM_OF_CANDLES_IN_SCREEN) {
			for (int i = 1; i < NUM_OF_CANDLES_IN_SCREEN; i++) {
				highestValue = (previousCandles[index - i].getHigh() > highestValue) ? previousCandles[index - i].getHigh() : highestValue;
				lowestValue = (previousCandles[index - i].getLow() < lowestValue) ? previousCandles[index - i].getLow() : lowestValue;
			}
		} else {
			for (JapaneseCandleBar prevJapaneseCandleBar : previousCandles) {
				highestValue = (prevJapaneseCandleBar.getHigh() > highestValue) ? prevJapaneseCandleBar.getHigh() : highestValue;
				lowestValue = (prevJapaneseCandleBar.getLow() < lowestValue) ? prevJapaneseCandleBar.getLow() : lowestValue;
			}
		}
		return ((double)(highestValue - lowestValue) / SQUARE_SIZE_CONSTANT);
	}

}
