package com.algotrado.mt4.impl;


public class PinbarStrategy extends GeneralBarStrategy {

	public boolean isStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		return isLongStrategyPattern(japaneseCandleBar, previousCandles, index, pipsValue) || 
				isShortStrategyPattern(japaneseCandleBar, previousCandles, index, pipsValue);
	}
	
	private boolean didBarBodyClosedInsidePreviousBar(JapaneseCandleBar[] previousCandles, int index) {
		return previousCandles[index].getOpen() >= previousCandles[index - 1].getLow() &&
				previousCandles[index].getOpen() <= previousCandles[index - 1].getHigh() &&
						previousCandles[index].getClose() >= previousCandles[index - 1].getLow() &&
						previousCandles[index].getClose() <= previousCandles[index - 1].getHigh();
	}

	

	public boolean isShortStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		if (index < 2 || previousCandles.length < index + 1) {
			return false;
		}
		return PinbarCandle.isBearishPinbar(japaneseCandleBar, 2.5) && previousCandles[index - 2].isBullishBar() && !previousCandles[index - 2].isDojiCandle()
				&& previousCandles[index - 1].isBullishBar() && !previousCandles[index - 1].isDojiCandle() && 
				didBearishPinbarGetActivated(previousCandles, index, -1) && didBarBodyClosedInsidePreviousBar(previousCandles, index) &&
				didBearishBarCreateNewHigh(previousCandles, index, pipsValue);
	}
	
	public boolean isLongStrategyPattern(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar[] previousCandles, int index, double pipsValue) {
		if (index < 2 || previousCandles.length < index + 1) {
			return false;
		}
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
//		try {
//			if (japaneseCandleBar.getTime().after(formatter.parse("2013.12.02")) && japaneseCandleBar.getTime().before(formatter.parse("2013.12.04"))) {
//				System.out.println(japaneseCandleBar);
//				System.out.println("isBullishPinbar=" + isBullishPinbar(japaneseCandleBar));
//				System.out.println("isBearishPinbar=" + isBearishPinbar(japaneseCandleBar));
//			}
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return PinbarCandle.isBullishPinbar(japaneseCandleBar, 2.5) && previousCandles[index - 2].isBearishBar() && !previousCandles[index - 2].isDojiCandle()
				&& previousCandles[index - 1].isBearishBar() && !previousCandles[index - 1].isDojiCandle() && 
				didBullishPinbarGetActivated(previousCandles, index, -1) && japaneseCandleBar.pinbarStrategy.didBarBodyClosedInsidePreviousBar(previousCandles, index) &&
				didBullishBarCreateNewLow(previousCandles, index, pipsValue);
	}

}
