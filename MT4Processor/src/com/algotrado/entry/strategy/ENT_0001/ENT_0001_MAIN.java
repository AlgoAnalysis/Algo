package com.algotrado.entry.strategy.ENT_0001;

import com.algotrado.entry.strategy.IEntryStrategyState;

public abstract class ENT_0001_MAIN implements  IEntryStrategyState{
	static protected final String name = "Harami";
	static protected final String code = "ENT-0001";
	
	protected Object[] parameters;
	
	public ENT_0001_MAIN(Object[] parameters) {
		this.parameters = parameters;
	}
}
