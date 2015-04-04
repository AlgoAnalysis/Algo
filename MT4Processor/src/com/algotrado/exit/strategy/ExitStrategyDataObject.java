package com.algotrado.exit.strategy;

public class ExitStrategyDataObject {

	private IExitStrategy exit;
	private double fractionToCloseOnTrigger;
	private Double statistics;
	
	public ExitStrategyDataObject(IExitStrategy exit,
			double percentToCloseOnTrigger, Double statistics) {
		super();
		this.exit = exit;
		this.fractionToCloseOnTrigger = percentToCloseOnTrigger;
		this.statistics = statistics;
	}

	public IExitStrategy getExit() {
		return exit;
	}

	public double getFractionToCloseOnTrigger() {
		return fractionToCloseOnTrigger;
	}
	
	public Double getStatistics() {
		return statistics;
	}
}
