package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyFirstState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;

public class ENT_0001_S1 extends ENT_0001_MAIN implements IEntryStrategyFirstState {

	protected final Integer stateNumber = Integer.valueOf(1);
	private EntryStrategyStateStatus status;
	private PatternManager patternManager;
	private PatternManagerStatus patternManagerStatus = null;
	private double prevHigh = 0;
	private double prevLow = 0;
	private double maxPatternHigh = 0;
	private double minPatternLow = 0;
	
	public ENT_0001_S1(Object[] parameters) {
		super(parameters);
		status = EntryStrategyStateStatus.WAIT_TO_START;
		PTN_0001_S1 state = new PTN_0001_S1((Integer)parameters[0]);
		patternManager = new PatternManager(state);
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == EntryStrategyStateStatus.WAIT_TO_START || status == EntryStrategyStateStatus.RUN)
		{
			patternManagerStatus = null;
			patternManager.setNewData(newData);
			if((patternManager.getStatus() == PatternManagerStatus.TRIGGER_BEARISH) || 
					(patternManager.getStatus() == PatternManagerStatus.TRIGGER_BULLISH) ||
					(patternManager.getStatus() == PatternManagerStatus.TRIGGER_NOT_SPECIFIED))
			{
				patternManagerStatus = patternManager.getStatus();
				status = EntryStrategyStateStatus.RUN_TO_NEXT_STATE;
				maxPatternHigh = (((JapaneseCandleBar)newData[0]).getHigh() > prevHigh) ? ((JapaneseCandleBar)newData[0]).getHigh() : prevHigh;
				minPatternLow = (((JapaneseCandleBar)newData[0]).getLow() > prevLow) ? ((JapaneseCandleBar)newData[0]).getLow() : prevLow;
			} else if (patternManager.getStatus() == PatternManagerStatus.ERROR) {
				status = EntryStrategyStateStatus.ERROR;
				throw new RuntimeException	("Error Occoured in Pattern Manager."); // TODO 
			} else {
				status = EntryStrategyStateStatus.RUN;
			}
			prevHigh = ((JapaneseCandleBar)newData[0]).getHigh();
			prevLow = ((JapaneseCandleBar)newData[0]).getLow();
		}
		else
		{
			status = EntryStrategyStateStatus.ERROR;
			throw new RuntimeException	("Error Occoured in ENT_0001_S1."); // TODO 
		}
	}

	@Override
	public IEntryStrategyState getCopyPatternState() {
		return new ENT_0001_S1(parameters);
	}

	@Override
	public EntryStrategyStateStatus getStatus() {
		return status;
	}

	@Override
	public IEntryStrategyState getNextState() {
		return new ENT_0001_S2(parameters, patternManagerStatus, maxPatternHigh, minPatternLow);
	}

	@Override
	public Date getStartTime() {
		return patternManager.getTimeListofTriggerState().get(0);
	}

	@Override
	public Date getTriggerTime() {
		return patternManager.getTimeListofTriggerState().get(patternManager.getTimeListofTriggerState().size() - 1);
	}

	@Override
	public Integer getStateNumber() {
		return stateNumber;
	}

}
