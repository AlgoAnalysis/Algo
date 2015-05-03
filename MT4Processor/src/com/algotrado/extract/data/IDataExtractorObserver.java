package com.algotrado.extract.data;

import java.util.List;

import com.algotrado.data.event.DataEventType;

public interface IDataExtractorObserver {

	public void notifyObserver(DataEventType dataEventType, List<Double> parameters);
	
	/**
	 * Use this method to pass the subject to the observer, so the observer can get the new data updates.
	 * Another way to change the way this method works is to send the updates through the notifyObserver method.
	 * @param dataExtractorSubject
	 */
	public void setSubject(IDataExtractorSubject dataExtractorSubject);
	public void removeSubject(IDataExtractorSubject dataExtractorSubject);
}
