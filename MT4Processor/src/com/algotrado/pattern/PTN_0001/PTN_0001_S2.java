package com.algotrado.pattern.PTN_0001;


import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.PatternStateStatus;
import com.algotrado.util.DebugUtil;

public class PTN_0001_S2 extends PTN_0001_Main{
	
	protected final Integer statesNumber = Integer.valueOf(2);
	private JapaneseCandleBar firstCandle;
	private double maxBodySize;
	
	public PTN_0001_S2(Object[] parameters,JapaneseCandleBar firstCandle) {
		super(parameters);
		Float percent = (Float) parameters[0];
		status = PatternStateStatus.RUN;
		this.firstCandle = firstCandle;
		maxBodySize = firstCandle.getBodySize() * ((float)1 - percent.floatValue());
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {	
		if(status == PatternStateStatus.RUN)
		{
			JapaneseCandleBar secondCandle = (JapaneseCandleBar)newData[0];
			if((secondCandle.getBodySize() <= maxBodySize) &&
			(secondCandle.getHigh() <= firstCandle.getHigh()) &&
			(secondCandle.getLow() >= firstCandle.getLow()))	{
				status = (firstCandle.isBullishBar()) ? PatternStateStatus.TRIGGER_BEARISH :PatternStateStatus.TRIGGER_BULLISH;
				trigerTime = secondCandle.getTime();
			}
			else {
				status = PatternStateStatus.KILL_STATE;
			}
		}
		else if(status == PatternStateStatus.TRIGGER_NOT_SPECIFIED ||
		status == PatternStateStatus.TRIGGER_BEARISH ||
		status == PatternStateStatus.TRIGGER_BULLISH) {
			status = PatternStateStatus.ALREADY_TRIGGERD;
		}
		else {
			status = PatternStateStatus.ERROR;
			if(DebugUtil.debugPatternChecking){
				throw new RuntimeException	("Pattern in error state");
			}
		}	
	}

	@Override
	public APatternState getNextState() {
		return null;
	}

}
