package com.algotrado.pattern;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;

public abstract class APatternState {

	
	public abstract void setNewData(NewUpdateData[] newData);
	public abstract APatternState getNextState();
	
	/**
	 * @return - the class that the state work on (JapaneseCandleBar , RSI and etc. )
	 */
	
	public abstract Integer getStateNumber();
	public abstract Date getTriggerTime();
	public abstract PatternStateStatus getStatus();
	
	// need to be static
	public abstract Object[] getNewDataClass();
	public abstract String getName();
	public abstract String getCode();
	public abstract String getDescription();
	public abstract Integer getNumberOfStates();
	public abstract Object[] getParameterMinValue();
	public abstract Object[] getParameterMaxValue();
	public abstract String[] getParameterStringList();
	public abstract boolean checkValidParametrs(Object[] parameters);
	
	
	protected PatternManager patternManager;
	public void setPatternManager(PatternManager patternManager) {
		this.patternManager = patternManager;
	}
	
}
