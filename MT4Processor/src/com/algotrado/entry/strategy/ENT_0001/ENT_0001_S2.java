package com.algotrado.entry.strategy.ENT_0001;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.pattern.PatternManagerStatus;


public class ENT_0001_S2 extends ENT_0001_MAIN implements IEntryStrategyLastState {
	private static final int MAX_RSI_LONG_VALUE = 80;
	private static final int MIN_RSI_SHORT_VALUE = 20;
	private static final int MAX_NUM_OF_CANDLES_AFTER_PATTERN = 5;
	protected final Integer stateNumber = Integer.valueOf(2);
	private PatternManagerStatus patternDirection = null;
	private double patternHighLimit;
	private double patternLowLimit;
	private double triggerCandlePrice;
	private EntryStrategyStateStatus status;
	private int countCandlesIndex;
	private Date triggerDate;
	private EntryStrategyTriggerType entryStrategyTriggerType;
	private SimpleUpdateData prevRSI;

	public ENT_0001_S2(Object[] parameters, PatternManagerStatus patternDirection, double patternHighLimit, double patternLowLimit, SimpleUpdateData prevRSI) {
		super(parameters);
		this.patternDirection = patternDirection;
		this.patternHighLimit = patternHighLimit;
		this.patternLowLimit = patternLowLimit;
		this.status = EntryStrategyStateStatus.RUN;
		this.countCandlesIndex = 1;
		this.entryStrategyTriggerType = EntryStrategyTriggerType.getEntryStrategyTriggerType(((Double)parameters[1]).intValue());
		this.prevRSI = prevRSI;
	}

	/**
	 * Assume newData is always with [0] japanese candle bar and [1] RSI value.
	 */
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.status == EntryStrategyStateStatus.RUN) {
			if (countCandlesIndex > MAX_NUM_OF_CANDLES_AFTER_PATTERN) {// We have already checked 5 candles. kill state.
				this.status = EntryStrategyStateStatus.KILL_STATE;
			} else {
				JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar)newData[0];
				double candleBarBreakOutPriceLong = this.entryStrategyTriggerType.getTriggerPrice(japaneseCandleBar, true);
				double candleBarBreakOutPriceShort = this.entryStrategyTriggerType.getTriggerPrice(japaneseCandleBar, false);
				SimpleUpdateData simpleUpdateData = (this.entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_BREAK_PRICE) ? prevRSI : ((SimpleUpdateData)newData[1]);
				if ((candleBarBreakOutPriceLong > patternHighLimit) && 
						(patternDirection == PatternManagerStatus.TRIGGER_BULLISH || patternDirection == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) && 
						(simpleUpdateData.getValue() < MAX_RSI_LONG_VALUE)) {
					triggerDate = japaneseCandleBar.getTime();
					status = EntryStrategyStateStatus.TRIGGER_BULLISH;
					triggerCandlePrice = candleBarBreakOutPriceLong;
				} else if ((candleBarBreakOutPriceShort < patternLowLimit) &&
						(patternDirection == PatternManagerStatus.TRIGGER_BEARISH || patternDirection == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) &&
						(simpleUpdateData.getValue() > MIN_RSI_SHORT_VALUE)) {
					triggerDate = japaneseCandleBar.getTime();
					status = EntryStrategyStateStatus.TRIGGER_BEARISH;
					triggerCandlePrice = candleBarBreakOutPriceShort;
				}
				countCandlesIndex++;
				prevRSI = ((SimpleUpdateData)newData[1]);
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

	@Override
	public Integer getNumberOfStates() {
		return 2;
	}

	@Override
	public double getBuyOrderPrice() {
		double patternPrice = (this.status == EntryStrategyStateStatus.TRIGGER_BULLISH) ? patternHighLimit : patternLowLimit;
		return (this.entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_CLOSE_PRICE) ? triggerCandlePrice : patternPrice;
	}

	@Override
	public double getStopLossPrice() {
		return (this.status == EntryStrategyStateStatus.TRIGGER_BEARISH) ? patternHighLimit : patternLowLimit;
	}

}
