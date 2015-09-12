package com.algotrado.pattern.PTN_0003;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.pattern.PatternStateStatus;

public class PTN_0003_S2  extends PTN_0003_Main implements IPatternLastState{
	protected final Integer stateNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private PatternStateStatus status;
	private Date triggerTime;
	private boolean firstCandleBullish;
	
	public PTN_0003_S2(Object[] parameters,PatternManager patternManage,JapaneseCandleBar firstCandle) {
		super(parameters,patternManage);
		status = PatternStateStatus.RUN;
		this.firstCandle = firstCandle;
		firstCandleBullish = firstCandle.isBullishBar();
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {	
		if(status == PatternStateStatus.RUN)
		{
			JapaneseCandleBar secondCandle = (JapaneseCandleBar)newData[0];
			if((secondCandle.getHigh() < firstCandle.getBadyMaximum()) && (secondCandle.getLow() > firstCandle.getBadyMinimum()))
			{
				triggerTime = secondCandle.getTime();
				status = (firstCandleBullish) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				if(firstCandleBullish)
				{
					status = PatternStateStatus.TRIGGER_BEARISH;
					patternManager.patternTrigger(PatternManagerStatus.TRIGGER_BEARISH);
				}
				else
				{
					status = PatternStateStatus.TRIGGER_BULLISH;
					patternManager.patternTrigger(PatternManagerStatus.TRIGGER_BULLISH);
				}
			}
			else
			{
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
