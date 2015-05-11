package com.algotrado.exit.strategy.EXT_0001;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.PositionDirectionType;

public class EXT_0001 implements IExitStrategy {
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
	private double fractionOfOriginalStopLoss;
	
	public EXT_0001(double bottomSpread, double topSpread) {
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
	}

	public EXT_0001(IEntryStrategyLastState entryLastState, double fractionOfOriginalStopLoss, 
			double bottomSpread, double topSpread, double currBrokerSpread, double currPrice) {
		super();
		this.entryLastState = entryLastState;
		this.entryStopLoss = entryLastState.getStopLossPrice();
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
//		this.isLongDirection = false;
//		this.isShortDirection = false;
		this.fractionOfOriginalStopLoss = fractionOfOriginalStopLoss;
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
			exitStrategyEntryPoint = entryStrategyEntryPoint + topSpread + currBrokerSpread;
			currStopLoss = entryStopLoss - bottomSpread;
		} else if (isShortDirection) {
			exitStrategyEntryPoint = entryStrategyEntryPoint - bottomSpread;
			currStopLoss = entryStopLoss + topSpread + currBrokerSpread;
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
			this.currStopLoss = this.exitStrategyEntryPoint - (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) * fractionOfOriginalStopLoss); 
		} else if (isShortDirection) {
			this.currStopLoss = this.exitStrategyEntryPoint + (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) * fractionOfOriginalStopLoss); 
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
	
	public double getCurrStopLoss() {
		return currStopLoss;
	}

	@Override
	public String getDataHeaders() {
		return "\n Top part margin = " + topSpread + ", Bottom part margin = " + bottomSpread + "\n"/*+ ", entry strategy S.L. = " 
			+ entryStopLoss + ", Entry Strategy Entry point = " + entryStrategyEntryPoint + 
			", exit Strategy entry point = " + exitStrategyEntryPoint*/;
	}

	@Override
	public double getNewEntryPoint() {
		return exitStrategyEntryPoint;
	}
	
	@Override
	public PositionDirectionType getExitDirection() {
		return isLongDirection ? PositionDirectionType.LONG : PositionDirectionType.SHORT;
	}

}
