package com.algotrado.entry.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.pattern.PatternDataObject;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerStatus;
import com.algotrado.util.Setting;


public class EntryStrategyManager {

	private IEntryStrategyState firstState;
	private List<EntryStrategyStateAndTime> stateArr;
	private EntryStrategyManagerStatus status;
	private String assetName;
	/**
	 * pattern managers is not null, can be empty list.
	 */
	private List<PatternManager> patternManagers;
	
	public EntryStrategyManager(IEntryStrategyState firstState, List<PatternManager> patternManagers, String assetName)
	{
		this.firstState = firstState;
		stateArr = new ArrayList<EntryStrategyStateAndTime>();
		stateArr.add(new EntryStrategyStateAndTime(firstState));
		this.patternManagers = patternManagers;
		this.assetName = assetName;
	}
	
	public void setNewData(NewUpdateData[] newData)
	{
		boolean needCreateNewState = true;
		boolean recordState;
		status = EntryStrategyManagerStatus.RUN;
		IEntryStrategyState prevState;
		//Prepare New Data:
		
		ArrayList<NewUpdateData> newUpdateDatasList  = new ArrayList<NewUpdateData>();
		
		for (NewUpdateData newUpdateData : newData) {
			newUpdateDatasList.add(newUpdateData);
		}
		
		for (PatternManager patternManager : patternManagers) {
			patternManager.setNewData(newData);
			if (patternManager.getStatus() == PatternManagerStatus.TRIGGER_BEARISH ||
					patternManager.getStatus() == PatternManagerStatus.TRIGGER_BULLISH ||
					patternManager.getStatus() == PatternManagerStatus.TRIGGER_NOT_SPECIFIED) {
				newUpdateDatasList.add(new PatternDataObject(patternManager, assetName));
			} else if (patternManager.getStatus() == PatternManagerStatus.ERROR) {
				status = EntryStrategyManagerStatus.ERROR;
				throw new RuntimeException	("Error Occoured in Pattern Manager."); // TODO 
			} else {
				status = EntryStrategyManagerStatus.RUN;
			}
		}
		
		NewUpdateData[] newDataGenerated = newUpdateDatasList.toArray(new NewUpdateData [newUpdateDatasList.size()]);
		
		for(Iterator<EntryStrategyStateAndTime> iterator = stateArr.iterator(); iterator.hasNext() ;)
		{
			EntryStrategyStateAndTime state = iterator.next();
			state.getState().setNewData(newDataGenerated);
			recordState = false;
			prevState = state.getState();
			switch(state.getState().getStatus())
			{
			case WAIT_TO_START:
				needCreateNewState = false;
				break;
			case KILL_STATE:
				iterator.remove();
				break;
			case RUN_TO_NEXT_STATE:
				state.setState(state.getState().getNextState());
				recordState = true;
				break;
			case TRIGGER_BEARISH:
				status = EntryStrategyManagerStatus.TRIGGER_BEARISH;
				recordState = true;
				break;
			case TRIGGER_BULLISH:
				status = EntryStrategyManagerStatus.TRIGGER_BULLISH;
				recordState = true;
				break;
			case ERROR:
				status = EntryStrategyManagerStatus.ERROR;
				break;
			case RUN:
				break;
			}
			if(recordState)
			{
				if(state.getTimeList().size() == 0)
				{
					state.addTime(((IEntryStrategyFirstState) prevState).getStartTime());
				}
				state.addTime(prevState.getTriggerTime());
			}
		}
		if(needCreateNewState)
		{
			stateArr.add(new EntryStrategyStateAndTime(firstState));
		}
	}
	
	public EntryStrategyManagerStatus getStatus() {
		return status;
	}
	
	public String getDataHeaders() {
		Integer numOfStates = firstState.getNumberOfStates();
		String headerString = Setting.getDateTimeHeader("Start Strategy") + ",";
		for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
		{
			headerString += Setting.getDateTimeHeader("State " + cnt.toString() + " triggered ") + ",";
		}
		
		headerString += "Direction, Entry, Stop Loss";
		
		return headerString;
	}
	
	@Override
	public String toString() {
		String valString = "";
		Integer numOfStates = firstState.getNumberOfStates();
		boolean firstLine = true;
		for(EntryStrategyStateAndTime state : stateArr)
		{
			if((state.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH) ||
				(state.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BULLISH))
			{
				if(!firstLine)
				{
					valString += "\n";
				}
				else
				{
					firstLine = false;
				}
				for(Integer cnt = 0;cnt <= numOfStates.intValue();cnt++)
				{
					valString += Setting.getDateTimeFormat(state.getTimeList().get(cnt)) + ",";
				}
				
				if (state.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH) {
					valString += "Short,";
				} else {
					valString += "Long,";
				}
				
				valString += ((IEntryStrategyLastState)state.getState()).getBuyOrderPrice() + ",";
				valString += ((IEntryStrategyLastState)state.getState()).getStopLossPrice() + ",";
			}
			
		}
		return valString;
	}
	
	private class EntryStrategyStateAndTime
	{
		private IEntryStrategyState state;
		private List<Date> timeList; 
		public EntryStrategyStateAndTime(IEntryStrategyState state)
		{
			
			IEntryStrategyFirstState firstState = (IEntryStrategyFirstState)state;
			this.state = firstState.getCopyPatternState();
			timeList = new ArrayList<Date>();
		}
		
		public boolean addTime(Date time)
		{
			return timeList.add(time);
		}
		
		public List<Date> getTimeList()
		{
			return timeList;
		}
		public IEntryStrategyState getState() {
			return state;
		}
		public void setState(IEntryStrategyState state) {
			this.state = state;
		}
	}
}
