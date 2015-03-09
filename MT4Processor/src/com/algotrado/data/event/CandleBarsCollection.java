package com.algotrado.data.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CandleBarsCollection implements NewUpdateData {
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

	@Override
	public Date getTime() {
		return candleBars.get(0).getTime();
	}

	@Override
	public String getAssetName() {
		return candleBars.get(0).getAssetName();
	}

	@Override
	public String getDataHeaders() {
		return candleBars.get(0).getDataHeaders();
	}
	
	
	
}
