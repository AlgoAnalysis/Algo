package com.algotrado.pattern;

import java.util.Date;

public interface IPatternFirstState{
	
	public Date getStartTime();
	public APatternState getCopyPatternState();
	public void setPatternManager(PatternManager patternManager);
}
