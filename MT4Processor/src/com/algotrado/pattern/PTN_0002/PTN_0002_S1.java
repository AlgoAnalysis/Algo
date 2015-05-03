package com.algotrado.pattern.PTN_0002;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0002_S1 extends PTN_0002_Main implements IPatternFirstState {

	protected final Integer stateNumber = Integer.valueOf(1);
	private JapaneseCandleBar japanese;
	private Date startTime;
	private Date triggerTime;
	private PatternStateStatus status;
	
	public PTN_0002_S1(Object[] parameters) {
		super(parameters);
		status = PatternStateStatus.WAIT_TO_START;
	}

	public PTN_0002_S1(int index) {
		super(index);
		status = PatternStateStatus.WAIT_TO_START;
	}
	
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == PatternStateStatus.WAIT_TO_START)
		{
			japanese = (JapaneseCandleBar)newData[0];
			startTime = newData[0].getTime();
			triggerTime = newData[0].getTime();
			status = PatternStateStatus.RUN_TO_NEXT_STATE;
		}
		else
		{
			status = PatternStateStatus.ERROR;
			throw new RuntimeException	(""); // TODO 
		}
	}

	@Override
	public IPatternState getNextState() {
		return new PTN_0002_S2(parameters,japanese);
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public IPatternState getCopyPatternState() {
		return new PTN_0002_S1(parameters);
	}

	public Integer getStateNumber() {
		return stateNumber;
	}

	public PatternStateStatus getStatus() {
		return status;
	}

	public Date getTriggerTime() {
		return triggerTime;
	}

}
