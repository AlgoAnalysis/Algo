package com.algotrado.pattern.PTN_0001;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0001_S1 extends PTN_0001_Main{

	protected final Integer statesNumber = Integer.valueOf(1);
	private JapaneseCandleBar japanese;
	
	public PTN_0001_S1(Object[] parameters) {
		super(parameters);
		status = PatternStateStatus.WAIT_TO_START;
	}

	@Override
	public void setNewData(Object[] newData) {
		if(status == PatternStateStatus.WAIT_TO_START)
		{
			japanese = (JapaneseCandleBar) newData[0];
			status = PatternStateStatus.RUN_TO_NEXT_STATE;
		}
		else
		{
			status = PatternStateStatus.ERROR;
			throw new RuntimeException	(""); // TODO 
		}
	}

	@Override
	public APatternState getNextState() {
		return new PTN_0001_S2(parameters,japanese);
	}

}
