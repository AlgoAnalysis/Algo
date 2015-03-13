package com.algotrado.entry.strategy;

import java.util.Date;


public interface IEntryStrategyFirstState {
	public Date getStartTime();
	public IEntryStrategyState getCopyPatternState();
}
