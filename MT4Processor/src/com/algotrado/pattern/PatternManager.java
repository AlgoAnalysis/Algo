package com.algotrado.pattern;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.util.Setting;

public class PatternManager {

	private IPatternState firstState;
	private List<PStateAndTime> stateArr;
	private PatternManagerStatus status;
	
	public PatternManager(IPatternState firstState)
	{
		this.firstState = firstState;
		stateArr = new ArrayList<PStateAndTime>();
		stateArr.add(new PStateAndTime(firstState));
	}
	
	public void setNewData(NewUpdateData[] newData)
	{
		boolean needCreateNewState = true;
		boolean recordState;
		status = PatternManagerStatus.RUN;
		IPatternState prevState;
		for(int cnt = stateArr.size() -1; cnt >=0 ;cnt --)
		{
			PStateAndTime state = stateArr.get(cnt);
			state.getState().setNewData(newData);
			recordState = false;
			prevState = state.getState();
			switch(state.getState().getStatus()) // check if we mast switch
			{
			case WAIT_TO_START:
				needCreateNewState = false;
				break;
			case KILL_STATE:
			case ALREADY_TRIGGERD:
				stateArr.remove(state);
				break;
			case RUN_TO_NEXT_STATE:
				state.setState(state.getState().getNextState()); // TODO - need to check if need to remove and add or the list is pointer to this state
				recordState = true;
				break;
			case TRIGGER_BEARISH:
				status = PatternManagerStatus.TRIGGER_BEARISH;
				recordState = true;
				break;
			case TRIGGER_BULLISH:
				status = PatternManagerStatus.TRIGGER_BULLISH;
				recordState = true;
				break;
			case TRIGGER_NOT_SPECIFIED:
				status = PatternManagerStatus.TRIGGER_NOT_SPECIFIED;
				recordState = true;
				break;
			case ERROR:
				status = PatternManagerStatus.ERROR;
				break;
			case RUN:
				break;
			}
			if(recordState)
			{
				if(state.getTimeList().size() == 0)
				{
					state.addTime(((IPatternFirstState) prevState).getStartTime());
				}
				state.addTime(prevState.getTriggerTime());
			}
		}
		if(needCreateNewState)
		{
			stateArr.add(new PStateAndTime(firstState));
		}
	}
	
	public PatternManagerStatus getStatus() {
		return status;
	}
	
	public List<Date> getTimeListofTriggerState()
	{
		for(PStateAndTime state : stateArr)
		{
			if(state.getState().getStatus() == PatternStateStatus.TRIGGER_BEARISH || 
					state.getState().getStatus() == PatternStateStatus.TRIGGER_BULLISH ||
					state.getState().getStatus() == PatternStateStatus.TRIGGER_NOT_SPECIFIED)
			{
				return state.getTimeList();
			}
		}		
		return null;
	}
	
	
	
	public String getDataHeaders() {
		Integer numOfStates = firstState.getNumberOfStates();
		String headerString = Setting.getDateTimeHeader("Start Pattern") + ",";
		for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
		{
			headerString += Setting.getDateTimeHeader("State " + cnt.toString() + " triggerd ") + ",";
		}
		return headerString;
	}
	
	@Override
	public String toString() {
		String valString = "";
		PStateAndTime stateFolnd = null;
		Integer numOfStates = firstState.getNumberOfStates();
		for(PStateAndTime state : stateArr)
		{
			if((state.getState().getStatus() == PatternStateStatus.TRIGGER_BEARISH) ||
				(state.getState().getStatus() == PatternStateStatus.TRIGGER_BULLISH) ||
				(state.getState().getStatus() == PatternStateStatus.TRIGGER_NOT_SPECIFIED))
			{
				stateFolnd = state;
				break;
			}
		}
		if(stateFolnd != null)
		{
			SimpleDateFormat dateformatter = new SimpleDateFormat(Setting.getDateTimeFormat());
			valString = dateformatter.format(stateFolnd.getTimeList().get(0)) + ",";
			for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
			{
				valString += dateformatter.format(stateFolnd.getTimeList().get(cnt)) + ",";
			}
		}
		return valString;
	}
	
	
	////////////////////// internal class ///////////////////////////////////

	private class PStateAndTime
	{
		private IPatternState state;
		private List<Date> timeList; 
		public PStateAndTime(IPatternState state)
		{
			
			IPatternFirstState firstState = (IPatternFirstState)state;
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
		public IPatternState getState() {
			return state;
		}
		public void setState(IPatternState state) {
			this.state = state;
		}
	}
	
}
