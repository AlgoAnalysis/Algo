package com.algotrado.extract.data;

import java.util.List;

public enum DataEventType {
	JAPANESE{
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
	},/*After close of candle, send candle data*/
	NEW_QUOTE{
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
		
	};/*Get price quote update when new quote arrives*/
	//RSI;
	
	public abstract boolean checkIfTheParametersValid(List<Float> parameters,boolean generteException);
}
