package com.algotrado.exit.strategy;

public class ExitStrategyDataObject {

	private IExitStrategy exit;
	private double fractionToCloseOnTrigger;
	private Double statistics;
	private Double closingPrice;
	
	public ExitStrategyDataObject(IExitStrategy exit,
			double percentToCloseOnTrigger, Double statistics) {
		super();
		this.exit = exit;
		this.fractionToCloseOnTrigger = percentToCloseOnTrigger;
		this.statistics = statistics;
		this.closingPrice = null;
	}

	public IExitStrategy getExit() {
		return exit;
	}

	public void setExit(IExitStrategy exit) {
		this.exit = exit;
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
}
