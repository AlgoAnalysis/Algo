package com.algotrado.data.event.indicator.IND_0001;

import java.util.List;

import com.algotrado.data.event.CandleBarsCollection;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;

public class IND_0001 extends IDataExtractorSubject implements
		IDataExtractorObserver {
	
	IDataExtractorSubject dataExtractorSubject;
	SubjectState subjectState;
	JapaneseCandleBarPropertyType japaneseCandleBarPropertyType;
	double gainLossValues[];
	int movingIndex;
	int length;
	double sumGain;
	double sumLoss;
	double preInputValue;
	IND_0001_NewUpdateDate newUpdateDate;
	
	public IND_0001(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Float> parameters) {
		super(dataSource, assetType, dataEventType, parameters);
		japaneseCandleBarPropertyType = JapaneseCandleBarPropertyType.getJapaneseCandleBarPropertyType(parameters.get(1));
		length = parameters.get(2).intValue();
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
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		JapaneseCandleBar japaneseCandleBar;
		CandleBarsCollection newDataCollection = (CandleBarsCollection) dataExtractorSubject.getNewData();
		japaneseCandleBar = newDataCollection.getCandleBars().get(0);
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

}
