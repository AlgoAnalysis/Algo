package com.algotrado.exit.strategy;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.TradeManager;

public abstract class IExitStrategy {

	protected IEntryStrategyLastState entryLastState;
	protected double currStopLoss;
	protected ExitStrategyStatus exitStrategyStatus;
	protected boolean isLongDirection;
	protected boolean isShortDirection;
	protected double bottomSpread;
	protected double topSpread;
	protected double currBrokerSpread;
	protected double entryStopLoss;
	protected double entryStrategyEntryPoint;
	protected double exitStrategyEntryPoint;
	public abstract void setNewData(NewUpdateData[] newData);
	
	public abstract void forceTrigger();
	
	public ExitStrategyStatus getStatus() {
		return this.exitStrategyStatus;
	}

	public void setStatus(ExitStrategyStatus exitStrategyStatus) {
		this.exitStrategyStatus = exitStrategyStatus;
	}

	public PositionDirectionType getExitDirection() {
		return isLongDirection ? PositionDirectionType.LONG : PositionDirectionType.SHORT;
	}

	public void setNewStopLoss(double stopLoss) {
		this.currStopLoss = stopLoss;
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

	public double getNewEntryPoint() {
		return exitStrategyEntryPoint;
	}

	public double getCurrStopLoss() {
		return currStopLoss;
	}

	public void triggerExit(TradeManager trade) {
		trade.executeExit(this, TradeManager.EXIT_0003);
	}

	public String getDataHeaders() {
		return "\n Top part margin = " + topSpread + ", Bottom part margin = " + bottomSpread + "\n";
	}

	public void triggerExitAndMoveSL(TradeManager trade) {
		trade.executeExitAndMoveSL(this, getStrategyIndex());
	}

	public void moveSL(TradeManager trade) {
		throw new RuntimeException("Method not implemented.");
	}
	
	public abstract int getStrategyIndex(); 
}
