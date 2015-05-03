package com.algotrado.entry.strategy;

public enum EntryStrategyStateStatus {
	WAIT_TO_START,
	RUN,
	RUN_TO_NEXT_STATE,
	TRIGGER_BULLISH,
	TRIGGER_BEARISH,
	KILL_STATE,
	ERROR;
}
