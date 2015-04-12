package com.algotrado.exit.strategy;

import com.algotrado.data.event.NewUpdateData;

public interface IExitStrategy {

	public void setNewData(NewUpdateData[] newData);
	
	public ExitStrategyStatus getStatus();
	
	public double getNewStopLoss();
	
	public double getNewEntryPoint();
	
	public void setNewStopLoss(double stopLoss);
	
	public void forceTrigger();
	
	public void setShortSpread(double shortSpread);

	public void setLongSpread(double longSpread);

	public void setCurrBrokerSpread(double currBrokerSpread);
	
	public String getDataHeaders();
}
