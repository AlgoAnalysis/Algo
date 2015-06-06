package com.algotrado.matlab.bridge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.SimpleUpdateData;
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
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.exit.strategy.EXT_0007.EXT_0007;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.extract.data.file.FileDataExtractor;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;
import com.algotrado.pattern.PTN_0002.PTN_0002_S1;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;
import com.algotrado.trade.PositionOrderStatusType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.trade.TradeManager;
import com.algotrado.trade.TRD_0001.TRD_0001;
import com.algotrado.util.Setting;

public class MatlabJavaOptimizationBridge implements IGUIController, Runnable, IMoneyManager {
	
	private DataSource dataSource = DataSource.FILE;
	private DataEventType dataEventType;
	private List<Double> parameters;
	private int rsiLength;
	private int rsiHistoryLength;
	private List<TradeManager> tradeManagers;
	private long timeMili;
	double exit0007CloseOnTrigger;
	private double xFactor;
	private double topSpread;
	private double bottomSpread;
	private List<PatternManager> patternManagers;
	private List<Double> rsiParameters = new ArrayList<Double>();
	private EntryStrategyManager entryStrategyManager;
	private SubjectState subjectState;
	private AssetType assetType;
	private IBroker broker;
	private Semaphore semaphore;
	
	// Stats:
	private double totalNumOfEntries = 0;
	private double totalNumOfSuccesses = 0;
	
	private ExitStrategyStatus [][] exitStrategiesBehavior;
	
	public MatlabJavaOptimizationBridge() {
		
	}

	public MatlabJavaOptimizationBridge(DataSource dataSource, AssetType assetType, DataEventType dataEventType, Double [] params) {
		//super(dataSource, assetType, dataEventType, parameters);
		init(dataSource, assetType, dataEventType, params);
	}

	public void init(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, Double[] params) {
		// Reset program to start from scratch.
		FileDataExtractor.resetAccount();
		
		
		// init params for program.
		this.dataSource = dataSource;
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.tradeManagers = new ArrayList<TradeManager>();
		
		timeMili = System.currentTimeMillis();
		
		////////change here ///////////////////
		int patternType = params[0].intValue();
		int patternParametersIndex = params[1].intValue();
		IPatternState state; // Pattern code, after changing press Ctrl+shift+o
		if (patternType == 1) {
			state = new PTN_0001_S1(patternParametersIndex); 
		} else if (patternType == 2) {
			state = new PTN_0002_S1(patternParametersIndex); 
		} else if (patternType == 3) {
			state = new PTN_0003_S1(patternParametersIndex); 
		} else {
			throw new RuntimeException("patternType contains illegal value. only values 1-3 are permitted");
		}
		EntryStrategyTriggerType entryStrategyTriggerType = EntryStrategyTriggerType.values()[params[2].intValue()];
		// parameters 
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.values()[params[3].intValue()];
		// RSI parameters
		JapaneseCandleBarPropertyType japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.values()[params[4].intValue()];
		rsiLength = params[5].intValue();
		rsiHistoryLength = params[6].intValue();
		int rsiType = params[7].intValue(); // 1 (SMA) or 2 (EMA)
		//String filePath = "C:\\Algo\\test\\" + state.getCode() + "_" + entryStrategyTriggerType.toString() +"_Trade_Ext0003.csv";
		
		this.xFactor = params[8];//for the 1:x exit strategy.
		//this.fractionOfOriginalStopLoss = 0.1;// for the 1:1 exit strategy.
		this.topSpread = Setting.getUsOilTopSpread();
		this.bottomSpread = Setting.getUsOilBottomSpread();
		//this.exit0001CloseOnTrigger = 0.5;
		this.exit0007CloseOnTrigger = params[9];
		
		/*params[10].intValue();
		params[11].intValue();*/
		
		double maxRsiLongValueForEntry = params[10];
		double minRsiShortValueForEntry = params[11];
		double maxNumOfCandlesAfterPatternForEntry = params[12].intValue();
		
		// End of change values
		
		this.exitStrategiesBehavior = new ExitStrategyStatus [1][1];
		this.exitStrategiesBehavior[0][0] = ExitStrategyStatus.TRIGGER;
		
		this.broker = dataSource;
		
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
		entryStrategyParameters.add(maxNumOfCandlesAfterPatternForEntry);
		entryStrategyManager = new EntryStrategyManager(new ENT_0001_S1(entryStrategyParameters.toArray()), patternManagers, assetType.name());
		entryStrategyManager.setMoneyManager(this);
		
		subjectState = SubjectState.RUNNING;
		
		semaphore = new Semaphore(0);
		
		
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
				semaphore.release();
			}
			if (positionStatus.getPositionStatus() == PositionOrderStatusType.CLOSED) {
				totalNumOfEntries++;
				if (positionStatus.getPositionCurrGain() > 0) {
					totalNumOfSuccesses++;
				}
			}
//			notifyObservers(assetType, dataEventType, rsiParameters);
		}
	}

	@Override
	public void updateOnEntry(List<EntryStrategyStateAndTime> stateArr) {
		for (EntryStrategyStateAndTime entryStrategyStateAndTime : stateArr) {
			if (entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH ||
					entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH) {
				// create new trade for each entry, for reports only.
				IEntryStrategyLastState lastState = (IEntryStrategyLastState)entryStrategyStateAndTime.getState();
				
				double currRsiValue = -100;
				if (entryStrategyManager.getNewUpdateData() != null && 
						entryStrategyManager.getNewUpdateData().length > 1 && 
						entryStrategyManager.getNewUpdateData()[1] instanceof SimpleUpdateData) {
					currRsiValue = ((SimpleUpdateData)(entryStrategyManager.getNewUpdateData()[1])).getValue();
				}
				
				double contractAmount = broker.getContractAmount(assetType);
				// give each trade the data needed for the trade.
				ExitStrategyDataObject [] exitStrategiesList = new ExitStrategyDataObject[1];//new ExitStrategyDataObject[2];
//				EXT_0001 ext0001 = new EXT_0001(lastState, fractionOfOriginalStopLoss, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
//				exitStrategiesList[EXIT_0001] = new ExitStrategyDataObject(ext0001, exit0001CloseOnTrigger, null, contractAmount);
				IExitStrategy ext0007 = new EXT_0007(lastState, xFactor, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType));
//				IExitStrategy ext0003 = new EXT_0003(lastState, bottomSpread, topSpread, broker.getLiveSpread(assetType), broker.getCurrentAskPrice(assetType), currRsiValue, rsiLongExitValue, rsiShortExitValue);
				exitStrategiesList[TradeManager.EXIT_0007] = new ExitStrategyDataObject(ext0007, exit0007CloseOnTrigger, null, contractAmount);
				// contract amount = 500
				// account = 1000000
				// min move 1 cent = 5$
				// 1% of account = 10000 = [num of cents = (entry - stop)] * [contract amount] * [num of contracts] - TODO change quantity to constant value for optimization.
				int currTradeQuantity = (int)( ( (broker.getAccountStatus().getBalance()/100) / 
											((Math.abs(ext0007.getNewEntryPoint() - ext0007.getCurrStopLoss()) * contractAmount) *
													broker.getMinimumContractAmountMultiply(assetType) ) ) / 1000);
				
				
				
				EntryStrategyDataObject entryStrategyDataObject = new EntryStrategyDataObject(entryStrategyStateAndTime.getTimeList(), null, entryStrategyStateAndTime.getState().getStatus(), entryStrategyManager.getDataHeaders());
				TRD_0001 currTrade = new TRD_0001(entryStrategyDataObject, exitStrategiesList, this , xFactor , assetType, 0.1/*This belongs to exit strategy 1*/, currTradeQuantity, this.exitStrategiesBehavior);
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
				
				} else {
					this.tradeManagers.remove(currTrade);
				}
			} else if (entryStrategyManager.getSubjectState() == SubjectState.END_OF_LIFE) {
				subjectState = SubjectState.END_OF_LIFE;
				semaphore.release();
//				notifyObservers(assetType, dataEventType, parameters);
			}
		}

	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

	public long getTimeMili() {
		return timeMili;
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
	
	public void runSingleParamsOptimizationCheck(Double [] params) {
		if (params.length < 12) {
			System.out.println("Usage: runSingleParamsOptimizationCheck({patternType={1-3}, patternParametersIndex={1}, entryStrategyTriggerType={0,1}, " +
					"japaneseTimeFrameType={0-7}, japaneseCandleBarPropertyType={0-3}, rsiLength, rsiHistoryLength, rsiType={1-2}, xFactor={1.5 or any other value}, "
					+ "exit0007CloseOnTrigger=(0-1], maxRsiLongValueForEntry, minRsiShortValueForEntry, maxNumOfCandlesAfterPatternForEntry" + "})");
		}
		
		init(DataSource.FILE, AssetType.USOIL, DataEventType.JAPANESE, params);
		run();
		//SwingUtilities.invokeLater(this);
		
		// Try to acquire semaphore and sleep until end of run.
		
		try {
			this.getSemaphore().acquire();
		} catch (InterruptedException e) {
			System.out.println("Could not acuire semaphore for some reason.");
			e.printStackTrace();
		}
		
		System.out.println("Total time: " + (System.currentTimeMillis() - this.getTimeMili())/1000 + " Seconds.");
		
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
	
	public double getTotalNumOfEntries() {
		return totalNumOfEntries;
	}

	public double getTotalNumOfSuccesses() {
		return totalNumOfSuccesses;
	}
	
	public double getSuccessPercentage() {
		return (totalNumOfSuccesses == 0) ? 0 : totalNumOfSuccesses/totalNumOfEntries;
	}
	
	public double getAccountBalance() {
		return broker.getAccountStatus().getBalance();
	}

	public static void main(String[] args) {
		// TODO create money manager.
		// entry strategy manager.
		
		Double [] params = {/*patternType*/1.0, /*patternParametersIndex*/1.0, /*entryStrategyTriggerType*/0.0, /*japaneseTimeFrameType*/1.0, /*japaneseCandleBarPropertyType*/1.0, 
							/*rsiLength*/7.0, /*rsiHistoryLength*/0.0, /*rsiType*/1.0, /*xFactor*/1.5, /*exit0007CloseOnTrigger*/1.0, /*rsiLongExitValue 80.0, 
							/*rsiShortExitValue 20.0,*/ /*maxRsiLongValueForEntry*/80.0, /*minRsiShortValueForEntry*/20.0, /*maxNumOfCandlesAfterPatternForEntry*/5.0};
		
		
		MatlabJavaOptimizationBridge matlabJavaOB = new MatlabJavaOptimizationBridge();
		
		matlabJavaOB.runSingleParamsOptimizationCheck(params);
		
		System.out.println("Account Balance = " + matlabJavaOB.getAccountBalance());
		System.out.println("Success Percentage = " + matlabJavaOB.getSuccessPercentage());
		System.out.println("Total num of Successes = " + matlabJavaOB.getTotalNumOfSuccesses());
		System.out.println("Total num of Entries = " + matlabJavaOB.getTotalNumOfEntries());
		
	}
	
}
