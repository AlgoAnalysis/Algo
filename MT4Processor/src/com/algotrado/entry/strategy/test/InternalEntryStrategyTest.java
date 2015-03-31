package com.algotrado.entry.strategy.test;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.entry.strategy.EntryStrategyManager;
import com.algotrado.entry.strategy.EntryStrategyManagerStatus;
import com.algotrado.entry.strategy.EntryStrategyTriggerType;
import com.algotrado.entry.strategy.ENT_0001.ENT_0001_S1;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.IPatternState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;

public class InternalEntryStrategyTest  extends IDataExtractorSubject implements IGUIController , IDataExtractorObserver, Runnable {

	private long timeMili;
	private List<PatternManager> patternManagers;
	private IDataExtractorSubject dataExtractorSubject;
	private IDataExtractorSubject rsiDataExtractorSubject;
	private IDataExtractorObserver dataRecorder;
	private SubjectState subjectState;
	private static DataSource dataSource = DataSource.FILE;
	private List<Double> rsiParameters = new ArrayList<Double>();
	private int rsiLength;
	private int rsiHistoryLength;
	private EntryStrategyManager entryStrategyManager;
	private JapaneseCandleBar japaneseCandle = null;
	private SimpleUpdateData rsi = null;
	private NewUpdateData[] newUpdateData;
	
	public InternalEntryStrategyTest()
	{
		super(dataSource, AssetType.USOIL,DataEventType.JAPANESE,(List<Double>)(new ArrayList<Double>()));
		timeMili = System.currentTimeMillis();
		
		////////change hare ///////////////////
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
		
		dataRecorder = new FileDataRecorder(filePath, this);
//		dataRecorder.setSubject(this);
		this.registerObserver(dataRecorder);
		
		subjectState = SubjectState.RUNNING;
	}
	
	@Override
	public void run() {
		/**
		 * Register to RSI.
		 */
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, rsiParameters,rsiHistoryLength, this);	
		/**
		 * Register to japanese candles.
		 */
		RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters,0, this);
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Double> parameters) {
		//Build new Data contains {JapaneseCandleBar, RSI}
		newUpdateData = null;
		
		if (dataEventType == DataEventType.JAPANESE) {
			japaneseCandle = ((JapaneseCandleBar)this.dataExtractorSubject.getNewData());
		} else if (dataEventType == DataEventType.RSI) {
			rsi = (SimpleUpdateData)this.rsiDataExtractorSubject.getNewData();
		}
		
		if (japaneseCandle != null  && rsi != null) {
			newUpdateData = new NewUpdateData[2];
			newUpdateData[0] = japaneseCandle;
			newUpdateData[1] = rsi;
			entryStrategyManager.setNewData(newUpdateData);
			
			if (entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BEARISH || 
					entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BULLISH) {
				notifyObservers(assetType, dataEventType, parameters);
			}
			rsi = null;
			japaneseCandle = null;
		}
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		if (dataExtractorSubject == null) {
			this.dataExtractorSubject = null;
			this.rsiDataExtractorSubject = null;
			return;
		}
		if (dataExtractorSubject.getDataEventType() == DataEventType.RSI) {
			this.rsiDataExtractorSubject = dataExtractorSubject;
		} else {
			this.dataExtractorSubject = dataExtractorSubject;
		}
	}
	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		setSubject(null);
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
	public String toString() {
		return entryStrategyManager.toString(); 
	}


	@Override
	public NewUpdateData getNewData() {
		// not implement because the file recorder not need this.
		return null;
	}


	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.toString() + "\n" + 
				entryStrategyManager.getDataHeaders();
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
		this.parameters = parameters;
	}

}
