package com.algotrado.pattern;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;

public interface IPatternLastState extends NewUpdateData{

	public double getPatternHigh();
	public double getPatternLow();
	public Date getStartPatternTime();
}
