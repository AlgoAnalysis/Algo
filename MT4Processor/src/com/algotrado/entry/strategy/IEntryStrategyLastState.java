package com.algotrado.entry.strategy;

import com.algotrado.trade.PositionDirectionType;

public interface IEntryStrategyLastState {

	public double getBuyOrderPrice();
	
	public double getStopLossPrice();
	
	public double getTakeProfitPrice();
	
	public PositionDirectionType getPositionDirectionType();
	
}
