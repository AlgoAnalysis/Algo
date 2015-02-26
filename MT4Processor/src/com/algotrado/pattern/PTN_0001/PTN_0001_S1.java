package com.algotrado.pattern.PTN_0001;


import java.util.Date;

import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0001_S1 extends PTN_0001_Main implements IPatternFirstState{

	protected final Integer statesNumber = Integer.valueOf(1);
	private JapaneseCandleBar japanese;
	private Date startTime;
	
	public PTN_0001_S1(Object[] parameters) {
		super(parameters);
		status = PatternStateStatus.WAIT_TO_START;
	}

	public PTN_0001_S1(int index) {
		super(index);
		status = PatternStateStatus.WAIT_TO_START;
	}
	
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == PatternStateStatus.WAIT_TO_START)
		{
			japanese = (JapaneseCandleBar) newData[0];
			startTime = newData[0].getTime();
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

	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public APatternState getCopyPatternState() {
		return new PTN_0001_S1(parameters);
	}

}
