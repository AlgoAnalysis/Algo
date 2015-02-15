package com.algotrado.extract.data;

import java.util.Collection;
import java.util.List;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class LargerTimeFrameDataExtractor extends IDataExtractorSubject implements IDataExtractorObserver {
	
	private TimeFrameType timeFrameType;
	private CandleBarsCollection dataList;
	private IDataExtractorSubject dataExtractorSubject;

	public LargerTimeFrameDataExtractor(AssetType assetType,
			DataEventType dataEventType, List<Float> parameters) {
		super(assetType, dataEventType, parameters);
		this.timeFrameType = setTimeFarmeType(parameters);
	}

	public TimeFrameType setTimeFarmeType(List<Float> parameters) {
		for (TimeFrameType currTimeFrameType : TimeFrameType.values()) {
			if (currTimeFrameType.getValueInMinutes() == parameters.get(0)) {
				return currTimeFrameType;
			}
		}
		return null;
	}

	/**
	 * Use this method to pass the subject to the observer, so the observer can get the new data updates.
	 * Another way to change the way this method works is to send the updates through the notifyObserver method.
	 * @param dataExtractorSubject
	 */
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;
	}

	@Override
	public void run() {
		notifyObservers(assetType, dataEventType, parameters);
	}

	@Override
	public NewUpdateData getNewData() {
		return dataList;
	}

	public TimeFrameType getTimeFrameType() {
		return timeFrameType;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Float> parameters) {
		dataList = new CandleBarsCollection();
		TimeFrameType subjectTimeFrame = setTimeFarmeType(parameters);
		CandleBarsCollection subjctDataList = (CandleBarsCollection)this.dataExtractorSubject.getNewData();
		Collection<SingleCandleBarData> subjectCandleBars = subjctDataList.getCandleBars();
		
		//Take all the candle bars from subject and create new candle bars in larger time frame. 
		
		run();
	}
}
