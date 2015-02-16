package com.algotrado.extract.data;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;
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
	
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = null;
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
		
		if (subjectTimeFrame.isLargerTimeFrame(timeFrameType)) {
			throw new RuntimeException("Cannot use larger time frame " + subjectTimeFrame.getValueInMinutes() + 
					" to get lower time frame " + timeFrameType.getValueInMinutes());
		}
		
		//Take all the candle bars from subject and create new candle bars in larger time frame. 
		
		double open=-1, close=-1, high=-1, low=-1, volume=-1;
		Date openTime = null;
		for (JapaneseCandleBar candleBarData : subjectCandleBars) {
			if (open < 0) {
				open = candleBarData.getOpen();
//				close = candleBarData.getClose();
				high = candleBarData.getHigh();
				low = candleBarData.getLow();
				openTime = candleBarData.getTime();
			}
			
			if (timeFrameType.isTimeFrameStartTime(candleBarData.getTime())) {
				open = candleBarData.getOpen();
				openTime = candleBarData.getTime();
				high = candleBarData.getHigh();
				low = candleBarData.getLow();
			} else if (timeFrameType.isTimeFrameEndTime(candleBarData.getTime())) {
				close = candleBarData.getClose();
				if (candleBarData.getHigh() > high) {
					high = candleBarData.getHigh();
				} else if (candleBarData.getLow() < low) {
					low = candleBarData.getLow();
				}
				dataList.addCandleBar(new SingleCandleBarData(open, close, high, low, openTime, candleBarData.getCommodityName(), 0, 
																0, 0, 0, 0, 0, 0));
			} else {
				if (candleBarData.getHigh() > high) {
					high = candleBarData.getHigh();
				} else if (candleBarData.getLow() < low) {
					low = candleBarData.getLow();
				}
			}
			
		}
		
		run();
	}
}
