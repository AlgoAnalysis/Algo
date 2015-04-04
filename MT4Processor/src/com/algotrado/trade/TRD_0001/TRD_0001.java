package com.algotrado.trade.TRD_0001;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyDataObject;
import com.algotrado.entry.strategy.EntryStrategyManagerStatus;
import com.algotrado.entry.strategy.IEntryStrategyFirstState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.EXT_0001.EXT_0001;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.util.Setting;

public class TRD_0001 extends TradeManager {
	private static final String ELIMINATE = "Eliminate";
	private static final String TRIGGER = "Trigger";
	// Exit strategy locations.
	private final int EXIT_0001 = 0;
	private final int EXIT_0007 = 1;
	
	// Members
	private EntryStrategyDataObject entryStrategyDataObj;
	private ExitStrategyDataObject [] exitStrategiesList;
	private IMoneyManager moneyManager;
	private IBroker broker;
	private AssetType assetType;
	
	private IDataExtractorSubject japaneseSource;
	private IDataExtractorSubject rsiSource;
	private IDataExtractorSubject quoteSource;
	
	private JapaneseCandleBar japaneseCandleBar;
	private SimpleUpdateData rsi;
	private JapaneseCandleBar quote;
	
	private List<TradeStateAndTime> tradeStateTimeList; 
	
	private double currStopLoss;
	
	/**
	 * Open position will be > 0
	 */
	private int positionId;
	
	/**
	 * Quantity of open position.
	 */
	private Double quantity;
	private Double originalQuantity;
	private PositionDirectionType positionDirectionType = null;
	
	public TRD_0001(EntryStrategyDataObject entryStrategyDataObj,
			IMoneyManager moneyManager, double stopLoss, double xFactor, AssetType assetType, double fractionOfOriginalSLExit0001) {
		super();
		this.entryStrategyDataObj = entryStrategyDataObj;
		this.exitStrategiesList = new ExitStrategyDataObject[2];
		EXT_0001 ext0001 = new EXT_0001(this.entryStrategyDataObj.getEntry(), stopLoss, fractionOfOriginalSLExit0001);
		this.exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, 0, null);
		EXT_0007 ext0007 = new EXT_0007(this.entryStrategyDataObj.getEntry(), stopLoss, xFactor);
		this.exitStrategiesList[EXIT_0007] = new ExitStrategyDataObject(ext0007, 0, null);
		this.moneyManager = moneyManager;
		this.quantity = null;
		this.assetType = assetType;
		tradeStateTimeList = new ArrayList<TRD_0001.TradeStateAndTime>();
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Double> parameters) {
		// organize data for entry manager
		if (dataEventType == DataEventType.JAPANESE) {
			japaneseCandleBar = (JapaneseCandleBar) japaneseSource.getNewData();
		} else if (dataEventType == DataEventType.RSI) {
			rsi =  (SimpleUpdateData)rsiSource.getNewData();
		} else if (dataEventType == DataEventType.NEW_QUOTE) {
			// TODO - need support in case the minimum time frame is not JapaneseCandleBar (not file)
			SimpleUpdateData simpleUpdateData = (SimpleUpdateData)this.quoteSource.getNewData();
			quote = new JapaneseCandleBar(simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getVolume(), simpleUpdateData.getTime(), simpleUpdateData.getAssetName());
		} else {
			throw new RuntimeException("Data type not supported. Please add support for data type.");
		}
		
		if (quote != null) {
			NewUpdateData [] newData = new NewUpdateData[2];
			newData[0] = japaneseCandleBar;
			newData[1] = rsi;
			if (quantity == 0 && japaneseCandleBar != null && rsi != null) { // there is no open position. check for entry point.
				newData = new NewUpdateData[2];
				newData[0] = japaneseCandleBar;
				newData[1] = rsi;
				// call entry manager to see if new entry is set.
				this.entryStrategyDataObj.getEntry().setNewData(newData);
				// if there is a new entry
				if (this.entryStrategyDataObj.getEntry().getStatus() == EntryStrategyManagerStatus.TRIGGER_BEARISH ||
						this.entryStrategyDataObj.getEntry().getStatus() == EntryStrategyManagerStatus.TRIGGER_BULLISH) {
					// request money manager to approve opening a new position
					double approvedQuantity = moneyManager.requestPermissionToOpenTrade(exitStrategiesList, entryStrategyDataObj);
					
					if (approvedQuantity > 0) { // if approved open a new position at the broker.
						PositionDirectionType direction = 
								(this.entryStrategyDataObj.getEntry().getStatus() == 
										EntryStrategyManagerStatus.TRIGGER_BEARISH) ? PositionDirectionType.SHORT : PositionDirectionType.LONG;
						newData = new NewUpdateData[3];
						newData[0] = quote;
						newData[1] = japaneseCandleBar;
						newData[2] = rsi;
						
						updateExitStrategiesWithNewData(newData, true);
						
						currStopLoss = this.exitStrategiesList[EXIT_0001].getExit().getNewStopLoss();
						
						positionId = broker.openPosition(assetType, approvedQuantity, direction, currStopLoss);
						
						originalQuantity = approvedQuantity;
						quantity = approvedQuantity;
						
						if (positionId > 0) { // position has opened.
							setExitStrategiesPositionAccordingToData();
						}
						// we should discuss what is the range of prices to open position.
						// and what should happen when we miss that range.
						tradeStateTimeList.add(new TradeStateAndTime(this, new ArrayList<ExitState>(exitStrategiesList.length)));
						List<Date> entryDates = this.entryStrategyDataObj.getEntry().getLastEntryDates();
						tradeStateTimeList.get(tradeStateTimeList.size() - 1).addTime(entryDates.get(0));
						tradeStateTimeList.get(tradeStateTimeList.size() - 1).addTime(entryDates.get(entryDates.size() - 1));
						
					}
				}
				japaneseCandleBar = null;
				rsi = null;
			} else {
				// if there is an active trade. than we should set new Data to exit strategies to see if any exit trigger has happened.
				if (japaneseCandleBar != null && rsi != null) {
					newData = new NewUpdateData[3];
					newData[0] = quote;
					newData[1] = japaneseCandleBar;
					newData[2] = rsi;
				} else {
					newData = new NewUpdateData[1];
					newData[0] = quote;
				}
				
				
//				
				
				updateExitStrategiesWithNewData(newData, false);
				
				setExitStrategiesPositionAccordingToData();
				
				if (japaneseCandleBar != null && rsi != null) {
					japaneseCandleBar = null;
					rsi = null;
				}
			}
			
			quote = null;
		}
		
	}

	private void updateExitStrategiesWithNewData(NewUpdateData[] newData, boolean beforeOpenPosition) {
		if (this.exitStrategiesList[EXIT_0001].getExit() != null) {
			this.exitStrategiesList[EXIT_0001].getExit().setNewData(newData);
		} else if (this.exitStrategiesList[EXIT_0007].getExit() != null) {
			this.exitStrategiesList[EXIT_0007].getExit().setNewData(newData);
		}
	}
	private void setExitStrategiesPositionAccordingToData() {
		// Check for stop loss first.
		PositionStatus positionStatus = broker.getPositionStatus(positionId);
		double currentLivePosition = positionStatus.getCurrentPosition();
		
		boolean reachedStopLoss = (positionStatus.getPositionDirectionType() == PositionDirectionType.LONG) ? 
										(currentLivePosition < currStopLoss) : (currentLivePosition > currStopLoss);
		if (reachedStopLoss) {
			forceExitAllPositions();
		} else {
			boolean executed = executeExit0007();
			if (!executed) {
				executeExit0001();
			}
		}
		
	}

	private boolean executeExit0007() {
		boolean executed = false;
		if (this.exitStrategiesList[EXIT_0007].getExit() != null) {
			// if there was an exit trigger call broker and exit. if there was error, update money manager.
			// If exited successfully update money manager.
			if (this.exitStrategiesList[EXIT_0007].getExit().getStatus() == ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS) {
				closePartialPosition(EXIT_0007);
				executed = true;
				moveExit0001StopLoss();
				this.exitStrategiesList[EXIT_0007].setExit(null);
			}
		}
		return executed;
	}
	
	private void moveExit0001StopLoss() {
		if (this.exitStrategiesList[EXIT_0001].getExit() != null) {
			// if there was an exit trigger call broker and exit. if there was error, update money manager.
			// If exited successfully update money manager.
			this.exitStrategiesList[EXIT_0001].getExit().forceTrigger();
			boolean success = broker.modifyPosition(positionId, this.exitStrategiesList[EXIT_0001].getExit().getNewStopLoss());
			
			if (!success) {
				// Here we should think what to do if position is not closed.
				throw new RuntimeException("Broker did not modify position, Please handle with this issue.");
			}
			
			PositionStatus positionStatus = broker.getPositionStatus(positionId);
			
			tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(EXIT_0001).setTriggerOrEliminate(TRIGGER);
			tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(EXIT_0001).setEndTime(positionStatus.getDate());
			
			moneyManager.updatePositionStatus(positionStatus);
			
			this.exitStrategiesList[EXIT_0001].setExit(null);
		}
	}

	private void executeExit0001() {
		if (this.exitStrategiesList[EXIT_0001].getExit() != null) {
			// if there was an exit trigger call broker and exit. if there was error, update money manager.
			// If exited successfully update money manager.
			if (this.exitStrategiesList[EXIT_0001].getExit().getStatus() == ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS) {
				closePartialPosition(EXIT_0001);
				this.exitStrategiesList[EXIT_0001].getExit().forceTrigger();
				boolean success = broker.modifyPosition(positionId, this.exitStrategiesList[EXIT_0001].getExit().getNewStopLoss());
				
				if (!success) {
					// Here we should think what to do if position is not closed.
					throw new RuntimeException("Broker did not modify position, Please handle with this issue.");
				}
				
				moneyManager.updatePositionStatus(broker.getPositionStatus(positionId));
				
				this.exitStrategiesList[EXIT_0001].setExit(null);
			}
		}
	}

	private void closePartialPosition(int index) {
		double quantityToClose = this.exitStrategiesList[index].getFractionToCloseOnTrigger() * quantity;
		if (quantityToClose > 0) { // Maybe quantityToClose should be rounded up/down to a sum that broker can close?
			boolean success = broker.closePosition(positionId, quantityToClose);
			if (quantityToClose > quantity) {
				throw new RuntimeException("Error quantity calculated was bigger than open position.");
			}
			quantity -= quantityToClose;
			if (!success) {
				// Here we should think what to do if position is not closed.
				throw new RuntimeException("Broker did not close position, Please handle with this issue.");
			} else {
				PositionStatus positionStatus = broker.getPositionStatus(positionId);
				exitStrategiesList[index].setClosingPrice(positionStatus.getCurrentPosition());
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(index).setTriggerOrEliminate(TRIGGER);
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(index).setEndTime(positionStatus.getDate());
			}
			
			moneyManager.updatePositionStatus(broker.getPositionStatus(positionId));
			
		}
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		if (dataExtractorSubject == null) {
			japaneseSource = null;
			rsiSource = null;
			quoteSource = null;
		} else {
			if (dataExtractorSubject.getDataEventType() == DataEventType.JAPANESE) {
				japaneseSource = dataExtractorSubject;
			} else if (dataExtractorSubject.getDataEventType() == DataEventType.RSI) {
				rsiSource = dataExtractorSubject;
			} else if (dataExtractorSubject.getDataEventType() == DataEventType.NEW_QUOTE) {
				quoteSource = dataExtractorSubject;
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
			} else if (dataExtractorSubject.getDataEventType() == DataEventType.NEW_QUOTE) {
				quoteSource = null;
			}
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
		boolean success = broker.closePosition(positionId, quantity);
		quantity = Double.valueOf(0);
		if (!success) {
			// Here we should think what to do if position is not closed.
			throw new RuntimeException("Broker did not close position, Please handle with this issue.");
		}
		
		moneyManager.updatePositionStatus(broker.getPositionStatus(positionId));
		// if success close all exit strategies.
		
		// store closing price.
		PositionStatus positionStatus = broker.getPositionStatus(positionId);
		
		for (int cnt = 0; cnt < exitStrategiesList.length; cnt++) {
			if (exitStrategiesList[cnt].getExit() != null) {
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).setTriggerOrEliminate(ELIMINATE);
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).setEndTime(positionStatus.getDate());
				exitStrategiesList[cnt].setExit(null);
				exitStrategiesList[cnt].setClosingPrice(positionStatus.getCurrentPosition());
			}
		}
		
		return true;
	}

	@Override
	public PositionStatus getStatus() {
		return broker.getPositionStatus(positionId);
	}
	
	private class ExitState
	{
		private String triggerOrEliminate;
		private Date endTime;
		public String getTriggerOrEliminate() {
			return triggerOrEliminate;
		}
		public void setTriggerOrEliminate(String triggerOrEliminate) {
			this.triggerOrEliminate = triggerOrEliminate;
		}
		public Date getEndTime() {
			return endTime;
		}
		public void setEndTime(Date endTime) {
			this.endTime = endTime;
		}
		
	}
	
	private class TradeStateAndTime
	{
		private TradeManager manager;
		private List<Date> timeList; 
		private List<ExitState> exits;
		public TradeStateAndTime(TradeManager manager, List<ExitState> exits)
		{
			this.manager = manager;
			this.timeList = new ArrayList<Date>();
			this.exits = exits;
		}
		
		public boolean addTime(Date time)
		{
			return timeList.add(time);
		}
		
		public List<Date> getTimeList()
		{
			return timeList;
		}
		public TradeManager getTrade() {
			return manager;
		}

		public List<ExitState> getExits() {
			return exits;
		}
	}

}
