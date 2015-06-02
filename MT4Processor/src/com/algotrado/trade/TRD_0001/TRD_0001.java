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
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.exit.strategy.EXT_0003.EXT_0003;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionOrderStatusType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.util.Setting;

public class TRD_0001 extends TradeManager {
	private static final String ELIMINATE = "Eliminate";
	private static final String TRIGGER = "Trigger";
	// Exit strategy locations.
	
	private static int numOfTrades = 0;
	
	// Members
	private EntryStrategyDataObject entryStrategyDataObj;
	private ExitStrategyDataObject [] exitStrategiesList;
	private int [] exitStrategiesNumbersList = {1,7};
	private ExitStrategyStatus [] [] exitStrategiesBehavior;
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
	
	private SubjectState subjectState;
	
	private double currStopLoss;
	private double lastTradeClosePosition;
	private double tradeEntryPoint;
	
	// Data headers part.
	private double xFactor;
	private double fractionOfOriginalSLExit0001;
	
	private boolean isClosedTrade = false;
	
	/**
	 * Open position will be > 0
	 */
	private int positionId;
	
	/**
	 * Quantity of open position.
	 */
	private Double quantity;
	private Double originalQuantity;
	
	private boolean performedToStringOpertion = false;
	
	public TRD_0001(EntryStrategyDataObject entryStrategyDataObj,ExitStrategyDataObject [] exitStrategiesList,
			IMoneyManager moneyManager, double xFactor, AssetType assetType, 
			double fractionOfOriginalSLExit0001, double quantity, ExitStrategyStatus [] [] exitStrategiesBehavior) {
		super();
		this.entryStrategyDataObj = entryStrategyDataObj;
		this.exitStrategiesList = exitStrategiesList;
//		EXT_0001 ext0001 = new EXT_0001(this.entryStrategyDataObj.getEntry(), stopLoss, fractionOfOriginalSLExit0001);
//		this.exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, 0, null);
//		EXT_0007 ext0007 = new EXT_0007(this.entryStrategyDataObj.getEntry(), stopLoss, xFactor);
//		this.exitStrategiesList[EXIT_0007] = new ExitStrategyDataObject(ext0007, 0, null);
		this.moneyManager = moneyManager;
		this.quantity = quantity;
		this.assetType = assetType;
		tradeStateTimeList = new ArrayList<TRD_0001.TradeStateAndTime>();
		this.subjectState = SubjectState.RUNNING;
		this.exitStrategiesBehavior = exitStrategiesBehavior;
	}
	
	public TRD_0001(String entryStrategyManagerDataHeaders, double xFactor, double fractionOfOriginalSLExit0001, ExitStrategyDataObject [] exitStrategiesList) {
		this.xFactor = xFactor;
		this.fractionOfOriginalSLExit0001 = fractionOfOriginalSLExit0001;
//		this.entryStrategyManagerDataHeaders = entryStrategyManagerDataHeaders;
		this.exitStrategiesList = exitStrategiesList;
	}
	
	public boolean startTrade() {
		if (quantity > 0) { // there is no open position. check for entry point.
			PositionDirectionType direction = 
					(this.entryStrategyDataObj.getStatus() == 
					EntryStrategyStateStatus.TRIGGER_BEARISH) ? PositionDirectionType.SHORT : PositionDirectionType.LONG;

			currStopLoss = this.exitStrategiesList[EXIT_0001].getExit().getCurrStopLoss();
			
			tradeEntryPoint = this.exitStrategiesList[EXIT_0001].getExit().getNewEntryPoint();
			
			if (direction.isValidStopLoss(currStopLoss, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType))) {
				positionId = broker.openPosition(assetType, quantity, direction, currStopLoss,0);

				originalQuantity = quantity;

				// we should discuss what is the range of prices to open position.
				// and what should happen when we miss that range.
				ArrayList<ExitState> exits = new ArrayList<ExitState>(exitStrategiesList.length);
				for (ExitStrategyDataObject exitStrategyDataObject : exitStrategiesList) {
					exits.add(new ExitState());
				}
				
				
				tradeStateTimeList.add(new TradeStateAndTime(this, exits));
				List<Date> entryDates = this.entryStrategyDataObj.getEntryDates();
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).addTime(entryDates.get(0));
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).addTime(entryDates.get(entryDates.size() - 1));
				numOfTrades++;
//				System.out.println("num Of trades " + this.getClass().getSimpleName() + " =" + numOfTrades);
				return true;
			}

			
		}
		return false;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Double> parameters) {
		if (!isClosedTrade) {
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

				updateExitStrategiesWithNewData(newData, false);

				setExitStrategiesPositionAccordingToData();

				if (japaneseCandleBar != null && rsi != null) {
					japaneseCandleBar = null;
					rsi = null;
				}
				//			}
				if ((this.japaneseSource != null && this.japaneseSource.getSubjectState() == SubjectState.END_OF_LIFE) || 
						(this.rsiSource != null && this.rsiSource.getSubjectState() == SubjectState.END_OF_LIFE) ||
						(this.quoteSource != null && this.quoteSource.getSubjectState() == SubjectState.END_OF_LIFE)) {
					subjectState = SubjectState.END_OF_LIFE;
				}

				quote = null;
			}
		} else {
			if (quoteSource != null) {
				quoteSource.unregisterObserver(this);
				if (quoteSource != null) {
					this.removeSubject(quoteSource);
				}
			}
			if (rsiSource != null) {
				rsiSource.unregisterObserver(this);
				if (rsiSource != null) {
					this.removeSubject(rsiSource);
				}
			}
			if (japaneseSource != null) {
				japaneseSource.unregisterObserver(this);
				if (japaneseSource != null) {
					this.removeSubject(japaneseSource);
				}
			}
			
		}
		
	}

	private void updateExitStrategiesWithNewData(NewUpdateData[] newData, boolean beforeOpenPosition) {
		double currBrokerSpread = broker.getLiveSpread(assetType);
		for (int i = 0; i < this.exitStrategiesList.length; i++) {
			if (this.exitStrategiesList[i].getExit() != null) {
				this.exitStrategiesList[i].getExit().setCurrBrokerSpread(currBrokerSpread);
				this.exitStrategiesList[i].getExit().setNewData(newData);
			} 
		}
	}
	private void setExitStrategiesPositionAccordingToData() {
		// Check for stop loss first.
		PositionStatus positionStatus = broker.getPositionStatus(positionId);
		double currentLivePosition = positionStatus.getCurrentPosition();
		
		boolean reachedStopLoss = (positionStatus.getPositionDirectionType() == PositionDirectionType.LONG) ? 
										(currentLivePosition <= currStopLoss) : (currentLivePosition >= currStopLoss);
		if (reachedStopLoss) {
			
			forceExitAllPositions();
		} else {
			for (int i = EXIT_0001; i < this.exitStrategiesList.length; i++) {
				if (this.exitStrategiesList[i].getExit() != null) {
					this.exitStrategiesList[i].getExit().getStatus().triggerExitState(this.exitStrategiesList[i].getExit(), this);
				}
			}
		}
		
	}

	public boolean executeExit(IExitStrategy exit, int indexindexOfStrategy) {
		boolean executed = false;
		
		boolean updateSL = true;
		/*
		 * Activate all triggered exits.
		 */
		closePartialPosition(indexindexOfStrategy);
		this.exitStrategiesList[indexindexOfStrategy].setExit(null);
		for(int i = 0; i < exitStrategiesBehavior[indexindexOfStrategy].length; i++) {
			if (this.exitStrategiesList[i].getExit() != null) {
				this.exitStrategiesList[i].getExit().setStatus(exitStrategiesBehavior[indexindexOfStrategy][i]);
				this.exitStrategiesList[i].getExit().getStatus().triggerExitState(this.exitStrategiesList[i].getExit(), this);
				updateSL = false;
			}
		}
		executed = true;

		if (updateSL) {
			PositionStatus positionStatus = broker.getPositionStatus(positionId);
			moneyManager.updatePositionStatus(positionStatus);
		}
				
		return executed;
	}
	
	public boolean executeExitAndMoveSL(IExitStrategy exit, int index) {
		// if there was an exit trigger call broker and exit. if there was error, update money manager.
		// If exited successfully update money manager.
		exit.forceTrigger();
		PositionStatus positionStatus = broker.getPositionStatus(positionId);
		this.exitStrategiesList[index].setClosingPrice(positionStatus.getCurrentPosition());
		boolean success = broker.modifyPosition(positionId, exit.getCurrStopLoss(),0);

		if (!success) {
			// Here we should think what to do if position is not closed.
			throw new RuntimeException("Broker did not modify position, Please handle with this issue.");
		}

		currStopLoss = exit.getCurrStopLoss();


		tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(index).setTriggerOrEliminate(TRIGGER);
		tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(index).setEndTime(positionStatus.getDate());

		moneyManager.updatePositionStatus(positionStatus);

		setClosedTrade();

		this.exitStrategiesList[index].setExit(null);
		
		return true;
	}
	
	public void setClosedTrade() {
		if (quantity == 0) {
			isClosedTrade = true;
		} else if (quantity < 0) {
			throw new RuntimeException("Quantity was not calculated correctly.");
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
			
			setClosedTrade();
			
//			moneyManager.updatePositionStatus(broker.getPositionStatus(positionId));
			
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
		String headerString = ""/*entryStrategyManagerDataHeaders + "\n"*/;
		headerString += "xFactor=" + xFactor + " , fractionOfOriginalSLExit0001=" + fractionOfOriginalSLExit0001;
		int index = 0;
		for (ExitStrategyDataObject exitStrategyDataObject : this.exitStrategiesList) {
			if(index == 0) {
				headerString += " , contract amount=" + exitStrategyDataObject.getContractAmount();
			}
			headerString += " ,\n percent to close on trigger " + exitStrategyDataObject.getExit().getClass().getSimpleName() + " = " + exitStrategyDataObject.getFractionToCloseOnTrigger() * 100 + "%";
			index++;
		}
		headerString += exitStrategiesList[EXIT_0001].getExit().getDataHeaders();
		headerString += "Trade Id" + ",";
		headerString += Setting.getDateTimeHeader("Trade Entry Start") + ",";
		headerString += Setting.getDateTimeHeader("Trade Entry End") + ",";
		for(Integer cnt = EXIT_0001;cnt < exitStrategiesList.length;cnt++)
		{
			headerString += "Exit " + Integer.valueOf(exitStrategiesNumbersList[cnt]).toString() + " trigger or eliminate " + ",";
			headerString += Setting.getDateTimeHeader("Exit " + Integer.valueOf(exitStrategiesNumbersList[cnt]).toString() + " end") + ",";
			headerString += "Exit " + Integer.valueOf(exitStrategiesNumbersList[cnt]).toString() + " gain/loss " + ",";
		}
		
		headerString += "Direction, Quantity purchased, Gain/Loss";
		
		return headerString;
	}
	
	@Override
	public String toString() {
		String rowStr = "";	
		
		rowStr += Setting.getDateTimeFormat(this.entryStrategyDataObj.getEntryDates().get(0)) + ", ";
		rowStr += Setting.getDateTimeFormat(this.entryStrategyDataObj.getEntryDates().get(this.entryStrategyDataObj.getEntryDates().size() - 1)) + ", ";
		
		double remainingQuantity = originalQuantity;
		int cnt = 0;
		double totalGain = 0;
		Boolean isLong = null;
		for (ExitStrategyDataObject exitStrategyDataObject : this.exitStrategiesList) {
			rowStr += tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).getTriggerOrEliminate() + ", ";
			rowStr += Setting.getDateTimeFormat(tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).getEndTime()) + ", ";
			double gain = exitStrategyDataObject.getGain(remainingQuantity);
			totalGain += gain;
			rowStr += gain + ", ";
			remainingQuantity = remainingQuantity - (remainingQuantity * exitStrategyDataObject.getFractionToCloseOnTrigger());
			isLong = exitStrategyDataObject.isLong();
			cnt++;
		}
		
		if (remainingQuantity > 0) { //the rest has been caught by SL.
			totalGain += remainingQuantity * (isLong ? 1 : (-1)) * (lastTradeClosePosition - tradeEntryPoint);
		}
		
		rowStr += ((isLong) ? PositionDirectionType.LONG.name() : PositionDirectionType.SHORT.name()) + ", ";
		rowStr += originalQuantity + ", ";
		rowStr += totalGain + ", ";
		
		if (remainingQuantity < 0) {
			throw new RuntimeException("Quantity Closed was larger than opened position. Error.");
		}
		
		performedToStringOpertion = true;
		
		return rowStr;
	}
	
	@Override
	public boolean forceExitAllPositions() {
		// TODO call broker to close position at market prices. if fails for some reason return false.
		boolean success = true;
		PositionStatus positionStatus = broker.getPositionStatus(positionId);
		if (positionStatus.getPositionStatus() != PositionOrderStatusType.CLOSED) {
			success = broker.closePosition(positionId, quantity);
		}
		quantity = Double.valueOf(0);
		if (!success) {
			// Here we should think what to do if position is not closed.
			throw new RuntimeException("Broker did not close position, Please handle with this issue.");
		}
		
		
		// if success close all exit strategies.
		
		// store closing price.
		lastTradeClosePosition = positionStatus.getCurrentPosition();
		
		for (int cnt = 0; cnt < exitStrategiesList.length; cnt++) {
			if (exitStrategiesList[cnt].getExit() != null) {
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).setTriggerOrEliminate(ELIMINATE);
				tradeStateTimeList.get(tradeStateTimeList.size() - 1).getExits().get(cnt).setEndTime(positionStatus.getDate());
				exitStrategiesList[cnt].setClosingPrice(positionStatus.getCurrentPosition());
				exitStrategiesList[cnt].setExit(null);
			}
		}
		
		setClosedTrade();
		
		moneyManager.updatePositionStatus(positionStatus);
		
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

	public ExitStrategyDataObject[] getExitStrategiesList() {
		return exitStrategiesList;
	}

	public void setBroker(IBroker broker) {
		this.broker = broker;
	}

	public SubjectState getSubjectState() {
		return subjectState;
	}
	
	public boolean isClosedTrade() {
		return isClosedTrade;
	}

	public int getPositionId() {
		return positionId;
	}

}
