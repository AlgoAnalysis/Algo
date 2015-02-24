package com.algotrado.extract.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;

public class CandleBarsCollection {
	private List<JapaneseCandleBar> candleBars;

	public CandleBarsCollection() {
		super();
		candleBars = new ArrayList<JapaneseCandleBar>();
	}
	
	public void addCandleBar(JapaneseCandleBar candleBar) {
		candleBars.add(candleBar);
	}
	
	public List<JapaneseCandleBar> getCandleBars(){
		return candleBars;
	}
	
	
	
}
