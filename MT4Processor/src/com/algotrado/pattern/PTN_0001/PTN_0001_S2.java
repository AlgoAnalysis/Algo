package com.algotrado.pattern.PTN_0001;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0001_S2 extends PTN_0001_Main{
	
	protected final Integer statesNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private Float percent;
	
	public PTN_0001_S2(Object[] parameters,JapaneseCandleBar firstCandle) {
		super(parameters);
		percent = (Float) parameters[0];
		status = PatternStateStatus.RUN;
		this.firstCandle = firstCandle;
	}

	@Override
	public void setNewData(Object[] newData) {
		JapaneseCandleBar secondCandle = (JapaneseCandleBar)newData[0];
		if(status == PatternStateStatus.RUN)
		{
			// TODO
		}
		else if(status == PatternStateStatus.TRIGGER_BEARISH ||
				status == PatternStateStatus.TRIGGER_BULLISH ||
				status == PatternStateStatus.TRIGGER_NOT_SPECIFIED)
		{
			status = PatternStateStatus.ALREADY_TRIGGERD;
		}
		else
		{
			status = PatternStateStatus.ERROR;
			throw new RuntimeException	(""); // TODO 
		}	
	}

	@Override
	public APatternState getNextState() {
		return null;
	}

}
