package com.algotrado.data.event.basic.japanese;

import java.util.Date;

import com.algotrado.util.Setting;



public class JapaneseCandleBar extends AbstractCandleBar {
	public static final String _5_MINUTES = "5 Minutes";
	public static final String _15_MINUTES = "15 Minutes";
	public static final String _4_HOUR = "4 Hour";
	public static final String _1_DAY = "1 Day";
	public static final String _1_HOUR = "1 Hour";
	protected double open, close, high, low, volume;
	private Date time;
//	protected double sma20;
	private String commodityName = null;

	public JapaneseCandleBar(double open, double close, double high, double low, double volume, Date time, String commodityName) {
		super();
		this.open = open;
		this.close = close;
		this.high = high;
		this.low = low;
		this.time = time;
		this.commodityName = commodityName;
		this.volume = volume;
	}
	
	public JapaneseCandleBar(JapaneseCandleBar japaneseCandleBar) {
		this(japaneseCandleBar.open, japaneseCandleBar.close, japaneseCandleBar.high, japaneseCandleBar.low, japaneseCandleBar.volume, japaneseCandleBar.time, japaneseCandleBar.commodityName);
	}
	
	public JapaneseCandleBar addPreviousJapaneseCandleBar(JapaneseCandleBar previousJapaneseCandleBar) {
		return new JapaneseCandleBar(previousJapaneseCandleBar.open, this.close, (previousJapaneseCandleBar.high > this.high) ? previousJapaneseCandleBar.high : this.high,
				(previousJapaneseCandleBar.low < this.low) ? previousJapaneseCandleBar.low : this.low, previousJapaneseCandleBar.volume + this.volume, this.time, commodityName);
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

	public String getAssetName() {
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
	

	public boolean isDojiCandle() {
		return Math.abs(open - close) <= Math.abs((double)((double)high - low)/10);
	}
	
	public double getRisk() {
		double multiplierOfPips = commodityName.equals("USDJPY")? (double)100: (double)10000;
		return Math.abs(high - low) * multiplierOfPips;
	}
	
	@Override
	public String getDataHeaders() {
		return Setting.getDateTimeHeader("") + ",volume,open,high,low,close";
	}
	
	public String toString() {
//		SimpleDateFormat dateformatter = new SimpleDateFormat(Setting.getDateTimeFormat());
		String toStringRet = Setting.getDateTimeFormat(time) + "," + volume + "," + open + "," + high + "," 
				+ low + "," + close;
		return toStringRet;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getBodySize()	{
		return Math.abs(open-close);
	}
	
	public double getRangeSize()	{
		return high - low;
	}
}
