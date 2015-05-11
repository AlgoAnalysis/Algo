package com.algotrado.exit.strategy;

import com.algotrado.trade.PositionDirectionType;

public class ExitStrategyDataObject {

	private IExitStrategy exit;
	private double fractionToCloseOnTrigger;
	private Double statistics;
	private Double closingPrice;
	private Double exitStrategyEntryPoint;
	private boolean isLong;
	private double contractAmount;
	
	public ExitStrategyDataObject(IExitStrategy exit,
			double percentToCloseOnTrigger, Double statistics, double contractAmount) {
		super();
		this.exit = exit;
		this.fractionToCloseOnTrigger = percentToCloseOnTrigger;
		this.statistics = statistics;
		this.closingPrice = null;
		this.contractAmount = contractAmount;
		initExitStrategyObj();
	}

	public void initExitStrategyObj() {
		if (this.exit != null) {
			exitStrategyEntryPoint = this.exit.getNewEntryPoint();
			isLong = this.exit.getExitDirection() == PositionDirectionType.LONG;
		}
	}

	public IExitStrategy getExit() {
		return exit;
	}

	public void setExit(IExitStrategy exit) {
		this.exit = exit;
		initExitStrategyObj();
	}

	public double getFractionToCloseOnTrigger() {
		return fractionToCloseOnTrigger;
	}
	
	public Double getStatistics() {
		return statistics;
	}

	public Double getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(Double closingPrice) {
		this.closingPrice = closingPrice;
	}
	
	public double getGain(double quantity) {
		return contractAmount * (quantity * fractionToCloseOnTrigger) * (isLong ? (closingPrice - exitStrategyEntryPoint) : (exitStrategyEntryPoint - closingPrice));
	}

	public boolean isLong() {
		return isLong;
	}

	public double getContractAmount() {
		return contractAmount;
	}
}
