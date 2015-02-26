package com.algotrado.pattern;

import com.algotrado.util.DebugUtil;

public abstract class PatternParameterList {

	static protected Object[][] parametersList; 
	public static Object[] getParametersFromIndex(int index)
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
