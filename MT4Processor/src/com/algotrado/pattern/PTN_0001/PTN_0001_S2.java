package com.algotrado.pattern.PTN_0001;


import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0001_S2 extends PTN_0001_Main implements IPatternLastState{
	
	protected final Integer stateNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private double maxBodySize;
	private PatternStateStatus status;
	private Date triggerTime;
	
	public PTN_0001_S2(Object[] parameters,PatternManager patternManager,JapaneseCandleBar firstCandle) {
		super(parameters,patternManager);
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
				triggerTime = secondCandle.getTime();
				status =  PatternStateStatus.TRIGGER_NOT_SPECIFIED;
				patternManager.patternTrigger(PatternManagerStatus.TRIGGER_NOT_SPECIFIED);
//				status = (firstCandle.isBullishBar()) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				
			}
			else {
				status = PatternStateStatus.KILL_STATE;
				patternManager.patternKillState();
			}
		}
		else if(status == PatternStateStatus.TRIGGER_NOT_SPECIFIED ||
		status == PatternStateStatus.TRIGGER_BEARISH ||
		status == PatternStateStatus.TRIGGER_BULLISH) {
			status = PatternStateStatus.KILL_STATE;
			patternManager.patternKillState();
		}
		else {
			throw new RuntimeException	("Pattern in error state");
			
		}	
	}

	@Override
	public APatternState getNextState() {
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

	@Override
	public double getPatternHigh() {
		return firstCandle.getHigh();
	}

	@Override
	public double getPatternLow() {
		return firstCandle.getLow();
	}

	@Override
	public Date getTime() {
		return firstCandle.getTime();
	}

	@Override
	public String getAssetName() {
		return firstCandle.getAssetName();
	}

	@Override
	public String getDataHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Date getStartPatternTime() {
		return firstCandle.getTime();
	}

}
