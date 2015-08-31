package com.algotrado.pattern.PTN_0001;


import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternParameterList;

public abstract class PTN_0001_Main extends APatternState{

	static protected final String name = "Harami";
	static protected final String code = "PTN-0001";
	static protected final String description = ""; // TODO
	static protected final Integer numberOfStates = Integer.valueOf(2);
	static protected final String[] parameterStringList = {"The second candle percent"};
	static protected final Object[] parameterMinValue = {Double.valueOf(0)};
	static protected final Object[] parameterMaxValue = {Double.valueOf(1)};
	static protected final Object[] newDataClass = {JapaneseCandleBar.class};
	static protected final PatternParameterList parametersList = new PTN_0001_Parameters(); // TODO - need to check
	
	protected Object[] parameters;
	
	public PTN_0001_Main(Object[] parameters,PatternManager patternManager) {
		this.parameters = parameters;
		this.patternManager = patternManager;
	}
	
	public PTN_0001_Main(Object[] parameters) {
		this.parameters = parameters;
		this.patternManager = null;
	}
	
	public PTN_0001_Main(int index)
	{
		this.parameters  = parametersList.getParametersFromIndex(index);
		this.patternManager = null;
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
