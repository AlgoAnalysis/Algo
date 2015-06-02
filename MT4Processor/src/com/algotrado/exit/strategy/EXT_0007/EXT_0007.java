package com.algotrado.exit.strategy.EXT_0007;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.TradeManager;

public class EXT_0007 extends IExitStrategy{
	private double xFactor;
	
	public EXT_0007(double bottomSpread, double topSpread) {
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
	}
	
	public EXT_0007(IEntryStrategyLastState entryLastState, double xFactor, double bottomSpread, double topSpread, double currBrokerSpread, double currPrice) {
		super();
		this.entryLastState = entryLastState;
		this.entryStopLoss = entryLastState.getStopLossPrice();
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
		this.xFactor = xFactor;
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
		this.entryStrategyEntryPoint = entryLastState.getBuyOrderPrice();
		this.currBrokerSpread = currBrokerSpread;
		
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
				if (currPrice > ( ((1 + xFactor) * exitStrategyEntryPoint) - (xFactor * currStopLoss) )) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			} else if (isShortDirection) {
				if (currPrice < ( ((1 + xFactor) * exitStrategyEntryPoint) - (xFactor * currStopLoss) )) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			}
		}
	}
	
	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR) {
			JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar)newData[0];
			if (isLongDirection) {
				if (japaneseCandleBar.getHigh() > ( ((1 + xFactor) * exitStrategyEntryPoint) - (xFactor * currStopLoss) )) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
				}
			} else if (isShortDirection) {
				if (japaneseCandleBar.getLow() < ( ((1 + xFactor) * exitStrategyEntryPoint) - (xFactor * currStopLoss) ) ) {
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
		return TradeManager.EXIT_0007;
	}
}
