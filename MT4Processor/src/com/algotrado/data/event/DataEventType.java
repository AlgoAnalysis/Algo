package com.algotrado.data.event;

import java.util.List;

import com.algotrado.data.event.basic.japanese.JapaneseCandleBarPropertyType;
import com.algotrado.data.event.basic.japanese.JapaneseCandleDataExtractor;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.data.event.indicator.IND_0001.IND_0001;
import com.algotrado.data.event.indicator.IND_0002.IND_0002;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorSubject;

public enum DataEventType {
	JAPANESE("Japanese"){
		@Override
		public boolean checkIfTheParametersValid(List<Double> parameters,boolean generteException)
		{
			boolean ret = true;
			String[] parameterStrings = JAPANESE.getParametersStrings();
			if(parameters.size() != parameterStrings.length)
			{
				if(generteException)
				{
					throw new RuntimeException	("The Japanse data event need " + Integer.toString(parameterStrings.length)+ " parameters.\n"
												+"And not " + new Integer(parameters.size()).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if( !JapaneseTimeFrameType.isIntervalValid(parameters.get(0)))
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
		public String[] getParametersStrings() {
			String[] ret = {"Interval","History lenght"};
			return ret;
		}

		@Override
		public IDataExtractorSubject getSubjectDataExtractor(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters) {
			return new JapaneseCandleDataExtractor(dataSource,assetType,dataEventType,parameters);
		}
		
	},/*After close of candle, send candle data*/
	NEW_QUOTE("New quote"){
		@Override
		public boolean checkIfTheParametersValid(List<Double> parameters,boolean generteException)
		{
			boolean ret = true;
			String[] parameterStrings = NEW_QUOTE.getParametersStrings();
			if(parameters.size() != parameterStrings.length)
			{
				if(generteException)
				{
					throw new RuntimeException	("The New quote data event need " + Integer.toString(parameterStrings.length) + " parameters.\n"
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
		public String[] getParametersStrings() {
			String[] ret = {};
			return ret;
		}

		@Override
		public IDataExtractorSubject getSubjectDataExtractor(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters) {
			// TODO Auto-generated method stub
			return null;
		}
		
		/*@Override
		public String getDataHeaders() {
			return "Current Price";
		}*/
		
	},/*Get price quote update when new quote arrives*/
	RSI("RSI")
	{

		@Override
		public boolean checkIfTheParametersValid(List<Double> parameters,
				boolean generteException) {
			boolean ret = true;
			String[] parameterStrings = RSI.getParametersStrings();
			if(parameters.size() != parameterStrings.length) // TODO
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event need "+ parameterStrings.length+" parameters.\n"
												+"And not " + parameters.size());
				}
				else
				{
					ret =false;
				}
			}
			else if( !JapaneseTimeFrameType.isIntervalValid(parameters.get(0))) // Interval
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event get not valid interval size (or not suppurted).\n"
												+"The setting interval was " + parameters.get(0).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if( !JapaneseCandleBarPropertyType.isPropertyValid(parameters.get(1)) ) // Interval
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event get not valid property (or not suppurted).\n"
												+"The setting property was " + parameters.get(1).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if((parameters.get(2).intValue() != parameters.get(2)) || parameters.get(2) <= 0) // Length
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event get not valid length.\n"
												+"The length was " + parameters.get(2).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if((parameters.get(3).intValue() != parameters.get(3)) || parameters.get(3) < 0) // historyLength
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event get not valid history length.\n"
												+"The length was " + parameters.get(3).toString());
				}
				else
				{
					ret =false;
				}
			}
			else if((parameters.get(4).intValue() != parameters.get(4)) || parameters.get(4) <= 0 || parameters.get(4) > 2) // rsi type
			{
				if(generteException)
				{
					throw new RuntimeException	("The RSI data event get not valid rsi type.\n"
												+"The rsi type was " + parameters.get(4).toString());
				}
				else
				{
					ret =false;
				}
			}
			return ret;
		}

		@Override
		public String[] getParametersStrings() {
			String[] ret = {"Interval", "JapaneseCandleBarPropertyType" , "Length", "historyLength","rsi type"};
			return ret;
		}

		@Override
		public IDataExtractorSubject getSubjectDataExtractor(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters) {
			switch(parameters.get(4).intValue())
			{
			case 1:	
				return new IND_0001(dataSource, assetType,dataEventType,parameters);
			case 2:	
				return new IND_0002(dataSource, assetType,dataEventType,parameters);
			default:
				return null;
			}
		}
		
	},
	MINIMAL_TIME_FRAME("Minimal time frame"){

		@Override
		public boolean checkIfTheParametersValid(List<Double> parameters,boolean generteException)
		{
			boolean ret = true;
			String[] parameterStrings = MINIMAL_TIME_FRAME.getParametersStrings();
			if(parameters.size() != parameterStrings.length)
			{
				if(generteException)
				{
					throw new RuntimeException	("The Minimal time frame need " + Integer.toString(parameterStrings.length) + " parameters.\n"
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
		public String[] getParametersStrings() {
			String[] ret = {};
			return ret;
		}

		@Override
		public IDataExtractorSubject getSubjectDataExtractor(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters) {
			return dataSource.getSubjectDataExtractor(assetType, dataEventType, parameters);
		}
		
	};
	
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

	public abstract String[] getParametersStrings();
	public abstract boolean checkIfTheParametersValid(List<Double> parameters,boolean generteException);
	public abstract IDataExtractorSubject getSubjectDataExtractor(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters);	
	
}
