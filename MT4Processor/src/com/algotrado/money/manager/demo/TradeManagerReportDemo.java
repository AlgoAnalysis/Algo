package com.algotrado.money.manager.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.entry.strategy.EntryStrategyManager;
import com.algotrado.entry.strategy.EntryStrategyManager.EntryStrategyStateAndTime;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.ENT_0001.ENT_0001_S1;
import com.algotrado.exit.strategy.ExitStrategyDataObject;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.exit.strategy.EXT_0003.EXT_0003;
import com.algotrado.exit.strategy.EXT_0004.EXT_0004;
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
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionOrderStatusType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.trade.TRD_0001.TRD_0001;
import com.algotrado.util.Setting;

public class TradeManagerReportDemo extends IDataExtractorSubject implements IGUIController, Runnable, IMoneyManager {
	
	private static final int EXIT_0001 = 0;
	private static final int EXIT_0003 = 0;
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
	private double rsiLongExitValue;
	private double rsiShortExitValue;
	
	private double xFactor;
	private double fractionOfOriginalStopLoss;
	double topSpread;
	double bottomSpread;
	double exit0001CloseOnTrigger;
	double exit0007CloseOnTrigger;
	
	int depth;
	double deviation;
	int backstep;
	int maxHistoryLength;
	
	private JapaneseTimeFrameType japaneseTimeFrameType;
	
	private ExitStrategyStatus [][] exitStrategiesBehavior;
	
	private static int numOfTrades = 0;
	
	private IBroker broker;
	
	private List<TradeManager> tradeManagers;
	
	public TradeManagerReportDemo() {
		super(dataSource, AssetType.USOIL, DataEventType.JAPANESE, (List<Double>)(new ArrayList<Double>()));
		this.tradeManagers = new ArrayList<TradeManager>();
		
		timeMili = System.currentTimeMillis();
		
		////////change here ///////////////////
		IPatternState state = new PTN_0001_S1(1); // Pattern code, after changing press Ctrl+shift+o
		EntryStrategyTriggerType entryStrategyTriggerType = EntryStrategyTriggerType.BUYING_BREAK_PRICE;
		// parameters 
		japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		// RSI parameters
		JapaneseCandleBarPropertyType japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.CLOSE;
		rsiLength = 7;
		rsiHistoryLength = 0;
		int rsiType = 1; // 1 (SMA) or 2 (EMA)
		String filePath = "C:\\Algo\\test\\" + state.getCode() + "_" + entryStrategyTriggerType.toString() +"_Trade_Ext0004.csv";
		
//		AssetType assetType = AssetType.USOIL;
		this.xFactor = 1.5;//for the 1:x exit strategy.
		this.fractionOfOriginalStopLoss = 0.1;// for the 1:1 exit strategy.
		this.topSpread = Setting.getUsOilTopSpread();
		this.bottomSpread = Setting.getUsOilBottomSpread();
		this.exit0001CloseOnTrigger = 0.5;
		this.exit0007CloseOnTrigger = 1;
		
		this.rsiLongExitValue = 90;
		this.rsiShortExitValue = 10;
		
		this.depth = 5;
		this.deviation = 5;
		this.backstep = 3;
		this.maxHistoryLength = 4;
		
		double maxRsiLongValueForEntry = (double)80;
		double minRsiShortValueForEntry = (double)20;
		double maxNumOfCandlesAfterPattern = 5;
		
		// end of change values
		
		this.broker = dataSource;
		
		/*this.exitStrategiesBehavior = new ExitStrategyStatus [2][2];
		this.exitStrategiesBehavior[0][0] = ExitStrategyStatus.TRIGGER;
		this.exitStrategiesBehavior[0][1] = ExitStrategyStatus.RUN;
		this.exitStrategiesBehavior[1][0] = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
		this.exitStrategiesBehavior[1][1] = ExitStrategyStatus.TRIGGER;*/
		
		this.exitStrategiesBehavior = new ExitStrategyStatus [1][1];
		this.exitStrategiesBehavior[0][0] = ExitStrategyStatus.TRIGGER;
		
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
		entryStrategyParameters.add(maxRsiLongValueForEntry);
		entryStrategyParameters.add(minRsiShortValueForEntry);
		entryStrategyParameters.add(maxNumOfCandlesAfterPattern);
		entryStrategyManager = new EntryStrategyManager(new ENT_0001_S1(entryStrategyParameters.toArray()), patternManagers, AssetType.USOIL.name());
		entryStrategyManager.setMoneyManager(this);
		
		
		dataRecorder = new FileDataRecorder(filePath, this);
//		dataRecorder.setSubject(this);
		
		
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
		boolean isEndOfLife = false;
		if (positionStatus.getPositionStatus() != PositionOrderStatusType.CLOSED) {
			for (TradeManager tradeManager : tradeManagers) {
				if (tradeManager.getSubjectState() == SubjectState.END_OF_LIFE) {
					isEndOfLife = true;
					break;
				}
			}
		}
		
		if (positionStatus.getPositionStatus() == PositionOrderStatusType.CLOSED || isEndOfLife) {
			if (isEndOfLife) {
				subjectState = SubjectState.END_OF_LIFE;
			}
			notifyObservers(assetType, dataEventType, rsiParameters);
		}
	}

	@Override
	public void updateOnEntry(List<EntryStrategyStateAndTime> stateArr, Date entryDateAndTime) {
		for (EntryStrategyStateAndTime entryStrategyStateAndTime : stateArr) {
			if (entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH ||
					entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH) {
				// create new trade for each entry, for reports only.
				IEntryStrategyLastState lastState = (IEntryStrategyLastState)entryStrategyStateAndTime.getState();
				
//				double currRsiValue = -100;
//				if (entryStrategyManager.getNewUpdateData() != null && 
//						entryStrategyManager.getNewUpdateData().length > 1 && 
//						entryStrategyManager.getNewUpdateData()[1] instanceof SimpleUpdateData) {
//					currRsiValue = ((SimpleUpdateData)(entryStrategyManager.getNewUpdateData()[1])).getValue();
//				}
				PositionDirectionType positionDirectionType = (entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH) ?
																PositionDirectionType.SHORT : PositionDirectionType.LONG;
				double contractAmount = broker.getContractAmount(assetType);
				// give each trade the data needed for the trade.
				ExitStrategyDataObject [] exitStrategiesList = new ExitStrategyDataObject[1];//new ExitStrategyDataObject[2];
//				EXT_0001 ext0001 = new EXT_0001(lastState, fractionOfOriginalStopLoss, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
//				exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, exit0001CloseOnTrigger, null, contractAmount);
//				IExitStrategy ext0007 = new EXT_0007(lastState, xFactor, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
				IExitStrategy ext0004 = new EXT_0004(lastState, positionDirectionType, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
//				IExitStrategy ext0003 = new EXT_0003(lastState, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType), currRsiValue, rsiLongExitValue, rsiShortExitValue);
				exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0004, exit0007CloseOnTrigger, null, contractAmount);
				// contract amount = 500
				// account = 1000000
				// min move 1 cent = 5$
				// 1% of account = 10000 = [num of cents = (entry - stop)] * [contract amount] * [num of contracts]
				int currTradeQuantity = (int)( ( (broker.getAccountStatus().getBalance()/100) / 
											((Math.abs(ext0004.getNewEntryPoint() - ext0004.getCurrStopLoss()) * contractAmount) *
													broker.getMinimumContractAmountMultiply(assetType) ) ) / 1000);
				
				
				
				TRD_0001 currTrade = new TRD_0001(entryStrategyStateAndTime, exitStrategiesList, this , assetType, currTradeQuantity, this.exitStrategiesBehavior);
				this.tradeManagers.add(currTrade);
				
				currTrade.setBroker(broker);
				boolean openedTrade = currTrade.startTrade();
				
				if (openedTrade) {
					/**
					 * Register to RSI.
					 */
					RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, rsiParameters,rsiHistoryLength, currTrade);	
					/**
					 * Register to Japanese candles.
					 */
					RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters,0, currTrade);
					
					RegisterDataExtractor.register(dataSource, assetType, DataEventType.NEW_QUOTE, new ArrayList<Double>(),rsiHistoryLength, currTrade);
					
					
					String filePath = "C:\\Algo\\test\\Zigzag_on_" + assetType.name()+".csv";
					///////////////////////////////////////
					List<Double> zigzagParameters = new ArrayList<Double>();
					
					zigzagParameters.add((double)japaneseTimeFrameType.getValueInMinutes());
					zigzagParameters.add((double)depth);
					zigzagParameters.add(deviation);
					zigzagParameters.add((double)backstep);
					RegisterDataExtractor.register(dataSource, assetType, DataEventType.ZIGZAG, zigzagParameters,maxHistoryLength, currTrade);
				
				} else {
					this.tradeManagers.remove(currTrade);
				}
			} else if (entryStrategyManager.getSubjectState() == SubjectState.END_OF_LIFE) {
				subjectState = SubjectState.END_OF_LIFE;
				notifyObservers(assetType, dataEventType, parameters);
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
		
		this.registerObserver(dataRecorder);
		
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
		double contractAmount = broker.getContractAmount(assetType);
		ExitStrategyDataObject [] exitStrategiesList = new ExitStrategyDataObject[1];
//		IExitStrategy ext0001 = new EXT_0001(bottomSpread, topSpread);
//		exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, exit0001CloseOnTrigger, null, contractAmount);
		IExitStrategy ext0003 = new EXT_0003(bottomSpread, topSpread);
		exitStrategiesList[EXIT_0003] = new ExitStrategyDataObject(ext0003, 1, null, contractAmount);
//		IExitStrategy ext0007 = new EXT_0007(bottomSpread, topSpread);
//		exitStrategiesList[EXIT_0007] = new ExitStrategyDataObject(ext0007, exit0007CloseOnTrigger, null, contractAmount);
		
		TRD_0001 currTrade = new TRD_0001(entryStrategyManager.getDataHeaders(), this.xFactor, this.fractionOfOriginalStopLoss, exitStrategiesList);
		headerString += currTrade.getDataHeaders();
		
		return headerString;
	}
	
	public String toString() {
		String toString = "";
		
		for (Iterator<TradeManager> iterator = tradeManagers.iterator(); iterator.hasNext();) {
			TradeManager tradeManager =  iterator.next();
			if (tradeManager.isClosedTrade() || tradeManager.getSubjectState() == SubjectState.END_OF_LIFE) {
				
				toString += ((TRD_0001)tradeManager).getPositionId() + "," + tradeManager.toString() + "\n";
//				tradeManager.unregisterObserver(dataSource, dataEventType, parameters, rsiParameters);
				iterator.remove();
			}
		}
		if (toString.endsWith("\n")) {
			toString = toString.substring(0, toString.lastIndexOf('\n'));
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
