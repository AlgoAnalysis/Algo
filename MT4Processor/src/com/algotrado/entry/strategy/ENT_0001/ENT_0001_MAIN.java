package com.algotrado.entry.strategy.ENT_0001;

import com.algotrado.entry.strategy.AEntryStrategyObserver;
import com.algotrado.entry.strategy.EntryStrategyParameterList;
import com.algotrado.entry.strategy.IEntryStrategyState;

public abstract class ENT_0001_MAIN implements  IEntryStrategyState{
	static protected final String name = "Harami";
	static protected final String code = "ENT-0001";
	static protected final String description = ""; // TODO
	static protected final Integer numberOfStates = Integer.valueOf(2);
	static protected final EntryStrategyParameterList parametersList = new ENT_0001_Parameters(); // TODO - need to check
	
	public static final int ENT_0001_parammetrs_order_maxRsiLongValue = 0;
	public static final int ENT_0001_parammetrs_order_minRsiShortValue = 1;
	public static final int ENT_0001_parammetrs_order_maxNumOfCandlesAfterPattern = 2;
	public static final int ENT_0001_parammetrs_order_StrategyTriggerType = 3;
	public static final int ENT_0001_parammetrs_numbers = 4;
	
	protected Object[] parameters;
	protected AEntryStrategyObserver entryStrategyObserver;
	
	public ENT_0001_MAIN(Object[] parameters,AEntryStrategyObserver entryStrategyObserver) {
		this.parameters = parameters;
		this.entryStrategyObserver = entryStrategyObserver;
	}
	
	public ENT_0001_MAIN(int index,AEntryStrategyObserver entryStrategyObserver)
	{
		this.parameters  = parametersList.getParametersFromIndex(index);
		this.entryStrategyObserver = entryStrategyObserver;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
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

	public Integer getNumberofstates() {
		return numberOfStates;
	}

	public static EntryStrategyParameterList getParameterslist() {
		return parametersList;
	}
}
