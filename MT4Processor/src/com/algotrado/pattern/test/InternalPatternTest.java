package com.algotrado.pattern.test;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.CandleBarsCollection;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.TimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;

public class InternalPatternTest extends IDataExtractorSubject implements IGUIController , IDataExtractorObserver, Runnable {

	private long timeMili;
	IDataExtractorSubject dataExtractorSubject;
	IDataExtractorObserver dataRecorder;
	SubjectState subjectState;
	PatternManager patternManager;
	public InternalPatternTest()
	{
		super(AssetType.USOIL,DataEventType.JAPANESE,(List<Float>)(new ArrayList<Float>()));
		DataSource dataSource = DataSource.FILE;
		timeMili = System.currentTimeMillis();
		PTN_0001_S1 state = new PTN_0001_S1(1);
		String filePath = "C:\\Algo\\test\\" + state.getCode() + ".csv";
		parameters.add((float) 5);
		parameters.add((float) 0); // TODO - check if we want history
		RegisterDataExtractor.setDataSource(dataSource);
		dataRecorder = new FileDataRecorder(filePath, this);
		dataRecorder.setSubject(this);
		subjectState = SubjectState.RUNNING;
		patternManager = new PatternManager(state);
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
		Float deffTime = Float.valueOf((float)(System.currentTimeMillis() - timeMili)/1000);
		System.out.println(deffTime.toString() + " Sec");
	}
	@Override
	public void run() {
		RegisterDataExtractor.register(assetType, dataEventType, parameters, this);

	}
	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		subjectState = dataExtractorSubject.getSubjectState();
		CandleBarsCollection newDataCollection = (CandleBarsCollection) dataExtractorSubject.getNewData();
		NewUpdateData[] newData = {newDataCollection.getCandleBars().get(0)};
		patternManager.setNewData(newData);
		if((patternManager.getStatus() == PatternManagerStatus.TRIGGER_BEARISH) || 
				(patternManager.getStatus() == PatternManagerStatus.TRIGGER_BULLISH) ||
				(patternManager.getStatus() == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) ||
				(subjectState == SubjectState.END_OF_LIFE))
		{
			dataRecorder.notifyObserver(this.dataEventType, this.parameters);
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
				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.getValueString() + "\n" + 
				patternManager.getDataHeaders();

	}


	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}
}
