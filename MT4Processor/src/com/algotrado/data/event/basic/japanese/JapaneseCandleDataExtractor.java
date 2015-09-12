package com.algotrado.data.event.basic.japanese;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.util.DebugUtil;

public class JapaneseCandleDataExtractor extends IDataExtractorSubject implements IDataExtractorObserver {
	
	private final DataEventType dataEventType = DataEventType.JAPANESE;
	private JapaneseTimeFrameType timeFrameType;
	private int historyLength;
//	private List<JapaneseCandleBar> dataList; // TODO - record history
	private JapaneseCandleBar newData = null;
	private Date openTime = null;
	private double open;
	private double close;
	private double high;
	private double low;
	private double volume;
	
	public JapaneseCandleDataExtractor(DataSource dataSource, AssetType assetType, DataEventType dataEventType, List<Double> parameters) {
		super(dataSource, assetType, dataEventType, parameters);
		open = -1;
		dataExtractorSubjectArr = new IDataExtractorSubject[1];
		if(timeFrameType != JapaneseTimeFrameType.ONE_MINUTE)
		{
			// Change parameters to lower interval
			int timeFrameOrder = timeFrameType.ordinal();
			JapaneseTimeFrameType lowerTimeFrame = JapaneseTimeFrameType.values()[timeFrameOrder - 1];
			
			List<Double> lowerTimeFrameParams = new ArrayList<Double>();
			lowerTimeFrameParams.add((double)lowerTimeFrame.getValueInMinutes());
			int NexthistoryLength = historyLength * (timeFrameType.getValueInMinutes()/lowerTimeFrame.getValueInMinutes());
			
			//tempTime = tempTime.ordinal();
			RegisterDataExtractor.register(this.dataSource, assetType, dataEventType, lowerTimeFrameParams,NexthistoryLength, this);
		}
		else
		{
			List<Double> minimalTimeFrameParams = new ArrayList<Double>();
			RegisterDataExtractor.register(this.dataSource, assetType, DataEventType.NEW_QUOTE, minimalTimeFrameParams,0, this);
		}
		
	}

	/**
	 * Use this method to pass the subject to the observer, so the observer can get the new data updates.
	 * Another way to change the way this method works is to send the updates through the notifyObserver method.
	 * @param dataExtractorSubject
	 */
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		dataExtractorSubjectArr[0] = dataExtractorSubject;
	}
	
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		dataExtractorSubjectArr[0] = null;
	}

	@Override
	public NewUpdateData getNewData() {
		return newData;
	}

	public JapaneseTimeFrameType getTimeFrameType() {
		return timeFrameType;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Double> parameters) {
		int subjectTimeFrameInterval;
		JapaneseCandleBar subjectCandle;
		if(dataEventType == DataEventType.JAPANESE)
		{
			subjectTimeFrameInterval = parameters.get(0).intValue();
			subjectCandle = (JapaneseCandleBar)this.dataExtractorSubjectArr[0].getNewData();
		}
		else // dataEventType == DataEventType.NEW_QUOTE 
		{
			// TODO - need support in case the minimum time frame is not JapaneseCandleBar (not file)
			subjectTimeFrameInterval = 0;
			SimpleUpdateData simpleUpdateData = (SimpleUpdateData)this.dataExtractorSubjectArr[0].getNewData();
			subjectCandle = new JapaneseCandleBar(simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getValue(), simpleUpdateData.getVolume(), simpleUpdateData.getTime(), simpleUpdateData.getAssetName());
		}
		
		if (DebugUtil.debugDataExtractor && (timeFrameType.getValueInMinutes() < subjectTimeFrameInterval)) {
			throw new RuntimeException("Cannot use larger time frame " + subjectTimeFrameInterval + 
					" to get lower time frame " + timeFrameType.getValueInMinutes());
		}

		if (open != -1) {
			if(timeFrameType.isTimeFrameStartTime(subjectCandle.getTime(), openTime))
			{
				newData = new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName());
				notifyObservers(this.assetType, this.dataEventType, this.parameters);
				open = -1;
			}
		}
	
		//Take the candle bar from subject and create new candle bar in larger time frame.
		boolean timeFrameEndTime = (subjectTimeFrameInterval != 0) ? timeFrameType.isTimeFrameEndTime(subjectCandle.getTime(), subjectTimeFrameInterval) : false;
		close = subjectCandle.getClose();
		if (open == -1) {
			open = subjectCandle.getOpen();
			high = subjectCandle.getHigh();
			low = subjectCandle.getLow();
			volume = subjectCandle.getVolume();
			openTime = timeFrameType.getRoundDate(subjectCandle.getTime());
		} else {
			volume += subjectCandle.getVolume();
			if (subjectCandle.getHigh() > high) {
				high = subjectCandle.getHigh();
			}
			if (subjectCandle.getLow() < low) {
				low = subjectCandle.getLow();
			}
		}
		
		if (timeFrameEndTime || (this.dataExtractorSubjectArr[0].getSubjectState() != SubjectState.RUNNING)) {
			newData = new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName());
			notifyObservers(this.assetType, this.dataEventType, this.parameters);
			open = -1;
		}
	}
	
	@Override
	public String getDataHeaders() {
		JapaneseCandleBar temp = new JapaneseCandleBar(0,0,0,0,0,null,null);
		return "Asset," + assetType.name() + "\n" +
				"Interval," + JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + this.dataSource.toString() + "\n" + 
				temp.getDataHeaders();
		
	}
	
	@Override
	public String toString() {
		return newData.toString(); 
	}

	@Override
	public SubjectState getSubjectState() {
		return dataExtractorSubjectArr[0].getSubjectState();
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		timeFrameType = JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0));
//		historyLength = parameters.get(1).intValue();		
	}
	
//	@Override
//	public void unregisterObserver(IDataExtractorObserver observer) {
//		this.observers.remove(observer);
//		observer.removeSubject(this);
//		if (this.observers.isEmpty()) {
//			dataExtractorSubjectArr[0].unregisterObserver(this);
//			RegisterDataExtractor.removeDataExtractorSubject(dataSource, assetType, dataEventType, parameters);
//		}
//	}
}
