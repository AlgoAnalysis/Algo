package com.algotrado.broker;

import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;

public interface IBroker {

	//File data extractor broker => add support for initial amount of money sum and on each trade we initiate and close update the sum.
	// File should act as a broker.
	// file should take spread from account on each trade when buy order is received.
	/**
	 * @param assetType
	 * @param dataEventType
	 * @param parameters
	 * @return
	 */
	public IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Double> parameters);
	
	/**
	 * @param assetTypes
	 * @param timeFrame
	 * @return
	 */
	public List<JapaneseCandleBar> getHistory(AssetType assetTypes,JapaneseTimeFrameType timeFrame);

	/**
	 *  file broker => openPosition(asset, amount, direction, stopLoss) => opens market price position returns positionId
	 * @param asset
	 * @param amount
	 * @param direction
	 * @param stopLoss
	 * @return
	 */
	public int openPosition(AssetType asset, double amount, PositionDirectionType direction, double stopLoss,double takeProfit);
	/**
	 *  file broker => closePosition(positionId, amountToClose)
	 * @return if succeeded
	 */
	public boolean closePosition(int positionId, double amountToClose);
	/**
	 *  file broker => closePosition(positionId) => closes all the position
	 * @return if succeeded
	 */
	public boolean closePosition(int positionId);
	/**
	 *  file broker => getPositionStatus(positionId) => identify cases in which broker has closed order position in SL.
	 */
	public PositionStatus getPositionStatus(int positionId);
	/**
	 *  file broker => modifyPosition(positionId, newStopLoss)
	 *  @return if succeeded
	 */
	public boolean modifyPosition(int positionId, double newStopLoss,double newTakeProfit);
	
	/**
	 * @param asset
	 * @return
	 */
	public double getLiveSpread(AssetType asset);
	
	public double getCurrentAskPrice(AssetType asset);
	
	public double getContractAmount(AssetType asset);
	public double getMinimumContractAmountMultiply(AssetType asset);
	public Account getAccountStatus();
}
