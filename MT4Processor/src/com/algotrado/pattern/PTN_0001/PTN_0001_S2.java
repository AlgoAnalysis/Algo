package com.algotrado.pattern.PTN_0001;


import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternStateStatus;
import com.algotrado.util.DebugUtil;

public class PTN_0001_S2 extends PTN_0001_Main{
	
	protected final Integer stateNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private double maxBodySize;
	private PatternStateStatus status;
	private Date triggerTime;
	
	public PTN_0001_S2(Object[] parameters,JapaneseCandleBar firstCandle) {
		super(parameters);
		Double percent = (Double) parameters[0];
		status = PatternStateStatus.RUN;
		this.firstCandle = firstCandle;
		maxBodySize = firstCandle.getBodySize() * ((double)1 - percent.doubleValue());
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {	

		if(status == PatternStateStatus.RUN)
		{
			JapaneseCandleBar secondCandle = (JapaneseCandleBar)newData[0];
			if((secondCandle.getBodySize() <= maxBodySize) &&
			(secondCandle.getHigh() <= firstCandle.getHigh()) &&
			(secondCandle.getLow() >= firstCandle.getLow()))	{
				status =  PatternStateStatus.TRIGGER_NOT_SPECIFIED;
//				status = (firstCandle.isBullishBar()) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				triggerTime = secondCandle.getTime();
			}
			else {
				status = PatternStateStatus.KILL_STATE;
			}
		}
		else if(status == PatternStateStatus.TRIGGER_NOT_SPECIFIED ||
		status == PatternStateStatus.TRIGGER_BEARISH ||
		status == PatternStateStatus.TRIGGER_BULLISH) {
			status = PatternStateStatus.KILL_STATE;
		}
		else {
			status = PatternStateStatus.ERROR;
			if(DebugUtil.debugPatternChecking){
				throw new RuntimeException	("Pattern in error state");
			}
		}	
	}

	@Override
	public IPatternState getNextState() {
		return null;
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
