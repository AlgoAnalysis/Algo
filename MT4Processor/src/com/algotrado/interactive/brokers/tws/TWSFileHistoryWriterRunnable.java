package com.algotrado.interactive.brokers.tws;

import java.util.List;

import com.ib.controller.Bar;

public class TWSFileHistoryWriterRunnable implements Runnable {
	
	private List<Bar> bars;
	
	public TWSFileHistoryWriterRunnable(List<Bar> bars) {
		super();
		this.bars = bars;
	}
	
	@Override
	public void run() {
		TWSFileWriterSingleton.writeDataToFile(bars);
	}
	
}