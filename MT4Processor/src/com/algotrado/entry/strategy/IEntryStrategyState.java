package com.algotrado.entry.strategy;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;

public interface IEntryStrategyState {
	
	public void setNewData(NewUpdateData[] newData);
	
	public EntryStrategyStateStatus getStatus();
	
	public IEntryStrategyState getNextState();
	
	public Date getTriggerTime();
	
	public Integer getStateNumber();

}
