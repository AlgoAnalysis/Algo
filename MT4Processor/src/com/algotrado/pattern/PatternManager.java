package com.algotrado.pattern;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.util.DebugUtil;

public class PatternManager {

	private APatternState firstState;
	private List<StateAndTime> stateArr;
	private PatternManagerStatus status;
	
	public PatternManager(APatternState firstState)
	{
		this.firstState = firstState;
		stateArr = new ArrayList<StateAndTime>();
		stateArr.add(new StateAndTime(firstState));
	}
	
	public void setNewData(Object[] newData)
	{
		boolean needCreateNewState = true;
		for(StateAndTime state : stateArr)
		{
			state.getState().setNewData(newData);
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
				state.addTime(null); // TODO - need the time;
				break;
			case TRIGGER_BEARISH:
				status = PatternManagerStatus.TRIGGER_BEARISH;
				state.addTime(null); // TODO - need the time;
				break;
			case TRIGGER_BULLISH:
				status = PatternManagerStatus.TRIGGER_BULLISH;
				state.addTime(null); // TODO - need the time;
				break;
			case TRIGGER_NOT_SPECIFIED:
				status = PatternManagerStatus.TRIGGER_NOT_SPECIFIED;
				state.addTime(null); // TODO - need the time;
				break;
			case ERROR:
				status = PatternManagerStatus.ERROR;
				break;
			case RUN:
				break;
			}
		}
		if(needCreateNewState)
		{
			stateArr.add(new StateAndTime(firstState));
		}
	}
	
	public PatternManagerStatus getStatus() {
		return status;
	}
	
	public List<Time> getTimeListofTriggerState()
	{
		for(StateAndTime state : stateArr)
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

	private class StateAndTime
	{
		private APatternState state;
		private List<Time> timeList; 
		public StateAndTime(APatternState state)
		{
			this.state = state;
			timeList = new ArrayList<Time>();
		}
		
		public boolean addTime(Time time)
		{
			return timeList.add(time);
		}
		
		public List<Time> getTimeList()
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
