package com.algotrado.pattern.PTN_0002;

import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternParameterList;

public abstract class PTN_0002_Main extends APatternState {

	static protected final String name = "Harami";
	static protected final String code = "PTN-0002";
	static protected final String description = ""; // TODO
	static protected final Integer numberOfStates = Integer.valueOf(2);
	static protected final String[] parameterStringList = {};
	static protected final Object[] parameterMinValue = {};
	static protected final Object[] parameterMaxValue = {};
	static protected final Object[] newDataClass = {JapaneseCandleBar.class};
	static protected final PatternParameterList parametersList = new PTN_0002_Parameters(); // TODO - need to check
	
	protected Object[] parameters;
	
	public PTN_0002_Main(Object[] parameters,PatternManager patternManager ) {
		this.parameters = parameters;
		this.patternManager = patternManager;
	}
	
	public PTN_0002_Main(Object[] parameters) {
		this.parameters = parameters;
	}
	
	public PTN_0002_Main(int index)
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
