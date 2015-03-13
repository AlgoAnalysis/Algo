package com.algotrado.extract.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import com.algotrado.data.event.CandleBarsCollection;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.TimeFrameType;

public class RamDataExtractor extends IDataExtractorSubject implements IDataExtractorObserver {
	
	private List<JapaneseCandleBar> candles = null;
	private IDataExtractorSubject dataExtractorSubject;
	private CandleBarsCollection dataList = null;
	private JapaneseCandleBar japaneseCandleBarNewData;
	private SubjectState subjectState = null;

	public RamDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Float> parameters) {
		super(DataSource.RAM,assetType, dataEventType, parameters);
		subjectState = SubjectState.RUNNING;
		candles = new ArrayList<JapaneseCandleBar>();
//		int timeFrameOrder = TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).ordinal();
//		if (timeFrameOrder == 0) {
		RegisterDataExtractor.register(DataSource.FILE, assetType, dataEventType, parameters, this);
//		} else {
//			RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters, this);
//		}
	}

	@Override
	public void run() {
		subjectState = SubjectState.RUNNING;
		dataList = new CandleBarsCollection();
		for(Iterator<JapaneseCandleBar> japaneseCandlesIter = candles.iterator() ; japaneseCandlesIter.hasNext();) {
			JapaneseCandleBar candle = japaneseCandlesIter.next();
			dataList.addCandleBar(candle);
			if (!japaneseCandlesIter.hasNext()) {
				subjectState = SubjectState.END_OF_LIFE;
			}
			for(JapaneseCandleBar bar : dataList.getCandleBars()) {
				japaneseCandleBarNewData = bar;
				notifyObservers(assetType, dataEventType, parameters);
			}
			dataList.getCandleBars().clear();
		}
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		candles.addAll(((CandleBarsCollection)this.dataExtractorSubject.getNewData()).getCandleBars());
		if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE) {
			SwingUtilities.invokeLater(this);
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
	public NewUpdateData getNewData() {
		return japaneseCandleBarNewData;
	}

	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.RAM.toString() + "\n" + 
				"Date,Time, " + getNewData().getDataHeaders();
	}
	
	@Override
	public String toString() {
		String toStringRet = null;
		for (Iterator<JapaneseCandleBar> jpnCandleIterator = dataList.getCandleBars().iterator(); jpnCandleIterator.hasNext(); ) {
			if (toStringRet == null) {
				toStringRet = "";
			}
			JapaneseCandleBar candle = jpnCandleIterator.next();
			SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
			toStringRet += dateformatter.format(candle.getTime()) + " , " + candle.getOpen() + " , " + candle.getHigh() + " , " 
			+ candle.getLow() + " , " + candle.getClose() + " , " + candle.getVolume() + ((!jpnCandleIterator.hasNext()) ? "" : "\n");
		}
		return toStringRet; 
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}
	
	@Override
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
		observer.removeSubject(this);
	}
	
	@Override
	public IDataExtractorSubject registerObserver(IDataExtractorObserver observer ) {
		boolean shouldRunSubject = !this.candles.isEmpty() && this.observers.isEmpty();
		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
			observer.setSubject(this);
		}
		
		if (shouldRunSubject) {//Do not run the Ram on first time after registering observer.
			SwingUtilities.invokeLater(this);
		}
		
		return this;
	}
	
	public static IDataExtractorSubject getSubjectDataExtractor(final AssetType assetType,DataEventType dataEventType,final List<Float> parameters){
		return new RamDataExtractor(assetType, dataEventType, parameters);
	}
}
