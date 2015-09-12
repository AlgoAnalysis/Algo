package com.algotrado.entry.strategy.ENT_0001;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.AEntryStrategyObserver;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.IPatternLastState;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;
import com.algotrado.pattern.PTN_0002.PTN_0002_S1;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;

public class ENT_0001_Observer extends AEntryStrategyObserver {
	public final static int ENT_0001_Observer_order_japanese_TimeFrameType 						= 0;
	public final static int ENT_0001_Observer_order_rsi_japaneseCandleBarPropertyType 			= 1;
	public final static int ENT_0001_Observer_order_rsi_Length 									= 2;
	public final static int ENT_0001_Observer_order_rsi_Type 									= 3;
	public final static int ENT_0001_Observer_order_pattern_Type 								= 4;
	public final static int ENT_0001_Observer_order_pattern_HaramiPercentageDiffOfBodySize 		= 5;
	public final static int ENT_0001_Observer_order_entry_maxRsiLongValueForEntry 				= 6;
	public final static int ENT_0001_Observer_order_entry_minRsiShortValueForEntry 				= 7;
	public final static int ENT_0001_Observer_order_entry_maxNumOfCandlesAfterPatternForEntry 	= 8;
	public final static int ENT_0001_Observer_order_entry_StrategyTriggerType 					= 9; 
	public final static int ENT_0001_Observer_order_entry_HourInDayToStartApprovingTrades 		= 10; // TODO - MNM???
	public final static int ENT_0001_Observer_order_entry_LengthOfTradeApprovalWindow 			= 11; // TODO - MNM???
	public final static int ENT_0001_Observer_order_entry_TradeDirection 						= 12; // TODO - MNM???
	public final static int ENT_0001_Observer_num_of_order 										= 13;
	
	public final static int  ENT_0001_Observer_Subject_japanese 	= 0;
	public final static int  ENT_0001_Observer_Subject_rsi 			= 1;
	public final static int  ENT_0001_Observer_Subject_quote 		= 2;
	public final static int  ENT_0001_Observer_Subject_num			= 3;
	private List<Double> rsiParameters;
	List<Double> japaneseParameters;
	
	private SubjectState japaneseState;
	private SubjectState rsiState;
	private SubjectState quoteState;
	private EntryStrategyTriggerType entryStrategyTriggerType;
	
	public ENT_0001_Observer(AssetType assetType,IBroker broker,
			Double[] Parameters,DataSource dataSource,IMoneyManager moneyManager) {
		super(dataSource,assetType, broker,moneyManager);
		rsiParameters = new ArrayList<Double>();
		rsiParameters.add(Parameters[ENT_0001_Observer_order_japanese_TimeFrameType]);
		rsiParameters.add(Parameters[ENT_0001_Observer_order_rsi_japaneseCandleBarPropertyType]);
		rsiParameters.add(Parameters[ENT_0001_Observer_order_rsi_Length]);
		rsiParameters.add(Parameters[ENT_0001_Observer_order_rsi_Type]); 
		japaneseParameters = new ArrayList<Double>();
		japaneseParameters.add(Parameters[ENT_0001_Observer_order_japanese_TimeFrameType]);
		
		this.entryStrategyTriggerType = EntryStrategyTriggerType.getEntryStrategyTriggerType((Parameters[ENT_0001_Observer_order_entry_StrategyTriggerType]).intValue());
		
				
		// Entry's
		
		Object[] entryParameters = new Double[ENT_0001_MAIN.ENT_0001_parammetrs_numbers];
		entryParameters[ENT_0001_MAIN.ENT_0001_parammetrs_order_maxRsiLongValue] = Parameters[ENT_0001_Observer_order_entry_maxRsiLongValueForEntry];
		entryParameters[ENT_0001_MAIN.ENT_0001_parammetrs_order_minRsiShortValue] = Parameters[ENT_0001_Observer_order_entry_minRsiShortValueForEntry];
		entryParameters[ENT_0001_MAIN.ENT_0001_parammetrs_order_maxNumOfCandlesAfterPattern] = Parameters[ENT_0001_Observer_order_entry_maxNumOfCandlesAfterPatternForEntry];
		entryParameters[ENT_0001_MAIN.ENT_0001_parammetrs_order_StrategyTriggerType] = Parameters[ENT_0001_Observer_order_entry_StrategyTriggerType];
		firstState = new ENT_0001_S1(entryParameters, this);
		
		// Patterns
		IPatternFirstState[] patternfirstStateArr = new IPatternFirstState[1];
		Object[] PatternParameters = new Double[1];
		PatternParameters[0] = Parameters[ENT_0001_Observer_order_pattern_HaramiPercentageDiffOfBodySize];
		switch(Parameters[ENT_0001_Observer_order_pattern_Type].intValue())
		{
		case 1:
			patternfirstStateArr[0] = new PTN_0001_S1(PatternParameters);
			break;
		case 2:
			patternfirstStateArr[0] = new PTN_0002_S1(PatternParameters);
			break;
		case 3:
			patternfirstStateArr[0] = new PTN_0003_S1(PatternParameters);
			break;
		default:
			throw new RuntimeException	("pattern not support!!!"); 
		}
		
		
		init(patternfirstStateArr);
		// do all register
		subjectArr = new IDataExtractorSubject[ENT_0001_Observer_Subject_num];
		if(this.entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_BREAK_PRICE)
		{
			RegisterDataExtractor.register(dataSource, assetType, DataEventType.NEW_QUOTE, new ArrayList<Double>(),0, this);
		}
		else
		{
			quoteState = SubjectState.END_OF_LIFE;
		}
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, rsiParameters,0, this);
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.JAPANESE, japaneseParameters,0, this);
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		if (dataExtractorSubject == null) {
			this.subjectArr[ENT_0001_Observer_Subject_japanese] = null;
			this.subjectArr[ENT_0001_Observer_Subject_rsi] = null;
			this.subjectArr[ENT_0001_Observer_Subject_quote]	 = null;
			return;
		}
		if (dataExtractorSubject.getDataEventType() == DataEventType.RSI) {
			this.subjectArr[ENT_0001_Observer_Subject_rsi] = dataExtractorSubject;
			rsiState = dataExtractorSubject.getSubjectState();
		} else if (dataExtractorSubject.getDataEventType() == DataEventType.JAPANESE) {
			this.subjectArr[ENT_0001_Observer_Subject_japanese] = dataExtractorSubject;
			japaneseState = dataExtractorSubject.getSubjectState();
		} else {
			this.subjectArr[ENT_0001_Observer_Subject_quote] = dataExtractorSubject;
			quoteState = dataExtractorSubject.getSubjectState();
		}
	}

	@Override
	public void patternTrigger(IPatternLastState patternLastState){
		updateENT_0001_S1(patternLastState);

	}

	private SimpleUpdateData lastQuote;
	private SimpleUpdateData lastRsi = new SimpleUpdateData(null, new Date(0), 0, 0);
	private JapaneseCandleBar lastJapaneseCandle = new JapaneseCandleBar(0,0,0,0,0,new Date(0),"");
	@Override
	public synchronized void notifyObserver(DataEventType dataEventType,
			List<Double> parameters) {
		
		boolean rsiAndJapaneseReady = false;
		switch(dataEventType)
		{
		case NEW_QUOTE:
			quoteState = subjectArr[ENT_0001_Observer_Subject_quote].getSubjectState();
			if(quoteState == SubjectState.RUNNING)
			{
				lastQuote = (SimpleUpdateData)subjectArr[ENT_0001_Observer_Subject_quote].getNewData();
				// update only state 2, (state 1 and pattern don't use quote)
				updateENT_0001_S2();
			}
			break;
		case JAPANESE:
			japaneseState = subjectArr[ENT_0001_Observer_Subject_japanese].getSubjectState();
			if(japaneseState == SubjectState.RUNNING)
			{
				lastJapaneseCandle = (JapaneseCandleBar)subjectArr[ENT_0001_Observer_Subject_japanese].getNewData();
				udateData = new NewUpdateData[1];
				udateData[0] = lastJapaneseCandle;
				patternManagers.get(0).setNewData(udateData);
				if(lastJapaneseCandle.getTime().getTime() == lastRsi.getTime().getTime())
				{
					rsiAndJapaneseReady = true;
				}
			}
			break;
		case RSI:
			rsiState = subjectArr[ENT_0001_Observer_Subject_rsi].getSubjectState();
			if(rsiState == SubjectState.RUNNING)
				{
				lastRsi = (SimpleUpdateData)subjectArr[ENT_0001_Observer_Subject_rsi].getNewData();
				if(lastJapaneseCandle.getTime().getTime() == lastRsi.getTime().getTime())
				{
					rsiAndJapaneseReady = true;
				}
			}
			break;
		default:
			throw new RuntimeException("rong dataEventType type");
		}
		if(rsiState == SubjectState.END_OF_LIFE && japaneseState == SubjectState.END_OF_LIFE && quoteState == SubjectState.END_OF_LIFE)
		{
			EntryStrategyObserverEndOfLive();
			unregisterAllSubjects();
		}
		else if(rsiAndJapaneseReady && (entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_CLOSE_PRICE))
		{
			updateENT_0001_S2();
		}
	
	} // notifyObserver
	
	private void updateENT_0001_S2()
	{
		udateData = new NewUpdateData[2];
		udateData[ENT_0001_S2.ENT_0001_S2_newData_order_Quote_or_JapaneseCandle] = (entryStrategyTriggerType == EntryStrategyTriggerType.BUYING_CLOSE_PRICE) ? lastJapaneseCandle : lastQuote;
		udateData[ENT_0001_S2.ENT_0001_S2_newData_order_Rsi] = lastRsi;
		updateEntryStates(2);
	}
	
	private void updateENT_0001_S1(IPatternLastState patternLastState)
	{
		udateData = new NewUpdateData[1];
		udateData[ENT_0001_S1.ENT_0001_S2_newData_order_lastPatternState] = patternLastState;
		updateEntryStates(1);
	}
	
	
	
}
