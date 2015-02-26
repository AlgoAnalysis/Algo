package com.algotrado.pattern;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.algotrado.data.event.NewUpdateData;

public class PatternManager {

	private APatternState firstState;
	private List<PStateAndTime> stateArr;
	private PatternManagerStatus status;
	
	public PatternManager(APatternState firstState)
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
		for(PStateAndTime state : stateArr)
		{
			state.getState().setNewData(newData);
			recordState = false;
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
					IPatternFirstState firstState= (IPatternFirstState)state.getState();
					state.addTime(firstState.getStartTime());
				}
				state.addTime(state.getState().getTrigerTime());
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

	private class PStateAndTime
	{
		private APatternState state;
		private List<Date> timeList; 
		public PStateAndTime(APatternState state)
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
		public APatternState getState() {
			return state;
		}
		public void setState(APatternState state) {
			this.state = state;
		}
	}
}
