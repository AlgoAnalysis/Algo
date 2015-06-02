package com.algotrado.exit.strategy.EXT_0003;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.TradeManager;

public class EXT_0003 extends IExitStrategy {
	
	private static final int RSI_INDEX = 2;
	private double longRsiValueForExit;
	private double shortRsiValueForExit;
	
	public EXT_0003(double bottomSpread, double topSpread) {
		super();
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
	}
	
	public EXT_0003(IEntryStrategyLastState entryLastState, double bottomSpread, 
			double topSpread, double currBrokerSpread, double currPrice, double currRsiValue,
			double longRsiValueForExit, double shortRsiValueForExit) {
		super();
		this.entryLastState = entryLastState;
		this.entryStopLoss = entryLastState.getStopLossPrice();
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
		this.entryStrategyEntryPoint = entryLastState.getBuyOrderPrice();
		this.currBrokerSpread = currBrokerSpread;
		this.longRsiValueForExit = longRsiValueForExit;
		this.shortRsiValueForExit = shortRsiValueForExit;
		
		if (((IEntryStrategyState)this.entryLastState).getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH) {
			this.isShortDirection = true;
		} else if (((IEntryStrategyState)this.entryLastState).getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH) {
			this.isLongDirection = true;
		} else if (((IEntryStrategyState)this.entryLastState).getStatus() == EntryStrategyStateStatus.ERROR) {
			this.exitStrategyStatus = ExitStrategyStatus.ERROR;
		}
		
		if (isLongDirection) {
			exitStrategyEntryPoint = entryStrategyEntryPoint + this.topSpread + this.currBrokerSpread;
			currStopLoss = entryStopLoss - this.bottomSpread;
		} else if (isShortDirection) {
			exitStrategyEntryPoint = entryStrategyEntryPoint - this.bottomSpread;
			currStopLoss = entryStopLoss + this.topSpread + this.currBrokerSpread;
		}
		
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR) {
			if (isLongDirection) {
				if (currRsiValue >= longRsiValueForExit) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			} else if (isShortDirection) {
				if (currRsiValue <= shortRsiValueForExit) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			}
		}
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR && newData.length >= RSI_INDEX + 1) {
			SimpleUpdateData simpleUpdateData = (SimpleUpdateData)newData[2];
			if (isLongDirection) {
				if (simpleUpdateData.getValue() >= longRsiValueForExit) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			} else if (isShortDirection) {
				if (simpleUpdateData.getValue() <= shortRsiValueForExit) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			}
		}
	}

	@Override
	public void forceTrigger() {
		this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
	}
	
	@Override
	public int getStrategyIndex() {
		return TradeManager.EXIT_0003;
	}
}
