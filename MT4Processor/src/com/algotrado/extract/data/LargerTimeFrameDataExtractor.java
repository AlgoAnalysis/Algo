package com.algotrado.extract.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.output.file.FileDataRecorder;

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
		notifyObservers(assetType, dataEventType, parameters);
	}

	@Override
	public NewUpdateData getNewData() {
		return dataList.getCandleBars().get(dataList.getCandleBars().size() - 1);
	}

	public TimeFrameType getTimeFrameType() {
		return timeFrameType;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType, List<Float> parameters) {
		dataList = new CandleBarsCollection();
		TimeFrameType subjectTimeFrame = setTimeFarmeType(parameters);
		JapaneseCandleBar subjectCandle = (JapaneseCandleBar)this.dataExtractorSubject.getNewData();
		
		if (subjectTimeFrame.isLargerTimeFrame(timeFrameType)) {
			throw new RuntimeException("Cannot use larger time frame " + subjectTimeFrame.getValueInMinutes() + 
					" to get lower time frame " + timeFrameType.getValueInMinutes());
		}
		
 
		boolean isNewWeek = false;
		if (previousDate != null && openTime != null) {//Support new candle when a new week starts. 
			Calendar prevCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			prevCalendar.setTime(previousDate);
			Calendar currCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			currCalendar.setTime(subjectCandle.getTime());
			if (prevCalendar.get(Calendar.WEEK_OF_YEAR) != currCalendar.get(Calendar.WEEK_OF_YEAR)) {
				isNewWeek = true;
				if (!timeFrameType.isTimeFrameEndTime(previousDate)) {
					dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getCommodityName()));
					previousDate = openTime;
				}
			}
		}
		close = subjectCandle.getClose();
		//Take the candle bar from subject and create new candle bar in larger time frame.
		boolean timeFrameEndTime = timeFrameType.isTimeFrameEndTime(subjectCandle.getTime());
		if ((open < 0) || timeFrameType.isTimeFrameStartTime(subjectCandle.getTime()) || isNewWeek) {
			open = subjectCandle.getOpen();
			high = subjectCandle.getHigh();
			low = subjectCandle.getLow();
			openTime = subjectCandle.getTime();
			volume = subjectCandle.getVolume();
			
		} else if (timeFrameEndTime) {
			volume += subjectCandle.getVolume();
			if (subjectCandle.getHigh() > high) {
				high = subjectCandle.getHigh();
			} else if (subjectCandle.getLow() < low) {
				low = subjectCandle.getLow();
			}
			dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getCommodityName()));
			previousDate = openTime;
		} else {
			volume += subjectCandle.getVolume();
			if (subjectCandle.getHigh() > high) {
				high = subjectCandle.getHigh();
			} else if (subjectCandle.getLow() < low) {
				low = subjectCandle.getLow();
			}
		}
		
		if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE) {
			this.subjectState = SubjectState.END_OF_LIFE;
			if (!timeFrameEndTime) {
				dataList.addCandleBar(new JapaneseCandleBar(open, close, high, low, volume, openTime, subjectCandle.getCommodityName()));
			}
			this.dataExtractorSubject.unregisterObserver(this);
			
		}
		run();
	}
	
	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.getValueString() + "\n" + 
				"Date and Time, Open Price, High Price, Low Price, Close Price, Volume";
	}
	
	@Override
	public String toString() {
		JapaneseCandleBar candle = dataList.getCandleBars().get(dataList.getCandleBars().size() - 1);
		return candle.getTime() + " , " + candle.getOpen() + " , " + candle.getHigh() + " , " 
				+ candle.getLow() + " , " + candle.getClose();
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
