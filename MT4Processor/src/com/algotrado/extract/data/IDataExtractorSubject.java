package com.algotrado.extract.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.SwingUtilities;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;

/**
 * This is the abstract class for data extractor.
 * All Subclasses must implement run() method that should do the data extraction functionality.
 * @author Ariel
 *
 */
public abstract class IDataExtractorSubject implements Runnable, Comparable<IDataExtractorSubject>{
	
	protected Collection<IDataExtractorObserver> observers;
	protected DataSource dataSource;
	protected AssetType assetType; 
	protected DataEventType dataEventType;
	protected List<Float> parameters;
	protected float pipsValue;
	/** 
	* @param assetType
	 * @param dataEventType
	 * @param parameters - 	may be different parameters according to event type.
	 * 						JAPANESE - 	parameters={Interval, historyLength}
	 * 						NEW_QUOTE - parameters={}
	 * 						RSI - parameters={Interval, JapaneseCandleBarPropertyType , Length, historyLength}
	 */	
	public IDataExtractorSubject (DataSource dataSource, AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		this.observers = new ConcurrentSkipListSet<IDataExtractorObserver>();
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
		this.dataSource = dataSource;
	}
	
	/**
	 * 
	 * @param observer
	 */
	public IDataExtractorSubject registerObserver(IDataExtractorObserver observer ) {
		boolean runNewTask = false;
		if (this.observers.isEmpty()) {
			runNewTask = true;
		}
		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
			observer.setSubject(this);
		}
		if (runNewTask) {
			SwingUtilities.invokeLater(this);
//			new Thread(this).run();
		}
		return this;
	}
	
	/**
	 * Unregisters Observer from all subscriptions.
	 * @param observer
	 */
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
		observer.removeSubject(this);
		if (this.observers.isEmpty()) {
			RegisterDataExtractor.removeDataExtractorSubject(dataSource, assetType, dataEventType, parameters);
		}
	}
	
	public void notifyObservers(AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		for (IDataExtractorObserver observer : this.observers) {
			observer.notifyObserver(dataEventType, parameters);
		}
	}
	
	public abstract NewUpdateData getNewData();
	
	/**
	 * Returns the file headers line.
	 * @return
	 */
	public abstract String getDataHeaders();
	
	public abstract SubjectState getSubjectState();

	public float getPipsValue() {
		return pipsValue;
	}
	
	@Override
	public int compareTo(IDataExtractorSubject o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		} else {
			if (o.assetType == this.assetType && o.dataEventType == this.dataEventType) {
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
					if (o.dataSource == DataSource.FILE && this.dataSource == DataSource.RAM) {
						return -1;
					} else if (o.dataSource == DataSource.RAM && this.dataSource == DataSource.FILE) {
						return 1;
					} else {
						return 0;
					}
				} else if (o.parameters == null) {
					return 1;
				}
			}
		}
		return -1;
	}
}
