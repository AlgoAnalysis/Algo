package com.algotrado.entry.strategy.ENT_0001;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyFirstState;
import com.algotrado.entry.strategy.IEntryStrategyState;

public class ENT_0001_S1 extends ENT_0001_MAIN implements IEntryStrategyFirstState {

	public ENT_0001_S1(Object[] parameters) {
		super(parameters);
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		
	}

	@Override
	public IEntryStrategyState getCopyPatternState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntryStrategyStateStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
