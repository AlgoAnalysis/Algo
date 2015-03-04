package com.algotrado.extract.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.algotrado.data.event.CandleBarsCollection;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.TimeFrameType;
import com.algotrado.util.Setting;

public class LargerTimeFrameDataExtractor extends IDataExtractorSubject implements IDataExtractorObserver, Comparable<LargerTimeFrameDataExtractor> {
	
	private TimeFrameType timeFrameType;
	private CandleBarsCollection dataList;
	private IDataExtractorSubject dataExtractorSubject;
	private Date previousDate = null;
	private Date openTime = null;
	private double open=-1;
	private double close=-1;
	private double high=-1;
	private double low=-1;
	private double volume=-1;
	private SubjectState subjectState;
	
	
	// This varible was created to check if now is a new timeframe in regards to previous time. This was made due to data inconsistencies.
	private Date prevCandleDate = null;
	
	public LargerTimeFrameDataExtractor(AssetType assetType,
			DataEventType dataEventType, List<Float> parameters) {
		super(assetType, dataEventType, parameters);
		this.timeFrameType = setTimeFarmeType(parameters);
		this.subjectState = SubjectState.RUNNING;
		
		// Change parameters to lower interval
		int timeFrameOrder = TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).ordinal();
		TimeFrameType lowerTimeFrame = TimeFrameType.values()[timeFrameOrder - 1];
		
		List<Float> lowerTimeFrameParams = new ArrayList<Float>();
		lowerTimeFrameParams.add(Integer.valueOf(lowerTimeFrame.getValueInMinutes()).floatValue());
		lowerTimeFrameParams.add(parameters.get(1) * (parameters.get(0)/lowerTimeFrame.getValueInMinutes()));
		
		//tempTime = tempTime.ordinal();
		RegisterDataExtractor.register(assetType, dataEventType, lowerTimeFrameParams, this);
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
		
	}

	@Override
	public NewUpdateData getNewData() {
		return dataList/*.getCandleBars().get(dataList.getCandleBars().size() - 1)*/;
	}

	public TimeFrameType getTimeFrameType() {
		return timeFrameType;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Float> parameters) {
		dataList = new CandleBarsCollection();
		TimeFrameType subjectTimeFrame = setTimeFarmeType(parameters);
		
		CandleBarsCollection subjectCandles = (CandleBarsCollection)this.dataExtractorSubject.getNewData();
		
		for (Iterator<JapaneseCandleBar> jpnCandleIterator = subjectCandles.getCandleBars().iterator(); jpnCandleIterator.hasNext(); ) {
			JapaneseCandleBar subjectCandle = jpnCandleIterator.next();
			
			if (subjectTimeFrame.isLargerTimeFrame(timeFrameType)) {
				throw new RuntimeException("Cannot use larger time frame " + subjectTimeFrame.getValueInMinutes() + 
						" to get lower time frame " + timeFrameType.getValueInMinutes());
			}


			boolean isNewTimeFrame = false;
			if (previousDate != null && openTime != null &&  
					!timeFrameType.isTimeFrameEndTime(prevCandleDate, subjectTimeFrame.getValueInMinutes())) {//Support new candle when a new time frame starts. 
				if (timeFrameType.isTimeFrameStartTime(subjectCandle.getTime(), prevCandleDate)) {
					isNewTimeFrame = true;
					dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName()));
					previousDate = openTime;
				}
			}
			close = subjectCandle.getClose();
			//Take the candle bar from subject and create new candle bar in larger time frame.
			boolean timeFrameEndTime = timeFrameType.isTimeFrameEndTime(subjectCandle.getTime(), subjectTimeFrame.getValueInMinutes());
			if ((open < 0) || timeFrameType.isTimeFrameStartTime(subjectCandle.getTime(), prevCandleDate) || isNewTimeFrame) {
				open = subjectCandle.getOpen();
				high = subjectCandle.getHigh();
				low = subjectCandle.getLow();
				openTime = subjectCandle.getTime();
				volume = subjectCandle.getVolume();
				if (timeFrameEndTime) {
					dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName()));
					previousDate = openTime;
				}
			} else if (timeFrameEndTime) {
				volume += subjectCandle.getVolume();
				if (subjectCandle.getHigh() > high) {
					high = subjectCandle.getHigh();
				} else if (subjectCandle.getLow() < low) {
					low = subjectCandle.getLow();
				}
				dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName()));
				previousDate = openTime;
			} else {
				volume += subjectCandle.getVolume();
				if (subjectCandle.getHigh() > high) {
					high = subjectCandle.getHigh();
				} else if (subjectCandle.getLow() < low) {
					low = subjectCandle.getLow();
				}
			}

			prevCandleDate = subjectCandle.getTime();

			if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE && !jpnCandleIterator.hasNext()) {
				this.subjectState = SubjectState.END_OF_LIFE;
				if (!timeFrameEndTime) {
					dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName()));
				}
				this.dataExtractorSubject.unregisterObserver(this);

			}
			if (!this.dataList.getCandleBars().isEmpty()) {
				notifyObservers(this.assetType, this.dataEventType, this.parameters);
			}
		}
	}

	public boolean addNewTimeFrameCandleBarToDataList(
			JapaneseCandleBar subjectCandle) {
		boolean isNewTimeFrame;
		isNewTimeFrame = true;
		dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getAssetName()));
		previousDate = openTime;
		return isNewTimeFrame;
	}
	
	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.getValueString() + "\n" + 
				Setting.getDateTimeHeder("") + "," + getNewData().getDataHeaders();
	}
	
	@Override
	public String toString() {
		String toStringRet = null;
		for (Iterator<JapaneseCandleBar> jpnCandleIterator = dataList.getCandleBars().iterator(); jpnCandleIterator.hasNext(); ) {
			if (toStringRet == null) {
				toStringRet = "";
			}
			JapaneseCandleBar candle = jpnCandleIterator.next();
			SimpleDateFormat dateformatter = new SimpleDateFormat(Setting.getDateTimeFormat());
			toStringRet += dateformatter.format(candle.getTime()) + " , " + candle.getOpen() + " , " + candle.getHigh() + " , " 
			+ candle.getLow() + " , " + candle.getClose() + " , " + candle.getVolume() + ((!jpnCandleIterator.hasNext()) ? "" : "\n");
		}
		return toStringRet; 
	}

	@Override
	public SubjectState getSubjectState() {
		return this.subjectState;
	}

	@Override
	public int compareTo(LargerTimeFrameDataExtractor o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		} else {
			if (o.timeFrameType == this.timeFrameType && o.assetType == this.assetType && o.dataEventType == this.dataEventType) {
				if (o.parameters != null && this.parameters != null) {
					if (o.parameters.size() != this.parameters.size()) {
						return this.parameters.size() - o.parameters.size();
					}
					Iterator<Float> fileDataRecorderIterator = this.parameters.iterator();
					for (Iterator<Float> oIterator = o.parameters.iterator(); oIterator.hasNext() && fileDataRecorderIterator.hasNext();) {
						Float oParam = oIterator.next();
						Float fileDataRecorderParam = fileDataRecorderIterator.next();
						if (oParam != fileDataRecorderParam) {
							return new Float(fileDataRecorderParam - oParam).intValue();
						}
						
					}
				} else if (o.parameters == null) {
					return 1;
				}
			}
		}
		return -1;
	}
}
