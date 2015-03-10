package com.algotrado.entry.strategy;

import com.algotrado.data.event.NewUpdateData;

public interface IEntryStrategyState {
	
	public void setNewData(NewUpdateData[] newData);
	
	public EntryStrategyStateStatus getStatus();

}
