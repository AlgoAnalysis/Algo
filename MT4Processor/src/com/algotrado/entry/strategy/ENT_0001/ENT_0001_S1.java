package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.entry.strategy.AEntryStrategyObserver;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyFirstState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.PatternStateStatus;

public class ENT_0001_S1 extends ENT_0001_MAIN implements IEntryStrategyFirstState {
	public static final int ENT_0001_S2_newData_order_lastPatternState = 0;
	
	protected final Integer stateNumber = Integer.valueOf(1);
	private EntryStrategyStateStatus status;
	private APatternState patternLastState;
	
	public ENT_0001_S1(Object[] parameters,AEntryStrategyObserver entryStrategyObserver) {
		super(parameters,entryStrategyObserver);
		status = EntryStrategyStateStatus.WAIT_TO_START;
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == EntryStrategyStateStatus.WAIT_TO_START)
		{
			patternLastState = (APatternState)newData[ENT_0001_S2_newData_order_lastPatternState]; 
			if((patternLastState.getStatus() == PatternStateStatus.TRIGGER_BEARISH) || 
					(patternLastState.getStatus() == PatternStateStatus.TRIGGER_BULLISH) ||
					(patternLastState.getStatus() == PatternStateStatus.TRIGGER_NOT_SPECIFIED))
			{
				status = EntryStrategyStateStatus.RUN_TO_NEXT_STATE;
				if (((IPatternLastState)patternLastState).getPatternHigh() == 
						((IPatternLastState)patternLastState).getPatternLow()) { 
					throw new RuntimeException	("pattern size is 0. this is not a real pattern."); 
				}
				entryStrategyObserver.entryRunToNextState();
			} else {
				throw new RuntimeException	("pattern not trigger!!!"); 
			}
		}
		else
		{
			throw new RuntimeException	("Error Occoured in ENT_0001_S1."); 
		}
	}

	@Override
	public IEntryStrategyState getCopyPatternState() {
		return new ENT_0001_S1(parameters,entryStrategyObserver);
	}

	@Override
	public EntryStrategyStateStatus getStatus() {
		return status;
	}

	@Override
	public IEntryStrategyState getNextState() {
		return new ENT_0001_S2(parameters, patternLastState,entryStrategyObserver);
	}

	@Override
	public Date getStartTime() {
		return ((IPatternLastState)patternLastState).getStartPatternTime();
	}

	@Override
	public Date getTriggerTime() {
		return patternLastState.getTriggerTime();
	}

	@Override
	public Integer getStateNumber() {
		return stateNumber;
	}

	@Override // TODO - check way i cant delete this function
	public Integer getNumberOfStates() {
		return 2;
	}

}
