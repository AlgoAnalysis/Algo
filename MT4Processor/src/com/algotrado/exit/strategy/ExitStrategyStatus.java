package com.algotrado.exit.strategy;

public enum ExitStrategyStatus {
	RUN(1),
	TRIGGER(2),
	MOVE_STOP_LOSS(4),
	TRIGGER_AND_MOVE_STOP_LOSS(6),
	ERROR(0);
	
	private int value;
	
	private ExitStrategyStatus (int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
