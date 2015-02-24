package com.algotrado.pattern;

import java.sql.Time;

public abstract class APatternState {

	// The follow variables is for Testing and Monitoring.
	static protected String name;
	static protected String code;
	static protected String description;
	static protected Integer numberOfStates;
	protected Integer statesNumber;
	static protected String[] parameterStringList;
	static protected Object[] parameterMinValue;
	static protected Object[] parameterMaxValue;
	
	protected Time trigerTime;
	
	// The follow variables for work.
	static protected Object[] newDataClass;
	static protected PatternParameterList parametersList;
	protected PatternStateStatus status;
	protected Object[] parameters;
	

	// parameters constructor 
	public APatternState(Object[] parameters)
	{
		this.parameters = parameters;
	}
	
	// index parameter constructor
	public APatternState(int index)
	{
		this(parametersList.getParametersFromIndex(index));
	}
	
	// copy constructor
	public APatternState(APatternState patternState)
	{
		this(patternState.getParameters());
	}
	
	public abstract void setNewData(Object[] newData);
	public abstract APatternState getNextState();
	
	/**
	 * @return - the class that the state work on (JapaneseCandleBar , Float, RSI and etc. )
	 */
	public Object[] getNewDataClass()
	{
		return newDataClass;
	}
	
	public static boolean checkValidParametrs(Object[] parameters)
	{
		return true; // TODO
	}
	
	
	
	public static String getName() {
		return name;
	}
	public static String getCode() {
		return code;
	}
	public static String getDescription() {
		return description;
	}
	public static Integer getNumberOfStates() {
		return numberOfStates;
	}
	public Integer getStatesNumber() {
		return statesNumber;
	}
	public static String[] getParameterStringList() {
		return parameterStringList;
	}
	public Time getTrigerTime() {
		return trigerTime;
	}
	public PatternStateStatus getStatus()
	{
		return status;
	}
	public static Object[] getParameterMinValue() {
		return parameterMinValue;
	}
	public static Object[] getParameterMaxValue() {
		return parameterMaxValue;
	}
	public Object[] getParameters() {
		return parameters;
	}
	
}
