package com.algotrado.pattern;

import com.algotrado.util.DebugUtil;

public abstract class PatternParameterList {

	protected Object[][] parametersList;
	public PatternParameterList(Object[][] parametersList)
	{
		this.parametersList = parametersList;
	}
	public Object[] getParametersFromIndex(int index)
	{
		if(index != 0 && index <= parametersList.length)
		{
			return parametersList[index-1];
		}
		if(DebugUtil.debugPatternChecking)
		{
			throw new RuntimeException("parameter list error index");
		}
		return null; 
	}
}
