package com.algotrado.pattern;

import java.util.Date;
import java.util.List;

import com.algotrado.data.event.NewUpdateData;


// TODO - remove this class
public class PatternDataObject implements NewUpdateData {

	private PatternManager patternManager;
	private String assetName;
	
	public PatternDataObject(PatternManager patternManager, String assetName) {
		this.assetName = assetName;
		this.patternManager = patternManager;
	}

	public PatternManager getPatternManager() {
		return patternManager;
	}

	public PatternManagerStatus getPatternManagerStatus() {
		return patternManager.getStatus();
	}

	@Override
	public Date getTime() {
		return patternManager.getTimeListofTriggerState().get(0);
	}

	@Override
	public String getAssetName() {
		return this.assetName;
	}

	@Override
	public String getDataHeaders() {
		return patternManager.getDataHeaders();
	}
	
	@Override
	public String toString() {
		return patternManager.toString();
	}
	
	public List<Date> getPatternDates() {
		return patternManager.getTimeListofTriggerState();
	}
	
	
}
