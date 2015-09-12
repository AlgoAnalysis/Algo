package com.algotrado.extract.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.minimal.time.frame.MinimalTimeFrame;
import com.algotrado.util.DebugUtil;

/**
 * This is the abstract class for data extractor.
 * All Subclasses must implement run() method that should do the data extraction functionality.
 * @author Ariel
 *
 */
public abstract class IDataExtractorSubject implements Comparable<IDataExtractorSubject>{
	
	protected Collection<IDataExtractorObserver> observers;
	protected DataSource dataSource;
	protected AssetType assetType; 
	protected DataEventType dataEventType;
	protected List<Double> parameters;
	protected double pipsValue;
	protected IDataExtractorSubject[] dataExtractorSubjectArr;
	/** 
	* @param assetType
	 * @param dataEventType
	 * @param parameters - 	may be different parameters according to event type.
	 * 						JAPANESE - 	parameters={Interval, historyLength}
	 * 						NEW_QUOTE - parameters={}
	 * 						RSI - parameters={Interval, JapaneseCandleBarPropertyType , Length, historyLength}
	 */	
	public IDataExtractorSubject (DataSource dataSource, AssetType assetType, DataEventType dataEventType, List<Double> parameters) {
		if(DebugUtil.debugDataExtractor)
		{
			if(checkIfDataConstractorValid(dataSource,assetType,dataEventType,parameters))
			{
				throw new RuntimeException("DataExtractorSubject data input not valid");
			}
		}
		this.observers = new ConcurrentLinkedQueue<IDataExtractorObserver>();
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
		setParameters(parameters);
		this.dataSource = dataSource;
	}
	
	/**
	 * 
	 * @param observer
	 */
	public IDataExtractorSubject registerObserver(IDataExtractorObserver observer) {
		if (!findElementInCollection(this.observers,observer)) {
			this.observers.add(observer);
			observer.setSubject(this);
		}
		return this;
	}
	
	public boolean findElementInCollection(Collection<IDataExtractorObserver> collection, IDataExtractorObserver observer) {

	    for (IDataExtractorObserver singleObserver : this.observers) {
	    	if (observer.equals(singleObserver)) {
	    		return true;
	    	}
	    }
	            
	    return false;
	}
	
	/**
	 * Unregisters Observer from all subscriptions.
	 * @param observer
	 */
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
		observer.removeSubject(this);
		if (this.observers.isEmpty()) {
			if(dataExtractorSubjectArr != null)
			{
				for(int cnt = 0;cnt < dataExtractorSubjectArr.length ;cnt++)
				{
					dataExtractorSubjectArr[cnt].unregisterObserver((IDataExtractorObserver)this);
				}
			}
			RegisterDataExtractor.removeDataExtractorSubject(dataSource, assetType, dataEventType, parameters);
		}
	}
	
	public void notifyObservers(AssetType assetType, DataEventType dataEventType, List<Double> parameters) {
		for (IDataExtractorObserver observer : this.observers) {
			observer.notifyObserver(dataEventType, parameters);
		}
	}
	
	public boolean checkIfDataConstractorValid(DataSource dataSource,
			AssetType assetType, DataEventType dataEventType,
			List<Double> parameters) {
		if((dataSource == null) || (assetType == null) || (dataEventType == null) || (parameters == null))
			return false;
		if(dataEventType != getDataEventType())
			return false;
		if(!dataEventType.checkIfTheParametersValid(parameters, DebugUtil.debugDataExtractor))
			return false;
		if(dataEventType == DataEventType.NEW_QUOTE)
		{
			if(((MinimalTimeFrame)this).getDataSource() != dataSource)
				return false;
		}
		return true;
	}
	
	public abstract NewUpdateData getNewData();
	public abstract DataEventType getDataEventType();
	public abstract void setParameters(List<Double> parameters);
	
	/**
	 * Returns the file headers line.
	 * @return
	 */
	public abstract String getDataHeaders();
	
	public abstract SubjectState getSubjectState();

	public double getPipsValue() {
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
					Iterator<Double> fileDataRecorderIterator = this.parameters.iterator();
					for (Iterator<Double> oIterator = o.parameters.iterator(); oIterator.hasNext() && fileDataRecorderIterator.hasNext();) {
						Double oParam = oIterator.next();
						Double fileDataRecorderParam = fileDataRecorderIterator.next();
						if (oParam != fileDataRecorderParam) {
							return new Double(fileDataRecorderParam - oParam).intValue();
						}
						
					}
					return (o.dataSource.ordinal() - this.dataSource.ordinal());
				} else if (o.parameters == null) {
					return 1;
				}
			}
		}
		return -1;
	}
}
