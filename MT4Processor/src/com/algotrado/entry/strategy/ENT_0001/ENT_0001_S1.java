package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyFirstState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.PatternDataObject;
import com.algotrado.pattern.PatternManagerStatus;

public class ENT_0001_S1 extends ENT_0001_MAIN implements IEntryStrategyFirstState {

	protected final Integer stateNumber = Integer.valueOf(1);
	private EntryStrategyStateStatus status;
	private PatternManagerStatus patternManagerStatus = null;
	private double prevHigh = 0;
	private double prevLow = 0;
	private double maxPatternHigh = 0;
	private double minPatternLow = 0;
	private PatternDataObject patternDataObject;
	private int patternCandlesCounter = 1;
	
	public ENT_0001_S1(Object[] parameters) {
		super(parameters);
		status = EntryStrategyStateStatus.WAIT_TO_START;
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if(status == EntryStrategyStateStatus.WAIT_TO_START || status == EntryStrategyStateStatus.RUN)
		{
//			if (newData == null || newData.length <= 2) {
//				throw new RuntimeException("newData Should get patternDataObjects to check ENT_0001_S1. Got only : " + newData);
//			}
			
			status = EntryStrategyStateStatus.RUN;
			if (prevHigh > 0) {
				for (int newDataPatternCnt = 2; newDataPatternCnt < newData.length; /*newDataPatternCnt++*/) {
					if (newData[newDataPatternCnt] instanceof PatternDataObject) {
						patternDataObject = (PatternDataObject)newData[newDataPatternCnt];

						patternManagerStatus = null;
						if((patternDataObject.getPatternManagerStatus() == PatternManagerStatus.TRIGGER_BEARISH) || 
								(patternDataObject.getPatternManagerStatus() == PatternManagerStatus.TRIGGER_BULLISH) ||
								(patternDataObject.getPatternManagerStatus() == PatternManagerStatus.TRIGGER_NOT_SPECIFIED))
						{
							patternManagerStatus = patternDataObject.getPatternManagerStatus();
							status = EntryStrategyStateStatus.RUN_TO_NEXT_STATE;
							maxPatternHigh = (((JapaneseCandleBar)newData[0]).getHigh() > prevHigh) ? ((JapaneseCandleBar)newData[0]).getHigh() : prevHigh;
							minPatternLow = (((JapaneseCandleBar)newData[0]).getLow() < prevLow) ? ((JapaneseCandleBar)newData[0]).getLow() : prevLow;
						} else if (patternDataObject.getPatternManagerStatus() == PatternManagerStatus.ERROR) {
							status = EntryStrategyStateStatus.ERROR;
							throw new RuntimeException	("Error Occoured in Pattern Manager."); // TODO 
						} else {
							status = EntryStrategyStateStatus.RUN;
						}

						break;
					} else {
						throw new RuntimeException("newData Should get patternDataObjects to check ENT_0001_S1. Got only : " + newData[newDataPatternCnt]);
					}
				}
			}
			prevHigh = ((JapaneseCandleBar)newData[0]).getHigh();
			prevLow = ((JapaneseCandleBar)newData[0]).getLow();
			patternCandlesCounter++;
			if (patternCandlesCounter > 2 && status != EntryStrategyStateStatus.RUN_TO_NEXT_STATE) {
				status = EntryStrategyStateStatus.KILL_STATE;
			}
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
		return patternDataObject.getPatternDates().get(0);
	}

	@Override
	public Date getTriggerTime() {
		return patternDataObject.getPatternDates().get(patternDataObject.getPatternDates().size() - 1);
	}

	@Override
	public Integer getStateNumber() {
		return stateNumber;
	}

	@Override
	public Integer getNumberOfStates() {
		return 2;
	}

}
