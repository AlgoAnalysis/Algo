package com.algotrado.extract.data;

import java.util.ArrayList;
import java.util.Collection;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class CandleBarsCollection implements NewUpdateData {
	private Collection<SingleCandleBarData> candleBars;

	public CandleBarsCollection() {
		super();
		candleBars = new ArrayList<SingleCandleBarData>();
	}
	
	public void addCandleBar(SingleCandleBarData candleBar) {
		candleBars.add(candleBar);
	}
	
	public Collection<SingleCandleBarData> getCandleBars(){
		return candleBars;
	}
	
	
	
}
