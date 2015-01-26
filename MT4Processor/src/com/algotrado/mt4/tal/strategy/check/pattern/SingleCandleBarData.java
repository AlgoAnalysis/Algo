package com.algotrado.mt4.tal.strategy.check.pattern;

import java.util.Date;

import com.algotrado.mt4.impl.JapaneseCandleBar;

public class SingleCandleBarData extends JapaneseCandleBar {

	protected double lower20Bollinger, higher20Bollinger, lower10Bollinger, sma10, higher10Bollinger, rsi;
	
	public SingleCandleBarData(double open, double close, double high,
			double low, Date time, String commodityName, double sma20, double lower20Bollinger, double higher20Bollinger, double sma10,
			double lower10Bollinger, double higher10Bollinger, double rsi) {
		super(open, close, high, low, time, commodityName, sma20);
		this.rsi = rsi;
		this.lower20Bollinger = lower20Bollinger;
		this.higher20Bollinger = higher20Bollinger;
		this.lower10Bollinger = lower10Bollinger;
		this.higher10Bollinger = higher10Bollinger;
		this.sma10 = sma10;
	}
	
	public SingleCandleBarData(SingleCandleBarData japaneseCandleBar) {
		this(japaneseCandleBar.open, japaneseCandleBar.close, japaneseCandleBar.high, japaneseCandleBar.low, japaneseCandleBar.getTime(), japaneseCandleBar.getCommodityName(), japaneseCandleBar.getSMA20(), 
				japaneseCandleBar.lower20Bollinger, japaneseCandleBar.higher20Bollinger, japaneseCandleBar.sma10,
				japaneseCandleBar.lower10Bollinger, japaneseCandleBar.higher10Bollinger, japaneseCandleBar.rsi);
	}

	public double getLower20Bollinger() {
		return lower20Bollinger;
	}

	public void setLower20Bollinger(double lower20Bollinger) {
		this.lower20Bollinger = lower20Bollinger;
	}

	public double getHigher20Bollinger() {
		return higher20Bollinger;
	}

	public void setHigher20Bollinger(double higher20Bollinger) {
		this.higher20Bollinger = higher20Bollinger;
	}

	public double getLower10Bollinger() {
		return lower10Bollinger;
	}

	public void setLower10Bollinger(double lower10Bollinger) {
		this.lower10Bollinger = lower10Bollinger;
	}

	public double getSma10() {
		return sma10;
	}

	public void setSma10(double sma10) {
		this.sma10 = sma10;
	}

	public double getHigher10Bollinger() {
		return higher10Bollinger;
	}

	public void setHigher10Bollinger(double higher10Bollinger) {
		this.higher10Bollinger = higher10Bollinger;
	}

	public double getRsi() {
		return rsi;
	}

	public void setRsi(double rsi) {
		this.rsi = rsi;
	}
	
	

}
