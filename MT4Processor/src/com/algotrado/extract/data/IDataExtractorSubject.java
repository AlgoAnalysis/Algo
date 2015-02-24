package com.algotrado.extract.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;

/**
 * This is the abstract class for data extractor.
 * All Subclasses must implement run() method that should do the data extraction functionality.
 * @author Ariel
 *
 */
public abstract class IDataExtractorSubject implements Runnable{
	
	protected Collection<IDataExtractorObserver> observers;
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
	 */	
	public IDataExtractorSubject (AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		this.observers = new ConcurrentSkipListSet<IDataExtractorObserver>();
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
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
			new Thread(this).run();
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
			RegisterDataExtractor.removeDataExtractorSubject(assetType, dataEventType, parameters);
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
}
