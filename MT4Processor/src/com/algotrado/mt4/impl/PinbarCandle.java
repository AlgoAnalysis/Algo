package com.algotrado.mt4.impl;

public class PinbarCandle extends Candle {

	public static boolean isBullishPinbar(JapaneseCandleBar japaneseCandleBar, double sizeRate) {
		return ((japaneseCandleBar.getOpen() >= japaneseCandleBar.getClose() && Math.abs((double)((double)japaneseCandleBar.getClose() - japaneseCandleBar.getLow())/((double)japaneseCandleBar.getClose() - japaneseCandleBar.getHigh())) > sizeRate) ||
				(japaneseCandleBar.getOpen() <= japaneseCandleBar.getClose() && Math.abs((double)((double)japaneseCandleBar.getOpen() - japaneseCandleBar.getLow())/((double)japaneseCandleBar.getOpen() - japaneseCandleBar.getHigh())) > sizeRate)) &&
				(Math.min(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) > japaneseCandleBar.getSMA20()) && 
				((Math.min(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getLow()) > (Math.max(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getHigh()));
	}
	
	public static boolean isBearishPinbar(JapaneseCandleBar japaneseCandleBar, double sizeRate) {
		return ((japaneseCandleBar.getOpen() >= japaneseCandleBar.getClose() && Math.abs((double)((double)japaneseCandleBar.getOpen() - japaneseCandleBar.getHigh())/((double)japaneseCandleBar.getOpen() - japaneseCandleBar.getLow())) > sizeRate) ||
				(japaneseCandleBar.getOpen() <= japaneseCandleBar.getClose() && Math.abs((double)((double)japaneseCandleBar.getClose() - japaneseCandleBar.getHigh())/((double)japaneseCandleBar.getClose() - japaneseCandleBar.getLow())) > sizeRate )) &&
				(Math.max(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) < japaneseCandleBar.getSMA20()) && 
				((Math.max(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getHigh()) > (Math.min(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getLow()));
					
	}
	
	public static boolean isPinbarCandle(JapaneseCandleBar japaneseCandleBar, double sizeRate){
		return PinbarCandle.isBullishPinbar(japaneseCandleBar, sizeRate) || PinbarCandle.isBearishPinbar(japaneseCandleBar, sizeRate);
	}
	
}
