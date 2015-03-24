package com.algotrado.pattern.PTN_0003;

import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternParameterList;

public abstract class PTN_0003_Main implements IPatternState {

	static protected final String name = "Harami";
	static protected final String code = "PTN-0003";
	static protected final String description = ""; // TODO
	static protected final Integer numberOfStates = Integer.valueOf(2);
	static protected final String[] parameterStringList = {};
	static protected final Object[] parameterMinValue = {};
	static protected final Object[] parameterMaxValue = {};
	static protected final Object[] newDataClass = {JapaneseCandleBar.class};
	static protected final PatternParameterList parametersList = new PTN_0003_Parameters(); // TODO - need to check
	
	Object[] parameters;
	
	public PTN_0003_Main(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public PTN_0003_Main(int index)
	{
		this.parameters  = parametersList.getParametersFromIndex(index);
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Integer getNumberOfStates() {
		return numberOfStates;
	}
	
	public String[] getParameterStringList() {
		return parameterStringList;
	}

	public Object[] getParameterMinValue() {
		return parameterMinValue;
	}

	public Object[] getParameterMaxValue() {
		return parameterMaxValue;
	}

	public Object[] getNewDataClass() {
		return newDataClass;
	}
	
	public boolean checkValidParametrs(Object[] parameters)
	{
		// TODO 
		return true;
	}
}
