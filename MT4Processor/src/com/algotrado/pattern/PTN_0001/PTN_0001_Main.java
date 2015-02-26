package com.algotrado.pattern.PTN_0001;


import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternParameterList;

public abstract class PTN_0001_Main extends APatternState{

	static protected final String name = "Harami";
	static protected final String code = "PTN-0001";
	static protected final String description = ""; // TODO
	static protected final Integer numberOfStates = Integer.valueOf(2);
	static protected final String[] parameterList = {"The second candle percent"};
	static protected final Object[] parameterMinValue = {Float.valueOf(0)};
	static protected final Object[] parameterMaxValue = {Float.valueOf(1)};
	static protected final Object[] newDataClass = {JapaneseCandleBar.class};
	static protected final PatternParameterList parametersList = new PTN_0001_Parameters(); // TODO - need to check
	
	public PTN_0001_Main(Object[] parameters) {
		super(parameters);
	}
	
	public PTN_0001_Main(int index)
	{
		super(index);
	}

}
