package com.algotrado.entry.strategy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Timer;

import com.algotrado.broker.IBroker;
import com.algotrado.broker.IBrokerResponse;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.money.manager.IMoneyManager;
import com.algotrado.pattern.IPatternFirstState;
import com.algotrado.pattern.PatternManager;
import com.algotrado.pattern.PatternManagerHolder;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.util.Setting;

public abstract class AEntryStrategyObserver implements IDataExtractorObserver,IBrokerResponse,ActionListener,PatternManagerHolder {

	protected List<PatternManager> patternManagers;
	protected List<List<EntryStrategyStateAndTime>> entryStrategyStatesList;
	protected IEntryStrategyFirstState firstState;
	protected DataSource dataSource;
	protected NewUpdateData[] udateData;
	protected IDataExtractorSubject subjectArr[];
	
	private IMoneyManager moneyManager;
	private AssetType assetType;
	private IBroker broker;
	private Map<Integer,RequestInfo> entryStrategyLastStateMap;
	private Map<Integer,Object> answerBeforeEndSendig; // TODO - this is for data file read only.  
	
	private Iterator<EntryStrategyStateAndTime> entryIterator;
	private EntryStrategyStateAndTime entryState;
	private IEntryStrategyState prevEntryState;
	private int entryStateupdateNumber;
	private EntryStrategyStateAndTime lastTriggerState;
	
	public AEntryStrategyObserver(DataSource dataSource,AssetType assetType,IBroker broker,IMoneyManager moneyManager)
	{
		this.entryStrategyLastStateMap = new HashMap<Integer,RequestInfo>(); 
		this.answerBeforeEndSendig = new HashMap<Integer, Object>();
		this.assetType = assetType;
		
		
		entryStrategyStatesList = new ArrayList<List<EntryStrategyStateAndTime>>();

		this.broker= broker;
		this.moneyManager = moneyManager;
	}
	
	protected void init(IPatternFirstState[] patternfirstStateArr)
	{
		Integer numberOfEntryStates = ((IEntryStrategyState)firstState).getNumberOfStates();
		for(int cnt = 0;cnt < numberOfEntryStates;cnt++)
		{
			entryStrategyStatesList.add(new ArrayList<EntryStrategyStateAndTime>());
		}
		
		EntryStrategyStateAndTime entryStrategyStateAndTime = new EntryStrategyStateAndTime(firstState);
		entryStrategyStatesList.get(0).add(entryStrategyStateAndTime);
		
		patternManagers =  new ArrayList<PatternManager>();
		for(IPatternFirstState patternfirstState: patternfirstStateArr)
		{
			patternManagers.add(new PatternManager(patternfirstState,this));
		}
	}

	public synchronized void entryTrigerr(IEntryStrategyLastState entryStrategyLastState,PositionDirectionType direction,double currStopLoss)
	{
		lastTriggerState = entryState;
		recordState();
		double quantity = moneyManager.getQuantityForEntry(entryStrategyLastState);
		if(quantity > 0)
		{
			boolean needGenerateTimer = true;
			Integer requestId = broker.openPosition(assetType, quantity, entryStrategyLastState.getPositionDirectionType(),
													entryStrategyLastState.getStopLossPrice(),
													entryStrategyLastState.getTakeProfitPrice() , this);
			
			if(answerBeforeEndSendig.size() != 0)
			{
				Object answer = answerBeforeEndSendig.get(requestId);
				if(answer != null)
				{
					needGenerateTimer = false;
					RequestInfo requestInfo = new RequestInfo(entryStrategyLastState, quantity, null);
					answerDecrypt(answer,requestInfo);
				}
			}
			
			if(needGenerateTimer) // Normal operation
			{
				Timer timer = new Timer(Setting.getBrokerAnswerTimeoutMiliSec(), this);
				timer.setRepeats(false);
				RequestInfo requestInfo = new RequestInfo(entryStrategyLastState,quantity,timer);
				entryStrategyLastStateMap.put(requestId, requestInfo);
				timer.start();
			}
		}
	}
	
	public synchronized void answerFromBroker(Integer requestId,Object answer)
	{
		RequestInfo requestInfo;
		requestInfo = entryStrategyLastStateMap.get(requestId);
		if(requestInfo != null) // Normal operation
		{
			requestInfo.getTimer().stop();
			entryStrategyLastStateMap.remove(requestId);
			answerDecrypt(answer,requestInfo);
		}
		else //TODO -  not normal operation, probably from data file broker.
			 // also can by delay in answer 
		{
			
		}
	}
	
	private void answerDecrypt(Object answer,RequestInfo requestInfo)
	{
		Integer tradeId = (Integer)answer;
		if(tradeId > 0)
		{
			moneyManager.entrySucceeded(requestInfo.getEntryStrategyLastState(), requestInfo.getQuantity(),tradeId);
		}
		else
		{
			// TODO - need update moneyManager on this.
		}
		
	}
	
	
	private void recordState()
	{
		if(entryState.getTimeList().size() == 0)
		{
			entryState.addTime(((IEntryStrategyFirstState) prevEntryState).getStartTime());
		}
		entryState.addTime(prevEntryState.getTriggerTime());		
	}
	
	public void entryKillState()
	{
		entryIterator.remove();
	}
	
	public void entryRunToNextState()
	{
		entryState.setState(entryState.getState().getNextState());
		entryIterator.remove();
		entryStrategyStatesList.get(entryStateupdateNumber).add(entryState);
		recordState();
	}
	
	
	@Override
	public synchronized void actionPerformed(ActionEvent arg0) {
		
//		RequestInfo requestInfo = null;
		Integer requestId = null;
		boolean timerInMap = false;
		for(Entry<Integer,RequestInfo> e : entryStrategyLastStateMap.entrySet()	 )
		{
			if(e.getValue().getTimer() == arg0.getSource())
			{
				requestId = e.getKey();
//				requestInfo = e.getValue();
				timerInMap = true;
				break;
			}
		}
		if(timerInMap)
		{
			entryStrategyLastStateMap.remove(requestId);
			// TODO - need retry or update moneyManager for fail ...
		}
		else
		{
			// TODO - not suppose to be
		}
	}
	
	protected void EntryStrategyObserverEndOfLive(){
		moneyManager.entryObserverEndOfLife(this);
	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		for(int cnt = 0;cnt < subjectArr.length;cnt++)
		{
			if(subjectArr[cnt] == dataExtractorSubject)
			{
				subjectArr[cnt] = null;
				break;
			}
		}
	}
	
	
	protected void updateEntryStates(int stateNumber)
	{	
		entryStateupdateNumber = stateNumber;
		for(entryIterator = entryStrategyStatesList.get(stateNumber-1).iterator(); entryIterator.hasNext() ;)
		{
			entryState = entryIterator.next();
			prevEntryState = entryState.getState();
			entryState.getState().setNewData(udateData);
		}
		if(stateNumber == 1)
		{
			if(entryStrategyStatesList.get(0).size() == 0)
			{
				EntryStrategyStateAndTime entryStrategyStateAndTime = new EntryStrategyStateAndTime((IEntryStrategyFirstState)firstState.getCopyPatternState());
				entryStrategyStatesList.get(0).add(entryStrategyStateAndTime);
			}
		}
	}

	
	@Override
	public String toString() {
		String valString = "";
		Integer numOfStates = ((IEntryStrategyState)firstState).getNumberOfStates();
		if(lastTriggerState != null)
		{
			valString = Setting.getDateTimeFormat(lastTriggerState.getTimeList().get(0)) + ",";
			for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
			{
				valString += Setting.getDateTimeFormat(lastTriggerState.getTimeList().get(cnt)) + ",";
			}
			
			if (lastTriggerState.getState().getStatus() == EntryStrategyStateStatus.TRIGGER_BEARISH) {
				valString += "Short,";
			} else {
				valString += "Long,";
			}
			
			valString += ((IEntryStrategyLastState)lastTriggerState.getState()).getBuyOrderPrice() + ",";
			valString += ((IEntryStrategyLastState)lastTriggerState.getState()).getStopLossPrice() + ",";
		}
		return valString;
	}
	
	public String getDataHeaders() {
		Integer numOfStates = ((IEntryStrategyState)firstState).getNumberOfStates();
		String headerString = Setting.getDateTimeHeader("Start Strategy") + ",";
		for(Integer cnt = 1;cnt <= numOfStates.intValue();cnt++)
		{
			headerString += Setting.getDateTimeHeader("State " + cnt.toString() + " triggered ") + ",";
		}
		
		headerString += "Direction, Entry, Stop Loss";
		
		return headerString;
	}
	
	protected void unregisterAllSubjects()
	{
		for(int cnt = 0;cnt < subjectArr.length;cnt++)
		{
			if(subjectArr[cnt] != null)
			{
				subjectArr[cnt].unregisterObserver(this);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private class RequestInfo
	{
		IEntryStrategyLastState entryStrategyLastState;
		double quantity;
		Timer timer;
		public RequestInfo(IEntryStrategyLastState entryStrategyLastState,
				double quantity, Timer timer) {
			super();
			this.entryStrategyLastState = entryStrategyLastState;
			this.quantity = quantity;
			this.timer = timer;
		}
		public IEntryStrategyLastState getEntryStrategyLastState() {
			return entryStrategyLastState;
		}
		public double getQuantity() {
			return quantity;
		}
		public Timer getTimer() {
			return timer;
		}
	}
	
	protected class EntryStrategyStateAndTime
	{
		private IEntryStrategyState state;
		private List<Date> timeList; 
		public EntryStrategyStateAndTime(IEntryStrategyFirstState state)
		{
			
			IEntryStrategyFirstState firstState = state;
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
