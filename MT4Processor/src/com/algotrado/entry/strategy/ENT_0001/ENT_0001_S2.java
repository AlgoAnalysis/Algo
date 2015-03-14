package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.PatternManagerStatus;


public class ENT_0001_S2 extends ENT_0001_MAIN implements IEntryStrategyLastState {
	private static final int MAX_NUM_OF_CANDLES_AFTER_PATTERN = 5;
	protected final Integer stateNumber = Integer.valueOf(2);
	private PatternManagerStatus patternDirection = null;
	private double patternHighLimit;
	private double patternLowLimit;
	private EntryStrategyStateStatus status;
	private int countCandlesIndex;
	private Date triggerDate;

	public ENT_0001_S2(Object[] parameters, PatternManagerStatus patternDirection, double patternHighLimit, double patternLowLimit) {
		super(parameters);
		this.patternDirection = patternDirection;
		this.patternHighLimit = patternHighLimit;
		this.patternLowLimit = patternLowLimit;
		this.status = EntryStrategyStateStatus.RUN;
		this.countCandlesIndex = 1;
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.status == EntryStrategyStateStatus.RUN) {
			if (countCandlesIndex > MAX_NUM_OF_CANDLES_AFTER_PATTERN) {// We have already checked 5 candles. kill state.
				this.status = EntryStrategyStateStatus.KILL_STATE;
			} else {
				double candleBarClosePrice = ((JapaneseCandleBar)newData[0]).getClose();
				if (candleBarClosePrice > patternHighLimit) {
					if (patternDirection == PatternManagerStatus.TRIGGER_BULLISH || 
							patternDirection == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) {
						triggerDate = ((JapaneseCandleBar)newData[0]).getTime();
						status = EntryStrategyStateStatus.TRIGGER_BULLISH;
					} else {
						this.status = EntryStrategyStateStatus.KILL_STATE;
					}
				} else if (candleBarClosePrice < patternLowLimit) {
					if (patternDirection == PatternManagerStatus.TRIGGER_BEARISH || 
							patternDirection == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) {
						triggerDate = ((JapaneseCandleBar)newData[0]).getTime();
						status = EntryStrategyStateStatus.TRIGGER_BEARISH;
					} else {
						this.status = EntryStrategyStateStatus.KILL_STATE;
					}
				}
				countCandlesIndex++;
			}
		} else if (this.status == EntryStrategyStateStatus.TRIGGER_BEARISH || 
				this.status == EntryStrategyStateStatus.TRIGGER_BULLISH) {
			this.status = EntryStrategyStateStatus.KILL_STATE;
		} else {
			this.status = EntryStrategyStateStatus.ERROR;
			throw new RuntimeException("An error occoured trying to run ENT_0001_S2");
		}
	}

	@Override
	public EntryStrategyStateStatus getStatus() {
		return status;
	}

	@Override
	public IEntryStrategyState getNextState() {
		return null;
	}

	@Override
	public Date getTriggerTime() {
		return triggerDate;
	}
	
	@Override
	public Integer getStateNumber() {
		return stateNumber;
	}

}
