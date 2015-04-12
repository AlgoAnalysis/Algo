package com.algotrado.broker;

import com.algotrado.extract.data.AssetType;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;

public interface IBroker {

	//File data extractor broker => add support for initial amount of money sum and on each trade we initiate and close update the sum.
	// File should act as a broker.
	// file should take spread from account on each trade when buy order is received.
	/**
	 *  file broker => openPosition(asset, amount, direction, stopLoss) => returns positionId
	 * @param asset
	 * @param amount
	 * @param direction
	 * @param stopLoss
	 * @return
	 */
	public int openPosition(AssetType asset, double amount, PositionDirectionType direction, double stopLoss);
	/**
	 *  file broker => closePosition(positionId, amountToClose)
	 * @return if succeeded
	 */
	public boolean closePosition(int positionId, double amountToClose);
	/**
	 *  file broker => getPositionStatus(positionId) => identify cases in which broker has closed order position in SL.
	 */
	public PositionStatus getPositionStatus(int positionId);
	/**
	 *  file broker => modifyPosition(positionId, newStopLoss)
	 *  @return if succeeded
	 */
	public boolean modifyPosition(int positionId, double newStopLoss);
	
	public double getLiveSpread(AssetType asset);
	
	public double getCurrentAskPrice(AssetType asset);
}
