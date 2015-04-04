package com.algotrado.exit.strategy.EXT_0001;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyManager;
import com.algotrado.entry.strategy.EntryStrategyManagerStatus;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;

public class EXT_0001 implements IExitStrategy {
	private EntryStrategyManager entryStrategyManager;
	private double currStopLoss;
//	private List<Double> parameters;
	private ExitStrategyStatus exitStrategyStatus;
	private boolean isLongDirection;
	private boolean isShortDirection;
	private double bottomSpread;
	private double topSpread;
	private double currBrokerSpread;
	private double entryStopLoss;
	private double entryStrategyEntryPoint;
	private double exitStrategyEntryPoint;
	
	

	public EXT_0001(EntryStrategyManager entryStrategyManager,
			double firstStopLoss/*, List<Double> parameters*/) {
		super();
		this.entryStrategyManager = entryStrategyManager;
		this.currStopLoss = firstStopLoss;
//		this.parameters = parameters;
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
		this.isLongDirection = false;
		this.isShortDirection = false;
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (!isLongDirection && !isShortDirection) { // first Run
			if (this.entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BEARISH) {
				this.isShortDirection = true;
			} else if (this.entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BULLISH) {
				this.isLongDirection = true;
			} else if (this.entryStrategyManager.getStatus() == EntryStrategyManagerStatus.ERROR) {
				this.exitStrategyStatus = ExitStrategyStatus.ERROR;
			}
			
			if (isLongDirection) {
				exitStrategyEntryPoint = entryStrategyEntryPoint + topSpread + currBrokerSpread;
				currStopLoss = entryStopLoss - bottomSpread;
			} else if (isShortDirection) {
				exitStrategyEntryPoint = entryStrategyEntryPoint - bottomSpread;
				currStopLoss = entryStopLoss + topSpread + currBrokerSpread;
			}
		}
		
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR) {
			JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar)newData[0];
			if (isLongDirection) {
				if (japaneseCandleBar.getHigh() > ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			} else if (isShortDirection) {
				if (japaneseCandleBar.getLow() < ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
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
		return this.currStopLoss;
	}

	/**
	 * If exit strategy 0007 is ran than we should force the move S.l of this Strategy.
	 */
	@Override
	public void forceTrigger() {
		if (isLongDirection) {
			this.currStopLoss = this.exitStrategyEntryPoint - (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) / 10); 
		} else if (isShortDirection) {
			this.currStopLoss = this.exitStrategyEntryPoint + (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) / 10); 
		} else {
			this.exitStrategyStatus = ExitStrategyStatus.ERROR;
			return;
		}
		this.exitStrategyStatus = ExitStrategyStatus.MOVE_STOP_LOSS;
	}

	public void setShortSpread(double shortSpread) {
		this.bottomSpread = shortSpread;
	}

	public void setLongSpread(double longSpread) {
		this.topSpread = longSpread;
	}

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

}
