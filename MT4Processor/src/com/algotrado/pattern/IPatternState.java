package com.algotrado.pattern;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;

public interface IPatternState {

	
	public abstract void setNewData(NewUpdateData[] newData);
	public abstract IPatternState getNextState();
	
	/**
	 * @return - the class that the state work on (JapaneseCandleBar , Float, RSI and etc. )
	 */
	
	public Integer getStateNumber();
	public Date getTriggerTime();
	public PatternStateStatus getStatus();
	
	// need to be static
	public Object[] getNewDataClass();
	public String getName();
	public String getCode();
	public String getDescription();
	public Integer getNumberOfStates();
	public Object[] getParameterMinValue();
	public Object[] getParameterMaxValue();
	public String[] getParameterStringList();
	public boolean checkValidParametrs(Object[] parameters);
}
