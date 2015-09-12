package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.entry.strategy.AEntryStrategyObserver;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.PatternStateStatus;
import com.algotrado.trade.PositionDirectionType;


public class ENT_0001_S2 extends ENT_0001_MAIN implements IEntryStrategyLastState {
	public static final int ENT_0001_S2_newData_order_Quote_or_JapaneseCandle = 0;
	public static final int ENT_0001_S2_newData_order_Rsi = 1;
	
	protected final Integer stateNumber = Integer.valueOf(2);
	private PatternStateStatus patternDirection = null;
	private double patternHighLimit;
	private double patternLowLimit;
	private double triggerCandlePrice;
	private EntryStrategyStateStatus status;
	private int countCandlesIndex;
	private Date triggerDate;
	private EntryStrategyTriggerType entryStrategyTriggerType;
	private long rsiLastTime; 
	protected double maxRsiLongValue;
	protected double minRsiShortValue;
	protected int maxNumOfCandlesAfterPattern;

	public ENT_0001_S2(Object[] parameters,APatternState patternLastState, AEntryStrategyObserver entryStrategyObserver) {
		super(parameters,entryStrategyObserver);
		this.patternDirection = patternLastState.getStatus();
		this.patternHighLimit = ((IPatternLastState)patternLastState).getPatternHigh();
		this.patternLowLimit = ((IPatternLastState)patternLastState).getPatternLow();
		this.status = EntryStrategyStateStatus.RUN;
		this.countCandlesIndex = 1;
		this.entryStrategyTriggerType = EntryStrategyTriggerType.getEntryStrategyTriggerType(((Double)parameters[ENT_0001_parammetrs_order_StrategyTriggerType]).intValue());
		this.rsiLastTime = patternLastState.getTriggerTime().getTime();
		this.maxRsiLongValue = ((Double)parameters[ENT_0001_parammetrs_order_maxRsiLongValue]);
		this.minRsiShortValue = ((Double)parameters[ENT_0001_parammetrs_order_minRsiShortValue]);
		this.maxNumOfCandlesAfterPattern = ((Double)parameters[ENT_0001_parammetrs_order_maxNumOfCandlesAfterPattern]).intValue();
	}

	/**
	 * Assume newData is always with [0] japanese candle bar and [1] RSI value.
	 */
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.status == EntryStrategyStateStatus.RUN) {
			if (countCandlesIndex > maxNumOfCandlesAfterPattern) {// We have already checked 5 candles. kill state.
				this.status = EntryStrategyStateStatus.KILL_STATE;
				entryStrategyObserver.entryKillState();
			} else {
				double currentPrice = this.entryStrategyTriggerType.getTriggerPrice(newData[ENT_0001_S2_newData_order_Quote_or_JapaneseCandle]);
				double currentRsiValue = ((SimpleUpdateData)newData[ENT_0001_S2_newData_order_Rsi]).getValue();
				PositionDirectionType directionType = null;
				if ((currentPrice > patternHighLimit) && 
						(patternDirection == PatternStateStatus.TRIGGER_BULLISH || patternDirection == PatternStateStatus.TRIGGER_NOT_SPECIFIED) && 
						(currentRsiValue < maxRsiLongValue)) {
					status = EntryStrategyStateStatus.TRIGGER_BULLISH;
					directionType = PositionDirectionType.LONG;
				} else if ((currentPrice < patternLowLimit) &&
						(patternDirection == PatternStateStatus.TRIGGER_BEARISH || patternDirection == PatternStateStatus.TRIGGER_NOT_SPECIFIED) &&
						(currentRsiValue > minRsiShortValue)) {
					status = EntryStrategyStateStatus.TRIGGER_BEARISH;
					directionType = PositionDirectionType.SHORT;
				}
				if(status == EntryStrategyStateStatus.TRIGGER_BEARISH || status == EntryStrategyStateStatus.TRIGGER_BULLISH)
				{
					triggerDate = newData[ENT_0001_S2_newData_order_Quote_or_JapaneseCandle].getTime();
					triggerCandlePrice = currentPrice;
					entryStrategyObserver.entryTrigerr(this, directionType, patternHighLimit);
				}
				else if(rsiLastTime != newData[ENT_0001_S2_newData_order_Rsi].getTime().getTime())
				{
					countCandlesIndex++;
					rsiLastTime = newData[ENT_0001_S2_newData_order_Rsi].getTime().getTime();
				}
			}
		} else if (this.status == EntryStrategyStateStatus.TRIGGER_BEARISH || 
				this.status == EntryStrategyStateStatus.TRIGGER_BULLISH) {
			this.status = EntryStrategyStateStatus.KILL_STATE;
			entryStrategyObserver.entryKillState();
		} else {
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

	@Override
	public Integer getNumberOfStates() {
		return 2;
	}

	@Override
	public double getBuyOrderPrice() { // TODO - need to check way this function implement like this.
		double patternPrice = (this.status == EntryStrategyStateStatus.TRIGGER_BULLISH) ? patternHighLimit : patternLowLimit;
		return (this.entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_CLOSE_PRICE) ? triggerCandlePrice : patternPrice;
	}

	@Override
	public double getStopLossPrice() {
		return (this.status == EntryStrategyStateStatus.TRIGGER_BEARISH) ? patternHighLimit : patternLowLimit;
	}

	@Override
	public double getTakeProfitPrice() {
		return 0;
	}

	@Override
	public PositionDirectionType getPositionDirectionType() {
		return (this.status == EntryStrategyStateStatus.TRIGGER_BEARISH) ? PositionDirectionType.SHORT : PositionDirectionType.LONG;
	}

}
