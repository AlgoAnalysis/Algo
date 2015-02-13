package com.algotrado.extract.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
	/** 
	* @param assetType
	 * @param dataEventType
	 * @param parameters - 	may be different parameters according to event type.
	 * 						JAPANESE - 	parameters={Interval}
	 * 						NEW_QUOTE - parameters={}
	 */	
	public IDataExtractorSubject (AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		this.observers = new ArrayList<IDataExtractorObserver>();
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
	}
	
	/**
	 * 
	 * @param observer
	 */
	public void registerObserver(IDataExtractorObserver observer ) {
		if (!this.observers.contains(observer)) {
			this.observers.add(observer);
		}
	}
	
	/**
	 * Unregisters Observer from all subscriptions.
	 * @param observer
	 */
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
	}
	
	public void notifyObservers(AssetType assetType, DataEventType dataEventType, List<Float> parameters) {
		for (IDataExtractorObserver observer : this.observers) {
			observer.notifyObserver();
		}
	}
}
