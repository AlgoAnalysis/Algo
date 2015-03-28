package com.algotrado.exit.strategy;

public class ExitStrategyDataObject {

	private IExitStrategy exit;
	private double fractionToCloseOnTrigger;
	
	public ExitStrategyDataObject(IExitStrategy exit,
			double percentToCloseOnTrigger) {
		super();
		this.exit = exit;
		this.fractionToCloseOnTrigger = percentToCloseOnTrigger;
	}

	public IExitStrategy getExit() {
		return exit;
	}

	public double getFractionToCloseOnTrigger() {
		return fractionToCloseOnTrigger;
	}
}
