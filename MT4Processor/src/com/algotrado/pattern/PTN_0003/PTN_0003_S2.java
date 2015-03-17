package com.algotrado.pattern.PTN_0003;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternStateStatus;
import com.algotrado.util.DebugUtil;

public class PTN_0003_S2  extends PTN_0003_Main implements IPatternLastState{
	protected final Integer stateNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private PatternStateStatus status;
	private Date triggerTime;
	private boolean firstCandleBullish;
	private boolean firstCandleBearish;
	
	public PTN_0003_S2(Object[] parameters,JapaneseCandleBar firstCandle) {
		super(parameters);
		status = PatternStateStatus.RUN;
		this.firstCandle = firstCandle;
		firstCandleBullish = firstCandle.isBullishBar();
		firstCandleBearish = firstCandle.isBearishBar();
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {	
		if(status == PatternStateStatus.RUN)
		{
			PatternStateStatus tempStatus = PatternStateStatus.KILL_STATE;
			JapaneseCandleBar secondCandle = (JapaneseCandleBar)newData[0];
			if(firstCandleBullish)			{
				if((secondCandle.getHigh() < firstCandle.getClose()) && (secondCandle.getLow() > firstCandle.getOpen()))
				{
					status = (firstCandleBullish) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				}
			}
			else if(firstCandleBearish){
				if((secondCandle.getHigh() < firstCandle.getOpen()) && (secondCandle.getLow() > firstCandle.getClose()))
				{
					status = (firstCandleBullish) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				}				
			}
			status = tempStatus;
			if(status != PatternStateStatus.KILL_STATE)
			{
				triggerTime = secondCandle.getTime();
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
