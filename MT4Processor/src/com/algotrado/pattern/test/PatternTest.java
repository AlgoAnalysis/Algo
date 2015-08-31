package com.algotrado.pattern.test;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.APatternState;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.pattern.PTN_0003.PTN_0003_S1;

public class PatternTest extends IDataExtractorSubject implements IGUIController , IDataExtractorObserver, Runnable {

	private long timeMili;
	IDataExtractorSubject dataExtractorSubject;
	IDataExtractorObserver dataRecorder;
	SubjectState subjectState;
	PatternManager patternManager;
	private static DataSource dataSource = DataSource.FILE;
	public PatternTest()
	{
		super(dataSource, AssetType.USOIL,DataEventType.JAPANESE,(List<Double>)(new ArrayList<Double>()));
		timeMili = System.currentTimeMillis();
		APatternState state = new PTN_0003_S1(1);
		patternManager = new PatternManager((IPatternFirstState)state);
		String filePath = "C:\\Algo\\test\\" + state.getCode() + ".csv";
		japaneseParameters.add((double) 5);
		//RegisterDataExtractor.setDataSource(dataSource);
		dataRecorder = new FileDataRecorder(filePath, this);
		dataRecorder.setSubject(this);
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
		Double deffTime = Double.valueOf((double)(System.currentTimeMillis() - timeMili)/1000);
		System.out.println(deffTime.toString() + " Sec");
		unregisterObserver(this);
	}
	@Override
	public void run() {
		RegisterDataExtractor.register(dataSource, assetType, dataEventType, japaneseParameters,0, this);

	}
	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Double> parameters) {
		subjectState = dataExtractorSubject.getSubjectState();
		NewUpdateData[] newData = {dataExtractorSubject.getNewData()};
		patternManager.setNewData(newData);
		if((patternManager.getStatus() == PatternManagerStatus.TRIGGER_BEARISH) || 
				(patternManager.getStatus() == PatternManagerStatus.TRIGGER_BULLISH) ||
				(patternManager.getStatus() == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) ||
				(subjectState == SubjectState.END_OF_LIFE))
		{
			dataRecorder.notifyObserver(this.dataEventType, this.japaneseParameters);
		}
	}
	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;

	}
	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		setSubject(null);

	}

	@Override
	public String toString() {
		return patternManager.toString(); 
	}


	@Override
	public NewUpdateData getNewData() {
		// not implement because the file recorder not need this.
		return null;
	}


	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + JapaneseTimeFrameType.getTimeFrameFromInterval(japaneseParameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.toString() + "\n" + 
				patternManager.getDataHeaders();

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
		// TODO Auto-generated method stub
		
	}
	
	
	static PatternTest test;
	public static void main(String[] args) throws InterruptedException {
		test = new PatternTest();
		SwingUtilities.invokeLater((PatternTest)test);
		Thread.sleep(3500);
		for(int cnt =0;cnt<20;cnt++)
		{
			Thread.sleep(500);
			RegisterDataExtractor.resetRegisterDataExtractor();
			test = new PatternTest();
			SwingUtilities.invokeLater((PatternTest)test);			
		}
	}
	
}
