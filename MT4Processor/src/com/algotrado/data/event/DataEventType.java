package com.algotrado.data.event;

import java.util.List;

public enum DataEventType {
	JAPANESE("Japanese"){
		@Override
		public boolean checkIfTheParametersValid(List<Float> parameters,boolean generteException)
		{
			boolean ret = true;
			if(parameters.size() != 2)
			{
				if(generteException)
				{
					throw new RuntimeException	("The Japanse data event need 2 parameters.\n"
												+"And not " + new Integer(parameters.size()).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if( !TimeFrameType.isIntervalValid(parameters.get(0)))
			{
				if(generteException)
				{
					throw new RuntimeException	("The Japanse data event get not valid interval size (or not suppurted).\n"
												+"The setting interval was " + parameters.get(0).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if((parameters.get(1).intValue() != parameters.get(1)) || parameters.get(1) < 0)
			{
				if(generteException)
				{
					throw new RuntimeException	("The Japanse data event get not valid history length.\n"
												+"The history length was " + parameters.get(1).toString());
				}
				else
				{
					ret =false;
				}
			}
			return ret;
		}
		
		@Override
		public String getDataHeaders() {
			return "Open Price, High Price, Low Price, Close Price, Volume";
		}
	},/*After close of candle, send candle data*/
	NEW_QUOTE("New quote"){
		@Override
		public boolean checkIfTheParametersValid(List<Float> parameters,boolean generteException)
		{
			boolean ret = true;
			if(parameters.size() != 0)
			{
				if(generteException)
				{
					throw new RuntimeException	("The New quote data event need 0 parameters.\n"
												+"And not " + new Integer(parameters.size()).toString());
				}
				else
				{
					ret =false;
				}
			}
			return ret;
		}
		
		@Override
		public String getDataHeaders() {
			return "Current Price";
		}
		
	};/*Get price quote update when new quote arrives*/
	//RSI;
	
	private String valueString;
	
	private DataEventType(String valueString)
	{
		this.valueString = valueString;
	}
	
	public static String[] getDataEventStrings()
	{
		String[] ret = new String[DataEventType.values().length];
		for(int index = 0;index<DataEventType.values().length;index++)
		{
			ret[index] = DataEventType.values()[index].valueString;
		}
		return ret;
	}
	
	public static DataEventType getDataEventTypeFromString(String valueString)
	{
		for(DataEventType event:DataEventType.values())
		{
			if(event.valueString == valueString)
			{
				return event;
			}
		}
		return null;
	}
	
	public String getDataEventString()
	{
		return valueString;
	}
	
	public abstract boolean checkIfTheParametersValid(List<Float> parameters,boolean generteException);
	
	public abstract String getDataHeaders();
}
