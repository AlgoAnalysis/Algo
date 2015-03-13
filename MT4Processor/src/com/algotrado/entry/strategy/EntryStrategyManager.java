package com.algotrado.entry.strategy;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.algotrado.data.event.NewUpdateData;


public class EntryStrategyManager {

	private IEntryStrategyState firstState;
	private List<EntryStrategyStateAndTime> stateArr;
	private EntryStrategyManagerStatus status;
	
	public EntryStrategyManager(IEntryStrategyState firstState)
	{
		this.firstState = firstState;
		stateArr = new ArrayList<EntryStrategyStateAndTime>();
		stateArr.add(new EntryStrategyStateAndTime(firstState));
	}
	
	public void setNewData(NewUpdateData[] newData)
	{
		boolean needCreateNewState = true;
		boolean recordState;
		status = EntryStrategyManagerStatus.RUN;
		IEntryStrategyState prevState;
		for(Iterator<EntryStrategyStateAndTime> iterator = stateArr.iterator(); iterator.hasNext() ;)
		{
			EntryStrategyStateAndTime state = iterator.next();
			state.getState().setNewData(newData);
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
