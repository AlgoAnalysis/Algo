package com.algotrado.money.manager.demo;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.entry.strategy.EntryStrategyDataObject;
import com.algotrado.entry.strategy.EntryStrategyManager;
import com.algotrado.entry.strategy.EntryStrategyManager.EntryStrategyStateAndTime;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.ENT_0001.ENT_0001_S1;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.exit.strategy.EXT_0001.EXT_0001;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;
import com.algotrado.trade.PositionOrderStatusType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.trade.TRD_0001.TRD_0001;
import com.algotrado.util.Setting;

public class TradeManagerReportDemo extends IDataExtractorSubject implements IGUIController, Runnable, IMoneyManager {
	
	private static final int EXIT_0001 = 0;
	private static final int EXIT_0007 = 1;
	private static DataSource dataSource = DataSource.FILE;
	private int rsiLength;
	private int rsiHistoryLength;
	private List<PatternManager> patternManagers;
	private List<Double> rsiParameters = new ArrayList<Double>();
	private EntryStrategyManager entryStrategyManager;
	private IDataExtractorObserver dataRecorder;
	private SubjectState subjectState;
	private long timeMili;
	
	private IBroker broker;
	
	private List<TradeManager> tradeManagers;
	
	public TradeManagerReportDemo() {
		super(dataSource, AssetType.USOIL, DataEventType.JAPANESE, (List<Double>)(new ArrayList<Double>()));
		this.tradeManagers = new ArrayList<TradeManager>();
		
		timeMili = System.currentTimeMillis();
		
		////////change here ///////////////////
		IPatternState state = new PTN_0003_S1(1); // Pattern code, after changing press Ctrl+shift+o
		EntryStrategyTriggerType entryStrategyTriggerType = EntryStrategyTriggerType.BUYING_CLOSE_PRICE;
		// parameters 
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		// RSI parameters
		JapaneseCandleBarPropertyType japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.CLOSE;
		rsiLength = 7;
		rsiHistoryLength = 0;
		int rsiType = 1; // 1 (SMA) or 2 (EMA)
		String filePath = "C:\\Algo\\test\\" + state.getCode() +"_"+ entryStrategyTriggerType.toString()+"_EntryStrategy.csv";
		//////////////////////////////////////////////////////

		patternManagers = new ArrayList<PatternManager>();
		patternManagers.add(new PatternManager(state));
		parameters = new ArrayList<Double>();
		parameters.add((double) japaneseTimeFrameType.getValueInMinutes());  
		
		List<Double> entryStrategyParameters = new ArrayList<Double>(); 
		entryStrategyParameters.addAll(parameters);
		entryStrategyParameters.add((double) entryStrategyTriggerType.ordinal());
		
		rsiParameters.add((double)japaneseTimeFrameType.getValueInMinutes());
		rsiParameters.add((double)japaneseCandleBarPropertyType.ordinal());
		rsiParameters.add((double)rsiLength);
		rsiParameters.add((double)rsiType); // RSI type
		entryStrategyManager = new EntryStrategyManager(new ENT_0001_S1(entryStrategyParameters.toArray()), patternManagers, AssetType.USOIL.name());
		entryStrategyManager.setMoneyManager(this);
		
		
		dataRecorder = new FileDataRecorder(filePath, this);
//		dataRecorder.setSubject(this);
		this.registerObserver(dataRecorder);
		
		subjectState = SubjectState.RUNNING;
		// and link the to report builder.
	}


	public static void main(String[] args) {
		// TODO create money manager.
		// entry strategy manager.
		TradeManagerReportDemo tradeManagerReportDemo = new TradeManagerReportDemo();
		SwingUtilities.invokeLater(tradeManagerReportDemo);
	}

	@Override
	public void updatePositionStatus(PositionStatus positionStatus) {
		// notify data recorder of closed positions.
		if (positionStatus.getPositionStatus() == PositionOrderStatusType.CLOSED) {
			notifyObservers(assetType, dataEventType, rsiParameters);
		}
	}

	@Override
	public void updateOnEntry(List<EntryStrategyStateAndTime> stateArr) {
		for (EntryStrategyStateAndTime entryStrategyStateAndTime : stateArr) {
			if (entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH ||
					entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH) {
				// create new trade for each entry, for reports only.
				IEntryStrategyLastState lastState = (IEntryStrategyLastState)entryStrategyStateAndTime.getState();
				
				// Change values here.
				AssetType assetType = AssetType.USOIL;
				int currTradeQuantity = 2;
				double xFactor = 2;//for the 1:x exit strategy.
				double fractionOfOriginalStopLoss = 0.1;// for the 1:1 exit strategy.
				double topSpread = Setting.getUsOilTopSpread();
				double bottomSpread = Setting.getUsOilBottomSpread();
				double exit0001CloseOnTrigger = 0.5;
				double exit0007CloseOnTrigger = 0.5;
				// end of change values
				
				// give each trade the data needed for the trade.
				ExitStrategyDataObject [] exitStrategiesList = new ExitStrategyDataObject[2];
				EXT_0001 ext0001 = new EXT_0001(lastState, fractionOfOriginalStopLoss, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
				exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, exit0001CloseOnTrigger, null);
				EXT_0007 ext0007 = new EXT_0007(lastState, xFactor, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
				exitStrategiesList[EXIT_0007] = new ExitStrategyDataObject(ext0007, exit0007CloseOnTrigger, null);
				
				EntryStrategyDataObject entryStrategyDataObject = new EntryStrategyDataObject(entryStrategyStateAndTime.getTimeList(), null, entryStrategyManager.getStatus(), entryStrategyManager.getDataHeaders());
				TRD_0001 currTrade = new TRD_0001(entryStrategyDataObject, exitStrategiesList, this , xFactor , assetType, fractionOfOriginalStopLoss, currTradeQuantity);
				this.tradeManagers.add(currTrade);
				
				currTrade.setBroker(broker);
				currTrade.startTrade();
				
				/**
				 * Register to RSI.
				 */
				RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, rsiParameters,rsiHistoryLength, currTrade);	
				/**
				 * Register to Japanese candles.
				 */
				RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters,0, currTrade);
				
				RegisterDataExtractor.register(dataSource, assetType, DataEventType.NEW_QUOTE, rsiParameters,rsiHistoryLength, currTrade);
			}
		}

	}


	@Override
	public void run() {
		/**
		 * Register to RSI.
		 */
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, rsiParameters,rsiHistoryLength, entryStrategyManager);	
		/**
		 * Register to Japanese candles.
		 */
		RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters,0, entryStrategyManager);
		
	}

	@Override
	public void setErrorMessage(String ErrorMsg, boolean endProgram) {
		System.out.println("Error: " + ErrorMsg);
		if (endProgram) {
			throw new RuntimeException	("");
		}

	}

	@Override
	public void resetGUI() {
		Double deffTime = Double.valueOf((double)(System.currentTimeMillis() - timeMili)/1000);
		System.out.println(deffTime.toString() + " Sec");
	}

	@Override
	public NewUpdateData getNewData() {
		// not implement because the file recorder not need this.
		return null;
	}

	@Override
	public DataEventType getDataEventType() {
		return null;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		this.parameters = parameters;
	}

	// Reports trade + exit
	// data headers = > all data I get from trade + entry + exit
	// toString => trade entry start, entry end, exit# trigger or eliminate, exit# end time, 
	//... all exits ... , amount purchased, Gain/Loss amount 
	@Override
	public String getDataHeaders() {
		String entryStrategyDataHeaders = entryStrategyManager.getDataHeaders();
		String newEntryStrategyDataHeaders = "";
		String[] splitDataHeaders = entryStrategyDataHeaders.split("\n");
		int cnt = 0;
		for (String single : splitDataHeaders) {// Remove last row from entry Strategy headers.
			if (cnt != (splitDataHeaders.length - 1)) {
				newEntryStrategyDataHeaders += single;
			}
			cnt++;
		}
		String headerString = newEntryStrategyDataHeaders;
		headerString += tradeManagers.get(0).getDataHeaders();
		
		return headerString;
	}
	
	public String toString() {
		String toString = "";
		for (TradeManager tradeManager : tradeManagers) {
			toString += tradeManager.toString();
		}
		return toString;
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}


	public void setRsiLength(int rsiLength) {
		this.rsiLength = rsiLength;
	}


	public void setRsiHistoryLength(int rsiHistoryLength) {
		this.rsiHistoryLength = rsiHistoryLength;
	}

}
