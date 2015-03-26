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
import com.algotrado.entry.strategy.ENT_0001.ENT_0001_S1;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
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
	private int rsiLength = 7;
	private int rsiHistoryLength = 0;
	private EntryStrategyManager entryStrategyManager;
	private List<JapaneseCandleBar> japaneseCandleList = new ArrayList<JapaneseCandleBar>();
	private List<SimpleUpdateData> rsiList = new ArrayList<SimpleUpdateData>();
	private NewUpdateData[] newUpdateData;
	
	public InternalEntryStrategyTest()
	{
		super(dataSource, AssetType.USOIL,DataEventType.JAPANESE,(List<Double>)(new ArrayList<Double>()));
		timeMili = System.currentTimeMillis();
//		PTN_0001_S1 state = new PTN_0001_S1(1);
//		PTN_0002_S1 state = new PTN_0002_S1(1);
		PTN_0003_S1 state = new PTN_0003_S1(1);
		patternManagers = new ArrayList<PatternManager>();
		patternManagers.add(new PatternManager(state));
		String filePath = "C:\\Algo\\test\\" + state.getCode() + "EntryStrategy.csv";
		parameters = new ArrayList<Double>();
		parameters.add((double) 5);
		
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		JapaneseCandleBarPropertyType japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.CLOSE;
		
		List<Double> entryStrategyParameters = new ArrayList<Double>(); 
		entryStrategyParameters.addAll(parameters);
		entryStrategyParameters.add((double) 0);//Buy trigger by close price.
//		entryStrategyParameters.add((double) 1);//Buy trigger by breakout price.
		
		rsiParameters.add((double)japaneseTimeFrameType.getValueInMinutes());
		rsiParameters.add((double)japaneseCandleBarPropertyType.ordinal());
		rsiParameters.add((double)rsiLength);
		rsiParameters.add((double)1); // RSI type
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
			japaneseCandleList.add((JapaneseCandleBar)this.dataExtractorSubject.getNewData());
//			if (rsiList.size() < japaneseCandleList.size() && japaneseCandleList.getTime().after(rsiList.getTime())) {
//				rsiList = null;
//			}
		} else if (dataEventType == DataEventType.RSI) {
			rsiList.add((SimpleUpdateData)this.rsiDataExtractorSubject.getNewData());
//			if (japaneseCandleList != null && rsiList.getTime().after(japaneseCandleList.getTime())) {
//				japaneseCandleList = null;
//			}
		}
		
		if (japaneseCandleList.size() > 0  && rsiList.size() > 0) {
			newUpdateData = new NewUpdateData[2];
			newUpdateData[0] = (rsiList.size() < japaneseCandleList.size()) ? japaneseCandleList.get(rsiList.size() - 1) : japaneseCandleList.get(japaneseCandleList.size() - 1);
			newUpdateData[1] = (rsiList.size() < japaneseCandleList.size()) ? rsiList.get(rsiList.size() - 1) : rsiList.get(japaneseCandleList.size() - 1);;
			entryStrategyManager.setNewData(newUpdateData);
			
			if (entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BEARISH || 
					entryStrategyManager.getStatus() == EntryStrategyManagerStatus.TRIGGER_BULLISH) {
				notifyObservers(assetType, dataEventType, parameters);
			}
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
