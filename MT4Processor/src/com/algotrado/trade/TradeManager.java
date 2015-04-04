package com.algotrado.trade;

import com.algotrado.extract.data.IDataExtractorObserver;

public abstract class TradeManager implements IDataExtractorObserver {

	// 1 entry manager
	// list of exits in array
	// 1 moneyManagement
	// money manager => 1 method to ask permission from moneyManagement to open/close position (ExitStrategyDataObject [] array). the Return value will include (quantity, percent to close from position on each exit)
	// The trade will hold statistics on each exit and entry. if returns null MoneyManager will return default values.
	/**
	 *  1 method to force exit on all positions from money management.
	 * @return if the method execution was successful.
	 */
	public abstract boolean forceExitAllPositions();
	
	/**
	 * @return get status of position (for money manager)
	 */
	public abstract PositionStatus getStatus();
	
	// money manager => 1 method to update money manager that trade was closed.
	
	// Reports trade + exit
	// data headers = > all data I get from trade + entry + exit
	// toString => trade entry start, entry end, exit# trigger or eliminate, exit# end time, ... all exits ... , amount purchased, Gain/Loss amount  
	
	
	//File data extractor broker => add support for initial amount of money sum and on each trade we initiate and close update the sum.
	// File should act as a broker.
	// file should take spread from account on each trade when buy order is received.
	// file broker => openPosition(asset, amount, direction, stopLoss) => returns positionId
	// file broker => closePosition(positionId, amountToClose)
	// file broker => getPositionStatus(positionId) => identify cases in which broker has closed order position in SL.
	// file broker => modifyPosition(positionId, newStopLoss)
	
}
