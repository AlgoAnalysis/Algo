package com.algotrado.entry.strategy;

import java.util.Date;
import java.util.List;


public class EntryStrategyDataObject {

	private Double statistics;
	private List<Date> entryDates;
	private EntryStrategyManagerStatus entryStrategyManagerStatus;
	private String entryDataHeaders;
	
	public EntryStrategyDataObject(List<Date> entryDates, Double statistics, EntryStrategyManagerStatus entryStrategyManagerStatus,
			String entryDataHeaders) {
		super();
		this.entryDates = entryDates;
		this.statistics = statistics;
		this.entryStrategyManagerStatus = entryStrategyManagerStatus;
		this.entryDataHeaders = entryDataHeaders;
	}

	public List<Date> getEntryDates() {
		return entryDates;
	}

	public Double getStatistics() {
		return statistics;
	}
	
	public EntryStrategyManagerStatus getStatus() {
		return entryStrategyManagerStatus;
	}
	
	public String getDataHeaders() {
		return this.entryDataHeaders;
	}
}
