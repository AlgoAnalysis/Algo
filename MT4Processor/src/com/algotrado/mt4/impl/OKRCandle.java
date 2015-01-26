package com.algotrado.mt4.impl;

public class OKRCandle extends Candle {
	public static boolean isBullishBar(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar prevJapaneseCandleBar, double pipsValue) {
		return (japaneseCandleBar.getHigh() > prevJapaneseCandleBar.getHigh() + pipsValue && japaneseCandleBar.getLow() < prevJapaneseCandleBar.getLow() - pipsValue &&
				( (japaneseCandleBar.getOpen() < japaneseCandleBar.getClose()) ? 
						(japaneseCandleBar.getClose() > (prevJapaneseCandleBar.getOpen()) && japaneseCandleBar.getClose() > (prevJapaneseCandleBar.getClose())) : false	) ) && 
						(Candle.getBodySize(japaneseCandleBar) >= (2 * (Candle.getCandleSize(japaneseCandleBar) - Candle.getBodySize(japaneseCandleBar) ) ) ) && 
						((Math.min(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getLow()) > (japaneseCandleBar.getHigh() - Math.max(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose())));
	}
	
	public static boolean isBearishBar(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar prevJapaneseCandleBar, double pipsValue) {
		return (japaneseCandleBar.getHigh() > prevJapaneseCandleBar.getHigh() + pipsValue && japaneseCandleBar.getLow() < prevJapaneseCandleBar.getLow() - pipsValue &&
				( (japaneseCandleBar.getOpen() > japaneseCandleBar.getClose()) ? 
						(japaneseCandleBar.getClose() < (prevJapaneseCandleBar.getOpen()) && japaneseCandleBar.getClose() < (prevJapaneseCandleBar.getClose())) : false	) ) && 
						(Candle.getBodySize(japaneseCandleBar) >= (2 * (Candle.getCandleSize(japaneseCandleBar) - Candle.getBodySize(japaneseCandleBar) ) ) ) && 
						((japaneseCandleBar.getHigh() - Math.max(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose())) > (Math.min(japaneseCandleBar.getOpen(), japaneseCandleBar.getClose()) - japaneseCandleBar.getLow()));
	}
	
	public static boolean isBarCandle(JapaneseCandleBar japaneseCandleBar, JapaneseCandleBar prevJapaneseCandleBar, double pipsValue){
		return OKRCandle.isBullishBar(japaneseCandleBar, prevJapaneseCandleBar, pipsValue) || 
				OKRCandle.isBearishBar(japaneseCandleBar, prevJapaneseCandleBar, pipsValue);
	}
}
