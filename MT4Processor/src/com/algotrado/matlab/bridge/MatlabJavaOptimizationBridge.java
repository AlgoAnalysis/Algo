package com.algotrado.matlab.bridge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.management.RuntimeErrorException;

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
import com.algotrado.output.file.FileDataRecorder;
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
	
	private static final int WEEKS_IN_YEAR = 54;
	private static final long MINUTES_IN_MILISEC = 60*1000;
	private static final long HOUR_IN_MINUTES = 60;
	private static final long WEEK_IN_MINUTES = 24*60*7;
	
	private DataSource dataSource = DataSource.FILE;
	private DataEventType dataEventType;
	private List<Double> parameters;
	private int rsiLength;
	private int rsiHistoryLength;
	private List<TradeManager> tradeManagers;
	double exit0007CloseOnTrigger;
	private double xFactor;
	private double topSpread;
	private double bottomSpread;
	private List<PatternManager> patternManagers;
	private List<Double> rsiParameters;
	private EntryStrategyManager entryStrategyManager;
	private SubjectState subjectState;
	private AssetType assetType;
	private IBroker broker;
	private boolean runDone;
	private JapaneseTimeFrameType japaneseTimeFrameType;
	private int depth = 5;
	private double deviation = 5;
	private int backstep = 3;
	private int maxHistoryLength = 4;
	
	// Stats:
	private double totalNumOfEntries;
	private double totalNumOfSuccesses;
	private long maxHighForDrawDown;
	private long highPointAccountBalanceAtMaxDrawDown;
	private long minLowForDrawDown;
	private double [][] minMaxAccountBalanceForDrawDown;
	private List<double []> minMaxAccountBalanceForDrawDownList;
	private long maxDrawDownOfBalance;
	private long currValueForDrawDown;
	private long minAccountBalance;
	
	private double sumOfProfits;
	private double sumOfLosses;
	
	private int hourToStartApproveTrades;
	private int windowLengthInHoursToApproveTrades;
	
	private MoneyManagerTradeDirection moneyManagerTradeDirection;
	private boolean shouldTradeShorts;
	private boolean shouldTradeLongs;
	
	/**
	 * This is for checking specific trade periods.
	 * I.e. To Be able to check only 4 weeks of trades without actually cutting the file that includes all trades. 
	 */
	private long startAllTradesTimeStamp;
	private long endAllTradesTimestamp;
	
	private double initialAccountBalance;
	
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
		this.totalNumOfEntries = 0;
		this.totalNumOfSuccesses = 0;
		
		// init params for program.
		this.dataSource = dataSource;
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.tradeManagers = new ArrayList<TradeManager>();
		
		
		this.sumOfProfits = 0;
		this.sumOfLosses = 0;
		
		
		////////change here ///////////////////
		int patternType = params[0].intValue();
//		int patternParametersIndex = params[1].intValue();
		IPatternState state; // Pattern code, after changing press Ctrl+shift+o
		if (patternType == 1) {
			state = new PTN_0001_S1(new Double [] {params[1]}); 
		} else if (patternType == 2) {
			state = new PTN_0002_S1(new Double [] {params[1]}); 
		} else if (patternType == 3) {
			state = new PTN_0003_S1(new Double [] {params[1]}); 
		} else {
			throw new RuntimeException("patternType contains illegal value. only values 1-3 are permitted");
		}
		EntryStrategyTriggerType entryStrategyTriggerType = EntryStrategyTriggerType.values()[params[2].intValue()];
		// parameters 
		japaneseTimeFrameType = JapaneseTimeFrameType.values()[params[3].intValue()];
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
		
		rsiParameters = new ArrayList<Double>();
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
		
		runDone = false;
		initialAccountBalance = broker.getAccountStatus().getBalance();
		maxHighForDrawDown = new Double(initialAccountBalance).longValue();
		minLowForDrawDown = (long) 1E12;
		minAccountBalance = (long) 1E12;
		maxDrawDownOfBalance = 0;
		currValueForDrawDown = 0;
		moneyManagerTradeDirection = MoneyManagerTradeDirection.BOTH;
		if (params.length >= 15) {
			hourToStartApproveTrades = new Double(params[13]).intValue();
			windowLengthInHoursToApproveTrades = new Double(params[14]).intValue();
			if (params.length >= 16) {
				if (params[15].intValue() > 2 || params[15].intValue() < 0) {
					throw new RuntimeException("Invalid param for trade direction of money manager, should be {0-2}.");
				}
				moneyManagerTradeDirection = MoneyManagerTradeDirection.values()[params[15].intValue()];
			}
			
			if (params.length >= 17) {
				if (params[16].longValue() > 0 && startAllTradesTimeStamp > 0) {
					endAllTradesTimestamp = startAllTradesTimeStamp + (params[16].longValue() * WEEK_IN_MINUTES * MINUTES_IN_MILISEC);
				}
			}
		} else {// trade all hours of day
			hourToStartApproveTrades = -1;
			windowLengthInHoursToApproveTrades = -1;
		}
		
		shouldTradeShorts = moneyManagerTradeDirection == MoneyManagerTradeDirection.SHORT || moneyManagerTradeDirection == MoneyManagerTradeDirection.BOTH;
		shouldTradeLongs = moneyManagerTradeDirection == MoneyManagerTradeDirection.LONG || moneyManagerTradeDirection == MoneyManagerTradeDirection.BOTH;
		
		
		
		minMaxAccountBalanceForDrawDownList = new ArrayList<double[]>();
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
				runDone = true;
			}
			if (positionStatus.getPositionStatus() == PositionOrderStatusType.CLOSED) {
				totalNumOfEntries++;
				if (positionStatus.getPositionCurrGain() > 0) {
					totalNumOfSuccesses++;
					sumOfProfits += positionStatus.getPositionCurrGain();
				} else {
					sumOfLosses -= positionStatus.getPositionCurrGain();
				}
				currValueForDrawDown = new Double (broker.getAccountStatus().getBalance()).longValue();
				
				long currMinDrawDown = minLowForDrawDown;
				if (currValueForDrawDown > maxHighForDrawDown) {
					if (maxHighForDrawDown > 0 && minLowForDrawDown < maxHighForDrawDown) {
						double [] firstminThanMaxArr = new double[2];
						firstminThanMaxArr[0] = minLowForDrawDown;
						firstminThanMaxArr[1] = maxHighForDrawDown;
						minMaxAccountBalanceForDrawDownList.add(firstminThanMaxArr);
					}
					maxHighForDrawDown = currValueForDrawDown;
//					System.out.println("new max high" + maxHighForDrawDown);
					minLowForDrawDown = (long) 1E12;//Start counting new low after a new highest point has been set for draw down.
				} else if (currValueForDrawDown < minLowForDrawDown) {
					minLowForDrawDown = currValueForDrawDown;
//					System.out.println("new min low" + minLowForDrawDown);
					currMinDrawDown = minLowForDrawDown;
				}
				
				if (currValueForDrawDown < minAccountBalance) {
					minAccountBalance = currValueForDrawDown;
				}
				
				long currDrawdown = maxHighForDrawDown - currMinDrawDown;
				
				if (currDrawdown > 0                           /*maxDrawDownOfBalance*/) {
					double currRelativeMaxDrawDown = (double)maxDrawDownOfBalance/(double)highPointAccountBalanceAtMaxDrawDown;
					double currRelativeDrawDown = (double)currDrawdown / (double)maxHighForDrawDown;
					if (currRelativeDrawDown > currRelativeMaxDrawDown || maxDrawDownOfBalance == 0) {
						maxDrawDownOfBalance = currDrawdown;
						highPointAccountBalanceAtMaxDrawDown = maxHighForDrawDown;
					}
				}
			}
//			notifyObservers(assetType, dataEventType, rsiParameters);
		}
		
		if (runDone) {
			fillDrawDownArray();
		}
	}

	private void fillDrawDownArray() {
		if(minLowForDrawDown < maxHighForDrawDown){
			// Add last min max couple for draw down.
			double [] firstminThanMaxArr = new double[2];
			firstminThanMaxArr[0] = minLowForDrawDown;
			firstminThanMaxArr[1] = maxHighForDrawDown;
			minMaxAccountBalanceForDrawDownList.add(firstminThanMaxArr);
		}
		minMaxAccountBalanceForDrawDown = new double[minMaxAccountBalanceForDrawDownList.size()][2];
		int i = 0;
		for (double [] firstminThanMaxArrForLoop : minMaxAccountBalanceForDrawDownList ) {
			minMaxAccountBalanceForDrawDown[i][0] = firstminThanMaxArrForLoop[0];
			minMaxAccountBalanceForDrawDown[i][1] = firstminThanMaxArrForLoop[1];
			i++;
		}
	}

	@Override
	public void updateOnEntry(List<EntryStrategyStateAndTime> stateArr, Date entryDateAndTime) {
		for (EntryStrategyStateAndTime entryStrategyStateAndTime : stateArr) {
			boolean shouldOpenTrade = (entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH && shouldTradeShorts) || 
					(entryStrategyStateAndTime.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH && shouldTradeLongs);
			
			shouldOpenTrade = shouldOpenTrade && entryDateAndTime.getTime() >= startAllTradesTimeStamp &&
					entryDateAndTime.getTime() <= endAllTradesTimestamp;
			
			if (shouldOpenTrade) {
				
				if (hourToStartApproveTrades >= 0 && hourToStartApproveTrades < 24 && 
						windowLengthInHoursToApproveTrades > 0 && windowLengthInHoursToApproveTrades <= 24) {//limit trades to given hours.
					Calendar calendarOfEntryTime = GregorianCalendar.getInstance();
					calendarOfEntryTime.setTimeInMillis(entryDateAndTime.getTime());
					
					Calendar startTradeCalendar = getTodayStartTradeCalendar(entryDateAndTime);
					long startTradeTime = startTradeCalendar.getTimeInMillis();
					long endTradeTime = startTradeCalendar.getTimeInMillis() + (windowLengthInHoursToApproveTrades * HOUR_IN_MINUTES * MINUTES_IN_MILISEC);
					if (calendarOfEntryTime.getTimeInMillis() >= startTradeTime && calendarOfEntryTime.getTimeInMillis() <= endTradeTime) {
						openNewTrade(entryStrategyStateAndTime);
					} else {//move one day back and check trade times.
						startTradeCalendar.add(Calendar.DATE, -1);
						startTradeTime = startTradeCalendar.getTimeInMillis();
						endTradeTime = startTradeCalendar.getTimeInMillis() + (windowLengthInHoursToApproveTrades * HOUR_IN_MINUTES * MINUTES_IN_MILISEC);
						if (calendarOfEntryTime.getTimeInMillis() >= startTradeTime && calendarOfEntryTime.getTimeInMillis() <= endTradeTime) {
							openNewTrade(entryStrategyStateAndTime);
						}
					}
				} else { //trade all hours of day.
					openNewTrade(entryStrategyStateAndTime);
				}
			} 
		}
		
		if (entryStrategyManager.getSubjectState() == SubjectState.END_OF_LIFE) {
			subjectState = SubjectState.END_OF_LIFE;
			runDone = true;
			fillDrawDownArray();
		}

	}
	
	private Calendar getTodayStartTradeCalendar(Date today){
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(today.getTime());
		calendar.set(Calendar.HOUR_OF_DAY, hourToStartApproveTrades);
		calendar.set(Calendar.MINUTE, 0);
		return calendar;
	}

	private void openNewTrade(EntryStrategyStateAndTime entryStrategyStateAndTime) {
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
			
			DataSource dataSource = DataSource.FILE;
			AssetType assetType = AssetType.USOIL;
			
			
			String filePath = "C:\\Algo\\test\\Zigzag_on_" + assetType.name()+".csv";
			
//			List<Double> parameters = new ArrayList<Double>();
//			
//			parameters.add((double)japaneseTimeFrameType.getValueInMinutes());
//			parameters.add((double)depth);
//			parameters.add(deviation);
//			parameters.add((double)backstep);
//			RegisterDataExtractor.register(dataSource, assetType, DataEventType.ZIGZAG, parameters,maxHstoryLength, currTrade);
		
		} else {
			this.tradeManagers.remove(currTrade);
		}
	}

	@Override
	public void run() {
		
		RegisterDataExtractor.resetRegisterDataExtractor();
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
		runSingleParamsOptimizationCheck(params, null);
	}
	
	public void runSingleParamsOptimizationCheck(Double [] params, String [] DateStr) throws RuntimeException {
		if (params.length < 15) {
			System.out.println("Usage: runSingleParamsOptimizationCheck({patternType={1-3}, patternParametersIndex={1}, entryStrategyTriggerType={0,1}, " +
					"japaneseTimeFrameType={0-7}, japaneseCandleBarPropertyType={0-3}, rsiLength, rsiHistoryLength, rsiType={1-2}, xFactor={1.5 or any other value}, "
					+ "exit0007CloseOnTrigger=(0-1], maxRsiLongValueForEntry, minRsiShortValueForEntry, maxNumOfCandlesAfterPatternForEntry, start trade hour 0-23, how many hours to trade 1-24"
					+ "moneyManagerTradeDirection={0-2}, " + "{trade window length in weeks > 0}, " + "}, {String date format of start trading date ('yyyy/MM/dd') all numbers})");
			return;
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		if (DateStr == null || DateStr[0] == null) { // Trade all the trades.
			startAllTradesTimeStamp = 0;
			endAllTradesTimestamp = WEEK_IN_MINUTES * MINUTES_IN_MILISEC * WEEKS_IN_YEAR * 100000;
		} else {
			try {
				Date startTradeDate = format.parse(DateStr[0]);
				startAllTradesTimeStamp = startTradeDate.getTime();
				endAllTradesTimestamp = 0; // No trade will be made unless this has been properly configured.
				if (params.length >= 17 && params[16].longValue() <= 0) {
					System.out.println("No trade will be made unless Trade window length has been properly configured");
				}
			} catch (ParseException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		init(DataSource.FILE, AssetType.USOIL, DataEventType.JAPANESE, params);
		run();
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
//		Double deffTime = Double.valueOf((double)(System.currentTimeMillis() - timeMili)/1000);
//		System.out.println(deffTime.toString() + " Sec");
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
	

	public double getInitialAccountBalance() {
		return initialAccountBalance;
	}

	public double getMaxDrawDown() {
		return (double)maxDrawDownOfBalance/(double)highPointAccountBalanceAtMaxDrawDown;
	}
	
	public boolean isRunDone() {
		return runDone;
	}

	public double[][] getMinMaxAccountBalanceForDrawDown() {
		return minMaxAccountBalanceForDrawDown;
	}
	
	/**
	 * 
	 * @return Loss = (account balance start - account balance total min) / account balance start
	 */
	public double getLossFromInitialAccount() {
		return (double)(initialAccountBalance - minAccountBalance)/(double)initialAccountBalance;
	}
	
	public double getAverageProfitTrade() {
		return sumOfProfits / totalNumOfSuccesses;
	}
	
	public double getAverageLossTrade() {
		return sumOfLosses / (double)(totalNumOfEntries - totalNumOfSuccesses);
	}
	
	public double getProfitFactor () {
		return sumOfProfits / sumOfLosses;
	}
	
	public double getGrossProfit () {
		return sumOfProfits;
	}
	
	public double getGrossLoss () {
		return sumOfLosses;
	}

	public static void main(String[] args) {
		// TODO create money manager.
		// entry strategy manager.
		long minimumTime = (long)Integer.MAX_VALUE;
		long timeMili;
		long exeTime;
		
		Double [] params = {
				(double)1,//patternType
				(double)0.08,//HaramiPercentageDiffOfBodySize
				(double)0,//entryStrategyTriggerType
				(double)1,//japaneseTimeFrameType
				(double)0,//japaneseCandleBarPropertyType
				(double)18,//rsiLength
				(double)0,//rsiHistoryLength
				(double)2,//rsiType
				(double)0.5,//xFactor
				(double)1,//exit0007CloseOnTrigger
				(double)8,//maxRsiLongValueForEntry
				(double)45,//minRsiShortValueForEntry
				(double)70,//maxNumOfCandlesAfterPatternForEntry
				(double)2.75,//HourInDayToStartApprovingTrades
				(double)1,//LengthOfTradeApprovalWindow
				2.0,/*moneyManagerTradeDirection*/
				208.0/*window length in weeks*/
		};
		
//		Double [] params = {/*patternType*/1.0, 
//							/*Harami Percentage diff Of body size*/0.1, 
//							/*entryStrategyTriggerType*/0.0, 
//							/*japaneseTimeFrameType*/1.0, 
//							/*japaneseCandleBarPropertyType*/1.0, 
//							/*rsiLength*/7.0, 
//							/*rsiHistoryLength*/0.0, 
//							/*rsiType*/1.0, 
//							/*xFactor*/5.0, 
//							/*exit0007CloseOnTrigger*/1.0,
//							/*maxRsiLongValueForEntry*/80.0, 
//							/*minRsiShortValueForEntry*/20.0, 
//							/*maxNumOfCandlesAfterPatternForEntry*/5.0, 
//							/*Hour in day to start approving trades*/ 8.0, 
//							/*Length of TRade approval window*/ 8.0, 
//							/*moneyManagerTradeDirection*/ 2.0, 
//							/*window length in weeks*/ 8.0};
		
//		Double [] params = {/*patternType*/1.0, /*Harami Percentage diff Of body size*/0.1, /*entryStrategyTriggerType*/1.0, /*japaneseTimeFrameType*/1.0, /*japaneseCandleBarPropertyType*/1.0, 
//				/*rsiLength*/8.0, /*rsiHistoryLength*/0.0, /*rsiType*/1.0, /*xFactor*/9.5, /*exit0007CloseOnTrigger*/1.0, /*rsiLongExitValue 80.0, 
//				/*rsiShortExitValue 20.0,*/ /*maxRsiLongValueForEntry*/50.0, /*minRsiShortValueForEntry*/0.0, /*maxNumOfCandlesAfterPatternForEntry*/20.0, /*maxNumOfCandlesAfterPatternForEntry*/-5.0, 
//				/*Hour in day to start approving trades*/ -8.0, /*Length of TRade approval window*/ -8.0, /*moneyManagerTradeDirection*/ 0.0};
		
		String startTradeDateStr = "2013/02/03";
		String[] strParams = new String[] {startTradeDateStr};
		
		MatlabJavaOptimizationBridge matlabJavaOB = new MatlabJavaOptimizationBridge();
		for (int i = 1; i <= 10; i++) {
			timeMili = System.currentTimeMillis();
			matlabJavaOB.runSingleParamsOptimizationCheck(params, strParams);
			while(!matlabJavaOB.isRunDone())
			{
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			exeTime = (System.currentTimeMillis() - timeMili);
			System.out.println("Total time: " + exeTime + " miliseconds.");
			System.out.println("Initial Account Balance = " + matlabJavaOB.getInitialAccountBalance());
			System.out.println("Initial Account Loss = " + matlabJavaOB.getLossFromInitialAccount());
			System.out.println("Account Balance = " + matlabJavaOB.getAccountBalance());
			System.out.println("Success Percentage = " + matlabJavaOB.getSuccessPercentage());
			System.out.println("Gross Profit = " + matlabJavaOB.getGrossProfit());
			System.out.println("Gross Loss = " + matlabJavaOB.getGrossLoss());
			System.out.println("Average Profit Trade = " + matlabJavaOB.getAverageProfitTrade());
			System.out.println("Average Losing Trade = " + matlabJavaOB.getAverageLossTrade());
			System.out.println("Profit factor = " + matlabJavaOB.getProfitFactor());
			System.out.println("Total num of Successes = " + matlabJavaOB.getTotalNumOfSuccesses());
			System.out.println("Total num of Entries = " + matlabJavaOB.getTotalNumOfEntries());
			System.out.println("Total max draw down = " + matlabJavaOB.getMaxDrawDown());
			System.out.println("Draw Down Arrays: \n");
			int drawDownIndex = 0;
			for (double [] minMaxDrawDownArr : matlabJavaOB.getMinMaxAccountBalanceForDrawDown()) {
				System.out.println((drawDownIndex++) + ") " + Arrays.toString(minMaxDrawDownArr));
			}
			System.out.println("\n");
			if(exeTime < minimumTime)
			{
				minimumTime = exeTime;
			}
		}
		
		System.out.println("\nMinimum total time: " + minimumTime + " miliseconds.");
	}
	
}
