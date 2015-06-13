package com.algotrado.money.manager;

import java.util.Date;
import java.util.List;

import com.algotrado.entry.strategy.EntryStrategyDataObject;
import com.algotrado.entry.strategy.EntryStrategyManager.EntryStrategyStateAndTime;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.trade.PositionStatus;

public interface IMoneyManager {

	/**
	 * updates postion status including if trade is still open or has closed.
	 * And what is the gain/loss of the trade.
	 * @param positionStatus
	 */
	public void updatePositionStatus(PositionStatus positionStatus);
	
//	public Double requestPermissionToOpenTrade(ExitStrategyDataObject [] exitStrategiesList, EntryStrategyDataObject entryStrategyDataObj);
	
	public void updateOnEntry(List<EntryStrategyStateAndTime> stateArr, Date entryDateAndTime);
}
