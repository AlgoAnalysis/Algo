package com.algotrado.trade.TRD_0001;

import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyDataObject;
import com.algotrado.entry.strategy.EntryStrategyManagerStatus;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.EXT_0001.EXT_0001;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.util.Setting;

public class TRD_0001 extends TradeManager {
	// Exit strategy locations.
	private final int EXIT_0001 = 0;
	private final int EXIT_0007 = 1;
	
	// Members
	private EntryStrategyDataObject entryStrategyDataObj;
	private ExitStrategyDataObject [] exitStrategiesList;
	private IMoneyManager moneyManager;
	
	private IDataExtractorSubject japaneseSource;
	private IDataExtractorSubject rsiSource;
	
	private JapaneseCandleBar japaneseCandleBar;
	private SimpleUpdateData rsi;
	
	/**
	 * Quantity of open position.
	 */
	private Double quantity;
	private PositionDirectionType positionDirectionType = null;
	
	public TRD_0001(EntryStrategyDataObject entryStrategyDataObj,
			IMoneyManager moneyManager, double stopLoss, double xFactor) {
		super();
		this.entryStrategyDataObj = entryStrategyDataObj;
		this.exitStrategiesList = new ExitStrategyDataObject[2];
		EXT_0001 ext0001 = new EXT_0001(this.entryStrategyDataObj.getEntry(), stopLoss);
		this.exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, 0, null);
		EXT_0007 ext0007 = new EXT_0007(this.entryStrategyDataObj.getEntry(), stopLoss, xFactor);
		this.exitStrategiesList[EXIT_0007] = new ExitStrategyDataObject(ext0007, 0, null);
		this.moneyManager = moneyManager;
		this.quantity = null;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Double> parameters) {
		// organize data for entry manager
		if (dataEventType == DataEventType.JAPANESE) {
			japaneseCandleBar = (JapaneseCandleBar) japaneseSource.getNewData();
			
		} else if (dataEventType == DataEventType.RSI) {
			rsi =  (SimpleUpdateData)rsiSource.getNewData();
		} else {
			throw new RuntimeException("Data type not supported. Please add support for data type.");
		}
		
		if (japaneseCandleBar != null && rsi != null) {
			NewUpdateData [] newData = new NewUpdateData[2];
			newData[0] = japaneseCandleBar;
			newData[1] = rsi;
			if (quantity == 0) { // there is no open position. check for entry point.
				// call entry manager to see if new entry is set.
				this.entryStrategyDataObj.getEntry().setNewData(newData);
				// if there is a new entry
				if (this.entryStrategyDataObj.getEntry().getStatus() == EntryStrategyManagerStatus.TRIGGER_BEARISH ||
						this.entryStrategyDataObj.getEntry().getStatus() == EntryStrategyManagerStatus.TRIGGER_BULLISH) {
					// request money manager to approve opening a new position
					double approvedQuantity = moneyManager.requestPermissionToOpenTrade(exitStrategiesList, entryStrategyDataObj);
					
					if (approvedQuantity > 0) { // if approved open a new position at the broker.
						// we should discuss what is the range of prices to open position.
						// and what should happen when we miss that range.
						
					}
				}
			} else {
				// if there is an active trade. than we should set new Data to exit strategies to see if any exit trigger has happened.
				
				if (this.exitStrategiesList[EXIT_0001].getExit() != null) {
					this.exitStrategiesList[EXIT_0001].getExit().setNewData(newData);
					
					// if there was an exit trigger call broker and exit. if there was error, update money manager.
					// If exited successfully update money manager.
					if (this.exitStrategiesList[EXIT_0001].getExit().getStatus() == ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS) {
						double quantityToClose = this.exitStrategiesList[EXIT_0001].getFractionToCloseOnTrigger() * quantity;
						
					}
				}
			}
			
			japaneseCandleBar = null;
			rsi = null;
		}
		
		
		
		
		
		
		
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		if (dataExtractorSubject == null) {
			japaneseSource = null;
			rsiSource = null;
		} else {
			if (dataExtractorSubject.getDataEventType() == DataEventType.JAPANESE) {
				japaneseSource = dataExtractorSubject;
			} else if (dataExtractorSubject.getDataEventType() == DataEventType.RSI) {
				rsiSource = dataExtractorSubject;
			} else {
				throw new RuntimeException("IDataExtractorSubject source not supported");
			}
		}

	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		if (dataExtractorSubject != null) {
			if (dataExtractorSubject.getDataEventType() == DataEventType.JAPANESE) {
				japaneseSource = null;
			} else if (dataExtractorSubject.getDataEventType() == DataEventType.RSI) {
				rsiSource = null;
			} /*else {
				throw new RuntimeException("IDataExtractorSubject source not supported");
			}*/
		}

	}
	
	// Reports trade + exit
	// data headers = > all data I get from trade + entry + exit
	// toString => trade entry start, entry end, exit# trigger or eliminate, exit# end time, 
	//... all exits ... , amount purchased, Gain/Loss amount  
	public String getDataHeaders() {
		String headerString = entryStrategyDataObj.getEntry().getDataHeaders();
		headerString += exitStrategiesList[EXIT_0001].getExit().getDataHeaders();
//		for(Integer cnt = EXIT_0001;cnt < exitStrategiesList.length;cnt++)
//		{
//			headerString += Setting.getDateTimeHeader("Exit " + cnt.toString() + " triggered ") + ",";
//		}
		
		headerString += Setting.getDateTimeHeader("Trade Entry Start") + ",";
		headerString += Setting.getDateTimeHeader("Trade Entry End") + ",";
		for(Integer cnt = EXIT_0001;cnt < exitStrategiesList.length;cnt++)
		{
			headerString += Setting.getDateTimeHeader("Exit " + cnt.toString() + " triggered ") + ",";
		}
		
		headerString += "Direction, Quantity puchased, Gain/Loss";
		
		return headerString;
	}
	
	@Override
	public String toString() {
		String rowStr = "";	
		
//		this.entryStrategyDataObj.getEntry().
		
		
		return "must implement this";
	}

	@Override
	public boolean forceExitAllPositions() {
		// TODO call broker to close position at market prices. if fails for some reason return false.
		
		// if success close all exit strategies.
		
		// store closing price.
		return false;
	}

	@Override
	public PositionStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

}
