package com.algotrado.entry.strategy;


public class EntryStrategyDataObject {

	private EntryStrategyManager entry;
	private Double statistics;
	
	public EntryStrategyDataObject(EntryStrategyManager entry, Double statistics) {
		super();
		this.entry = entry;
		this.statistics = statistics;
	}

	public EntryStrategyManager getEntry() {
		return entry;
	}

	public Double getStatistics() {
		return statistics;
	}
}
