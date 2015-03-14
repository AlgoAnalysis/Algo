package com.algotrado.data.event.basic.japanese;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
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
	private List<JapaneseCandleBar> dataList;
	private JapaneseCandleBar newData = null;
	private IDataExtractorSubject dataExtractorSubject;
	private Date openTime = null;
	private double open;
	private double close;
	private double high;
	private double low;
	private double volume;
	
	public JapaneseCandleDataExtractor(DataSource dataSource, AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		super(dataSource, assetType, dataEventType, parameters);
		open = -1;
		if(timeFrameType != JapaneseTimeFrameType.ONE_MINUTE)
		{
			// Change parameters to lower interval
			int timeFrameOrder = timeFrameType.ordinal();
			JapaneseTimeFrameType lowerTimeFrame = JapaneseTimeFrameType.values()[timeFrameOrder - 1];
			
			List<Float> lowerTimeFrameParams = new ArrayList<Float>();
			lowerTimeFrameParams.add(Integer.valueOf(lowerTimeFrame.getValueInMinutes()).floatValue());
			lowerTimeFrameParams.add(parameters.get(1) * (parameters.get(0)/lowerTimeFrame.getValueInMinutes()));
			
			//tempTime = tempTime.ordinal();
			RegisterDataExtractor.register(this.dataSource, assetType, dataEventType, lowerTimeFrameParams, this);
		}
		else
		{
			List<Float> minimalTimeFrameParams = new ArrayList<Float>();
			RegisterDataExtractor.register(this.dataSource, assetType, DataEventType.MINIMAL_TIME_FRAME, minimalTimeFrameParams, this);
		}
		
	}

	/**
	 * Use this method to pass the subject to the observer, so the observer can get the new data updates.
	 * Another way to change the way this method works is to send the updates through the notifyObserver method.
	 * @param dataExtractorSubject
	 */
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;
	}
	
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = null;
	}

	@Override
	public NewUpdateData getNewData() {
		return newData;
	}

	public JapaneseTimeFrameType getTimeFrameType() {
		return timeFrameType;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Float> parameters) {
		int subjectTimeFrameInterval;
		JapaneseCandleBar subjectCandle;
		if(dataEventType == DataEventType.JAPANESE)
		{
			subjectTimeFrameInterval = parameters.get(0).intValue();
			subjectCandle = (JapaneseCandleBar)this.dataExtractorSubject.getNewData();
		}
		else // dataEventType == DataEventType.MINIMAL_TIME_FRAME 
		{
			// TODO - need support in case the minimum time frame is not JapaneseCandleBar (not file)
			subjectTimeFrameInterval = JapaneseTimeFrameType.ONE_MINUTE.getValueInMinutes();
			subjectCandle = (JapaneseCandleBar)this.dataExtractorSubject.getNewData();			
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
		boolean timeFrameEndTime = timeFrameType.isTimeFrameEndTime(subjectCandle.getTime(), subjectTimeFrameInterval);
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
			} else if (subjectCandle.getLow() < low) {
				low = subjectCandle.getLow();
			}
		}
		
		if (timeFrameEndTime || (this.dataExtractorSubject.getSubjectState() != SubjectState.RUNNING)) {
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
		return dataExtractorSubject.getSubjectState();
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Float> parameters) {
		timeFrameType = JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0));
		historyLength = parameters.get(1).intValue();		
	}
}
