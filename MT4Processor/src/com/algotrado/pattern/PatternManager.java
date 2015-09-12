package com.algotrado.pattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.util.Setting;

public class PatternManager{

	private IPatternFirstState firstState;
	private List<PStateAndTime> stateArr;
	private PatternManagerStatus status;
	private Iterator<PStateAndTime> iterator;
	private PStateAndTime state;
	private boolean needCreateNewState;
	private APatternState prevState;
	private PStateAndTime triggerStateAndTime;
	private PatternManagerHolder patternManagerHolder;
	
	public PatternManager(IPatternFirstState firstState, PatternManagerHolder patternManagerHolder)
	{
		this.firstState = firstState;
		firstState.setPatternManager(this);
		stateArr = new ArrayList<PStateAndTime>();
		stateArr.add(new PStateAndTime(firstState));
		this.patternManagerHolder = patternManagerHolder;
		
	}
	
	public void setNewData(NewUpdateData[] newData)
	{
		triggerStateAndTime = null;
		needCreateNewState = true;
		status = PatternManagerStatus.RUN;
		
		for(iterator = stateArr.iterator(); iterator.hasNext() ;)
		{
			state = iterator.next();
			prevState = state.getState();
			state.getState().setNewData(newData);
		}
		if(needCreateNewState)
		{
			stateArr.add(new PStateAndTime(firstState));
		}
	}
	
	public PatternManagerStatus getStatus() {
		return status;
	}
	
	private void recordState()
	{
		if(state.getTimeList().size() == 0)
		{
			state.addTime(((IPatternFirstState) prevState).getStartTime());
		}
		state.addTime(prevState.getTriggerTime());		
	}
	
	public void patternWaitToStart()
	{
		needCreateNewState = false;
	}
	
	public void patternKillState()
	{
		iterator.remove();
	}
	
	public void patternRunToNextState()
	{
		state.setState(state.getState().getNextState());
		recordState();
	}
	
	public void patternTrigger(PatternManagerStatus triggerType)
	{
		status = triggerType;
		triggerStateAndTime = state;
		recordState();
		patternManagerHolder.patternTrigger((IPatternLastState)state.getState());
	}	
	
	public List<Date> getTimeListofTriggerState()
	{
		if(triggerStateAndTime != null)
		{
			return triggerStateAndTime.getTimeList();
		}
		return null;
	}
	
	public String getDataHeaders() {
		Integer numOfStates = ((APatternState)firstState).getNumberOfStates();
		String headerString = Setting.getDateTimeHeader("Start Pattern") + ",";
		for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
		{
			headerString += Setting.getDateTimeHeader("State " + cnt.toString() + " triggerd ") + ",";
		}
		headerString += "Direction";
		return headerString;
	}
	
	@Override
	public String toString() {
		String valString = "";
		Integer numOfStates = ((APatternState)firstState).getNumberOfStates();
		if(triggerStateAndTime != null)
		{
			valString = Setting.getDateTimeFormat(triggerStateAndTime.getTimeList().get(0)) + ",";
			for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
			{
				valString += Setting.getDateTimeFormat(triggerStateAndTime.getTimeList().get(cnt)) + ",";
			}
			valString += triggerStateAndTime.getState().getStatus().toString();
		}
		return valString;
	}
	
	public IPatternLastState getLastTriggerState() {
		return (IPatternLastState)triggerStateAndTime.getState();
	}
	
	
	
	////////////////////// internal class ///////////////////////////////////

	private class PStateAndTime
	{
		private APatternState state;
		private List<Date> timeList; 
		public PStateAndTime(IPatternFirstState firstState)
		{
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
		public APatternState getState() {
			return state;
		}
		public void setState(APatternState state) {
			this.state = state;
		}
	}
	
}
