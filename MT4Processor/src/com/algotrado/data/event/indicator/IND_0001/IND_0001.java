package com.algotrado.data.event.indicator.IND_0001;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;

public class IND_0001 extends IDataExtractorSubject implements
		IDataExtractorObserver {
	private final DataEventType dataEventType = DataEventType.RSI;
	private IDataExtractorSubject dataExtractorSubject;
	private SubjectState subjectState;
	private JapaneseCandleBarPropertyType japaneseCandleBarPropertyType;
	private double gainLossValues[];
	private int movingIndex;
	private int length;
	private int historyLength;
	private double sumGain;
	private double sumLoss;
	private double preInputValue;
	private IND_0001_NewUpdateDate newUpdateDate;
	private Float japaneseCandleInterval;
	
	public IND_0001(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Float> parameters) {
		super(dataSource, assetType, dataEventType, parameters);

		gainLossValues = new double[length];
		movingIndex = 0;
		for(int index =0;index < length;index++)
		{
			gainLossValues[index] =0;
		}
		sumGain = 0;
		sumLoss = 0;
		preInputValue = 0;
		subjectState = SubjectState.RUNNING;
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
		if(gainLossValues[movingIndex] >= 0)
		{
			sumGain -= gainLossValues[movingIndex];
		}
		else
		{
			sumLoss += gainLossValues[movingIndex];
		}
		gainLossValues[movingIndex] = inputValue - preInputValue;
		if(gainLossValues[movingIndex] >= 0)
		{
			sumGain += gainLossValues[movingIndex];
		}
		else
		{
			sumLoss -= gainLossValues[movingIndex];
		}
		double rsiValue;
		if((sumGain == 0) && (sumLoss == 0))
		{
			rsiValue = 0.5;
		}
		else
		{
			rsiValue = 100*sumGain/(sumGain + sumLoss);
		}
		newUpdateDate = new IND_0001_NewUpdateDate(this.assetType,japaneseCandleBar.getTime(),rsiValue);
		preInputValue = inputValue;
		movingIndex = (movingIndex+1)%length;
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
	public String getDataHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
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

}
