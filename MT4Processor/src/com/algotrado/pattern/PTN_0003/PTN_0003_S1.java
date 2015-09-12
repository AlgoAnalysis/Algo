package com.algotrado.pattern.PTN_0003;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0003_S1 extends PTN_0003_Main implements IPatternFirstState {

	protected final Integer stateNumber = Integer.valueOf(1);
	private JapaneseCandleBar japanese;
	private Date startTime;
	private Date triggerTime;
	private PatternStateStatus status;
	
	public PTN_0003_S1(Object[] parameters,PatternManager patternManager) {
		super(parameters,patternManager);
		status = PatternStateStatus.WAIT_TO_START;
	}
	
	public PTN_0003_S1(Object[] parameters) {
		super(parameters);
		status = PatternStateStatus.WAIT_TO_START;
	}

	public PTN_0003_S1(int index) {
		super(index);
		status = PatternStateStatus.WAIT_TO_START;
	}
	
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == PatternStateStatus.WAIT_TO_START)
		{
			japanese = (JapaneseCandleBar)newData[0];
			if(japanese.getBodySize() > 0)
			{
				startTime = newData[0].getTime();
				triggerTime = newData[0].getTime();
				status = PatternStateStatus.RUN_TO_NEXT_STATE;
				patternManager.patternRunToNextState();
			}
			else
			{
				patternManager.patternWaitToStart();
			}
		}
		else
		{
			throw new RuntimeException	("Pattern worng status (probably not go to next sate)");
		}
	}

	@Override
	public APatternState getNextState() {
		return new PTN_0003_S2(parameters,patternManager,japanese);
	}

	@Override
	public Date getStartTime() {
		return startTime;
	}

	@Override
	public APatternState getCopyPatternState() {
		return new PTN_0003_S1(parameters,patternManager);
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
