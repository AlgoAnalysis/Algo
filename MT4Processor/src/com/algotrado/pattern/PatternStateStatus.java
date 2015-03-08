package com.algotrado.pattern;

public enum PatternStateStatus {
	WAIT_TO_START,
	RUN,
	RUN_TO_NEXT_STATE,
	TRIGGER_BULLISH,
	TRIGGER_BEARISH,
	TRIGGER_NOT_SPECIFIED,
	KILL_STATE,
	ERROR,
}
