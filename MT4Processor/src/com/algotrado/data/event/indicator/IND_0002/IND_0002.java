package com.algotrado.data.event.indicator.IND_0002;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateDate;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;

public class IND_0002 extends IDataExtractorSubject implements
		IDataExtractorObserver {

	public static void main(String[] args) {
		//////// change hare ///////////////////
		DataSource dataSource = DataSource.FILE;
		AssetType assetType = AssetType.USOIL;
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		JapaneseCandleBarPropertyType japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.CLOSE;
		int rsiLength = 7;
		int rsiHistoryLength = 0;
		String filePath = "C:\\Algo\\test\\RSI2_on_" + assetType.name()+"_in_len"+ rsiLength + ".csv";
		///////////////////////////////////////
		IDataExtractorObserver dataRecorder;
		List<Float> parameters = new ArrayList<Float>();
		
		parameters.add((float)japaneseTimeFrameType.getValueInMinutes());
		parameters.add((float)japaneseCandleBarPropertyType.ordinal());
		parameters.add((float)rsiLength);
		parameters.add((float)rsiHistoryLength);
		parameters.add((float)2); // RSI type
		dataRecorder = new FileDataRecorder(filePath, null);
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.RSI, parameters, dataRecorder);	
	}
	
	private final DataEventType dataEventType = DataEventType.RSI;
	private IDataExtractorSubject dataExtractorSubject;
	private JapaneseCandleBarPropertyType japaneseCandleBarPropertyType;
	private int movingIndex;
	private int length;
	private int historyLength;
	private double avrGain; 
	private double avrLoss;
	private double preValue;
	private double inputFactor;
	private double avrFactor;
	private SimpleUpdateDate newUpdateDate;
	private Float japaneseCandleInterval;
	
	
	public IND_0002(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Float> parameters) {
		super(dataSource, assetType, dataEventType, parameters);
		movingIndex = 0;
		avrGain = 0;
		avrLoss = 0;
		preValue = 0;
		inputFactor = 1/(double)length;
		avrFactor = 1 - inputFactor;
		List<Float> japaneseParameters = new ArrayList<Float>();
		japaneseParameters.add(japaneseCandleInterval);
		japaneseParameters.add((float)(historyLength + length));
		RegisterDataExtractor.register(dataSource,assetType,DataEventType.JAPANESE,japaneseParameters,this);
	}
	
	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar) dataExtractorSubject.getNewData();
		double inputValue = japaneseCandleBarPropertyType.getJapaneseCandleBarValue(japaneseCandleBar);
		double rsiValue;
		double gain = (inputValue > preValue) ? (inputValue - preValue) : 0;
		double loss = (inputValue < preValue) ? (preValue - inputValue) : 0;
		if(movingIndex >  length)
		{
			avrGain = avrGain*avrFactor + gain*inputFactor;
			avrLoss = avrLoss*avrFactor + loss*inputFactor;
			rsiValue = 100*avrGain/(avrGain + avrLoss);
		}
		else
		{
			rsiValue = 50;
			if(movingIndex != 0)
			{
				avrGain += gain*inputFactor;
				avrLoss += loss*inputFactor;
			}
			if(movingIndex ==  length)
			{
				rsiValue = 100*avrGain/(avrGain + avrLoss);
			}
			movingIndex++;
		}
		preValue = inputValue;
		newUpdateDate = new SimpleUpdateDate(this.assetType,japaneseCandleBar.getTime(),rsiValue);
		notifyObservers(this.assetType, this.dataEventType, this.parameters);
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
	public NewUpdateData getNewData() {
		return newUpdateDate;
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Float> parameters) {
		japaneseCandleInterval = parameters.get(0);
		japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.getJapaneseCandleBarPropertyType(parameters.get(1));
		length = parameters.get(2).intValue();
		historyLength = parameters.get(3).intValue();
	}

	@Override
	public String getDataHeaders() {
		SimpleUpdateDate temp = new SimpleUpdateDate(null,null,0);
		return "Asset," + assetType.name() + "\n" +
				"Interval," + japaneseCandleInterval+ "\n" + 
				"Data Source," + this.dataSource.toString() + "\n" +
				"RSI type,2\n" +
				"Length," + length + "\n" +
				temp.getDataHeaders();
	}
	
	@Override
	public String toString() {
		return newUpdateDate.toString(); 
	}

	@Override
	public SubjectState getSubjectState() {
		return dataExtractorSubject.getSubjectState();
	}

}
