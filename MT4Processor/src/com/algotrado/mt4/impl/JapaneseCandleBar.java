package com.algotrado.mt4.impl;

import java.util.Date;


public class JapaneseCandleBar extends AbstractCandleBar {
	public static final String _5_MINUTES = "5 Minutes";
	public static final String _15_MINUTES = "15 Minutes";
	public static final String _4_HOUR = "4 Hour";
	public static final String _1_DAY = "1 Day";
	public static final String _1_HOUR = "1 Hour";
	protected double open, close, high, low, sma20;
	private Date time;
	public PinbarStrategy pinbarStrategy;
	private String commodityName = null;

	public JapaneseCandleBar(double open, double close, double high, double low, Date time, String commodityName, double sma20) {
		super();
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.time = time;
		this.sma20 = sma20;
		pinbarStrategy = new PinbarStrategy();
		this.commodityName = commodityName;
	}
	
	public JapaneseCandleBar(JapaneseCandleBar japaneseCandleBar) {
		this(japaneseCandleBar.open, japaneseCandleBar.close, japaneseCandleBar.high, japaneseCandleBar.low, japaneseCandleBar.time, japaneseCandleBar.commodityName, japaneseCandleBar.sma20);
	}
	
	public JapaneseCandleBar addPreviousJapaneseCandleBar(JapaneseCandleBar previousJapaneseCandleBar) {
		return new JapaneseCandleBar(previousJapaneseCandleBar.open, this.close, (previousJapaneseCandleBar.high > this.high) ? previousJapaneseCandleBar.high : this.high,
				(previousJapaneseCandleBar.low < this.low) ? previousJapaneseCandleBar.low : this.low, this.time, commodityName, sma20);
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}
	
	public boolean isBullishBar() {
		return open - close < 0;
	}
	
	public boolean isBearishBar() {
		return open - close > 0;
	}
	
	public double getSMA20() {
		return sma20;
	}

	public void setSMA20(double ma20) {
		this.sma20 = ma20;
	}

	public boolean isDojiCandle() {
		return Math.abs(open - close) <= Math.abs((double)((double)high - low)/10);
	}
	
	public double getRisk() {
		double multiplierOfPips = commodityName.equals("USDJPY")? (double)100: (double)10000;
		return Math.abs(high - low) * multiplierOfPips;
	}
	
	public String toString() {
		return "time: " + time + " ,open price= " + open + " ,close price= " + close + " ,high price= " + high + " ,low price= " + low;
	}
}
