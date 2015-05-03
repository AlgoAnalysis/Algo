package com.algotrado.entry.strategy;

import com.algotrado.util.DebugUtil;

public class EntryStrategyParameterList {
	protected Object[][] parametersList;
	public EntryStrategyParameterList(Object[][] parametersList)
	{
		this.parametersList = parametersList;
	}
	public Object[] getParametersFromIndex(int index)
	{
		if(index != 0 && index <= parametersList.length)
		{
			return parametersList[index-1];
		}
		if(DebugUtil.debugEntryStrategyChecking)
		{
			throw new RuntimeException("parameter list error index");
		}
		return null; 
	}
}
