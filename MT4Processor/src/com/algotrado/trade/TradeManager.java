package com.algotrado.trade;

import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.exit.strategy.EXT_0003.EXT_0003;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.SubjectState;

public abstract class TradeManager implements IDataExtractorObserver {
	
	public final static int EXIT_0001 = 0;
	public final static int EXIT_0003 = 0;
	public final static int EXIT_0007 = 0;

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
	
	public String getDataHeaders() {
		return "Requires implementation " + this.getClass() + " line 24";
	}
	
	public abstract SubjectState getSubjectState();
	
	public abstract boolean isClosedTrade();
	
	public abstract boolean executeExit(IExitStrategy exit, int index);
	
	public abstract boolean executeExitAndMoveSL(IExitStrategy exit, int index);
	
//	public abstract boolean executeExitAndMoveSL(EXT_0003 exit);
//	public abstract boolean executeExitAndMoveSL(EXT_0007 exit);
	
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
	
	public int compareTo(IDataExtractorObserver o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		}
		return -1;
	}
	
}
