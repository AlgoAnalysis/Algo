package com.algotrado.entry.strategy.test;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.entry.strategy.AEntryStrategyObserver;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.ENT_0001.ENT_0001_Observer;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;
import com.algotrado.pattern.PTN_0002.PTN_0002_S1;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;
import com.algotrado.trade.PositionStatus;

public class ENT_0001_Test  extends IDataExtractorSubject implements IGUIController,IMoneyManager {
	////////change hare ///////////////////
	// General:
	private static DataSource dataSource = DataSource.FILE;
	private static AssetType assetType = AssetType.USOIL;
	private static JapaneseTimeFrameType japanese_TimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
	// Pattern:
	private static int pattern_Type = 3; // 1 - 3
	private static double pattern_HaramiPercentageDiffOfBodySize = 0.1; // 1 - 3
	// RSI:
	private static JapaneseCandleBarPropertyType rsi_japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.CLOSE;
	private static int rsi_Length = 7;
	private static int rsi_Type = 1; // 1 (SMA) or 2 (EMA)
	// Entry: 
	private static double maxRsiLongValue = (double)80;
	private static double minRsiShortValue = (double)20;
	private static int maxNumOfCandlesAfterPattern = 5;
	private static EntryStrategyTriggerType entryStrategyTriggerType = EntryStrategyTriggerType.BUYING_CLOSE_PRICE;	
	//////////////////////////////////////////////////////
	
	private long timeMili;
	private IDataExtractorObserver dataRecorder;
	private SubjectState subjectState;
	private Double[] parameters;
	private ENT_0001_Observer ent_0001_Observer;
	private boolean testRun;
	Double deffTime;

	public static void main(String[] args) throws InterruptedException {
		ENT_0001_Test test;
		Double minTime = Double.MAX_VALUE;
		for(int cnt =0;cnt<2000;cnt++)
		{
			test = new ENT_0001_Test();
			while(test.isTestRun()) Thread.sleep(10);
			Thread.sleep(1);
			if(minTime > test.getDeffTime())
				minTime = test.getDeffTime();
		}
	
		System.out.println("minimum time = " + minTime.toString() + " Sec");
	}
	
	public ENT_0001_Test()
	{
		super(dataSource, assetType,DataEventType.TEST,(List<Double>)(new ArrayList<Double>()));
		timeMili = System.currentTimeMillis();
		testRun = true;
		String patternCode;
		parameters = new Double[ENT_0001_Observer.ENT_0001_Observer_num_of_order];
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_japanese_TimeFrameType] = (double)japanese_TimeFrameType.getValueInMinutes();
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_rsi_japaneseCandleBarPropertyType] = (double)rsi_japaneseCandleBarPropertyType.ordinal();
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_rsi_Length] = (double)rsi_Length;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_rsi_Type] = (double)rsi_Type;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_pattern_Type] = (double)pattern_Type;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_pattern_HaramiPercentageDiffOfBodySize] = pattern_HaramiPercentageDiffOfBodySize;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_entry_maxRsiLongValueForEntry] = maxRsiLongValue;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_entry_minRsiShortValueForEntry] = minRsiShortValue;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_entry_maxNumOfCandlesAfterPatternForEntry] = (double)maxNumOfCandlesAfterPattern;
		parameters[ENT_0001_Observer.ENT_0001_Observer_order_entry_StrategyTriggerType] = (double)entryStrategyTriggerType.ordinal();		
		
		switch(pattern_Type)
		{
		case 1:
			patternCode = (new PTN_0001_S1(1)).getCode();
			break;
		case 2:
			patternCode = (new PTN_0002_S1(1)).getCode();
			break;
		case 3:
			patternCode = (new PTN_0003_S1(1)).getCode();
			break;
		default:
			patternCode = "";
			break;
		}
		String filePath = "C:\\Algo\\test\\" + patternCode +"_"+ entryStrategyTriggerType.toString()+"_EntryStrategy.csv";

		ent_0001_Observer = new ENT_0001_Observer(assetType, null, parameters, dataSource, this);
		dataRecorder = new FileDataRecorder(filePath, this);		
		this.registerObserver(dataRecorder);
		subjectState = SubjectState.RUNNING;
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
		deffTime = Double.valueOf((double)(System.currentTimeMillis() - timeMili)/1000);
		System.out.println(deffTime.toString() + " Sec");
		testRun = false;
	}
	
	@Override
	public String toString() {
		return ent_0001_Observer.toString(); 
	}


	@Override
	public NewUpdateData getNewData() {
		// not implement because the file recorder not need this.
		return null;
	}


	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + japanese_TimeFrameType.getValueInMinutes() + "\n" + 
				"Data Source," + dataSource.toString() + "\n" + 
				ent_0001_Observer.getDataHeaders();
	}


	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}

	@Override
	public DataEventType getDataEventType() {
		return null;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		super.parameters = parameters;
	}
	
	


	@Override
	public void updatePositionStatus(PositionStatus positionStatus) {
		// Not necessary 
		
	}

	@Override
	public double getQuantityForEntry(
			IEntryStrategyLastState entryStrategyLastState) {
		this.notifyObservers(assetType, dataEventType, null);
		return 0;
	}

	@Override
	public void entrySucceeded(IEntryStrategyLastState entryStrategyLastState,
			double quantity, Integer tradeID) {
		// Not necessary 
		
	}

	@Override
	public void entryObserverEndOfLife(
			AEntryStrategyObserver entryStrategyObserver) {
		subjectState = SubjectState.END_OF_LIFE;
		this.notifyObservers(assetType, dataEventType, null);
		
	}
	
	@Override
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
		observer.removeSubject(this);
	}

	public boolean isTestRun() {
		return testRun;
	}

	public Double getDeffTime() {
		return deffTime;
	}

	
}
