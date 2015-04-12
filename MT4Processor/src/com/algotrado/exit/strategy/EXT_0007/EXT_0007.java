package com.algotrado.exit.strategy.EXT_0007;

import java.util.List;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyManager;
import com.algotrado.entry.strategy.EntryStrategyManagerStatus;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;

public class EXT_0007 implements IExitStrategy{
	private IEntryStrategyLastState entryLastState;
	private double currStopLoss;
	private ExitStrategyStatus exitStrategyStatus;
	private boolean isLongDirection;
	private boolean isShortDirection;
	private double bottomSpread;
	private double topSpread;
	private double currBrokerSpread;
	private double entryStopLoss;
	private double entryStrategyEntryPoint;
	private double exitStrategyEntryPoint;
	private double xFactor;
	
	public EXT_0007(IEntryStrategyLastState entryLastState, double xFactor, double bottomSpread, double topSpread, double currBrokerSpread, double currPrice) {
		super();
		this.entryLastState = entryLastState;
		this.currStopLoss = entryLastState.getStopLossPrice();
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
//		this.isLongDirection = false;
//		this.isShortDirection = false;
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
				if (currPrice > ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			} else if (isShortDirection) {
				if (currPrice < ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
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
	public ExitStrategyStatus getStatus() {
		return this.exitStrategyStatus;
	}

	@Override
	public double getNewStopLoss() {
		RuntimeException runtimeException = new RuntimeException("getNewStopLoss() => Should not call this method on exit strategy 0007.");
		runtimeException.printStackTrace();
		throw runtimeException;
	}

	@Override
	public void forceTrigger() {
		this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
	}

	@Override
	public void setShortSpread(double shortSpread) {
		this.bottomSpread = shortSpread;
	}

	@Override
	public void setLongSpread(double longSpread) {
		this.topSpread = longSpread;
	}

	@Override
	public void setCurrBrokerSpread(double currBrokerSpread) {
		this.currBrokerSpread = currBrokerSpread;
	}
	
	@Override
	public void setNewStopLoss(double stopLoss) {
		this.currStopLoss = stopLoss;
	}
	
	@Override
	public String getDataHeaders() {
		return "\n Top part margin = " + topSpread + ", Bottom part margin = " + bottomSpread /*+ ", entry strategy S.L. = " 
			+ entryStopLoss + ", Entry Strategy Entry point = " + entryStrategyEntryPoint + 
			", exit Strategy entry point = " + exitStrategyEntryPoint*/;
	}
	
	@Override
	public double getNewEntryPoint() {
		return exitStrategyEntryPoint;
	}

}
