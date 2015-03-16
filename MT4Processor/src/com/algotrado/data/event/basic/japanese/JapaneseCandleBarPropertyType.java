package com.algotrado.data.event.basic.japanese;


public enum JapaneseCandleBarPropertyType {
	OPEN
	{
		@Override
		public double getJapaneseCandleBarValue(JapaneseCandleBar candle) {
			return candle.getOpen();
		}
	},	
	CLOSE
	{
		@Override
		public double getJapaneseCandleBarValue(JapaneseCandleBar candle) {
			return candle.getClose();
		}
	},
	HIGH
	{
		@Override
		public double getJapaneseCandleBarValue(JapaneseCandleBar candle) {
			return candle.getHigh();
		}
	},
	LOW
	{
		@Override
		public double getJapaneseCandleBarValue(JapaneseCandleBar candle) {
			return candle.getLow();
		}
	};
	
	public abstract double getJapaneseCandleBarValue(JapaneseCandleBar candle);
	static public JapaneseCandleBarPropertyType getJapaneseCandleBarPropertyType(Double index)
	{
		if((index.intValue() != index.doubleValue()) ||
			(index.intValue() < 0) ||
			(index.intValue() >= JapaneseCandleBarPropertyType.values().length))
		{
			return null;
		}
		return JapaneseCandleBarPropertyType.values()[index.intValue()];
	}
	
	static public boolean isPropertyValid(Double Property)
	{
		if(Property.intValue() != Property)
		{
			return false;
		}
		if(Property<0 || Property >=JapaneseCandleBarPropertyType.values().length)
		{
			return false;
		}
		return true;
	}
}
