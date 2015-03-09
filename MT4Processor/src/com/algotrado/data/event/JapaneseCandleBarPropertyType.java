package com.algotrado.data.event;

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
	static public JapaneseCandleBarPropertyType getJapaneseCandleBarPropertyType(Float index)
	{
		if((index.intValue() != index.floatValue()) ||
			(index.intValue() < 0) ||
			(index.intValue() >= JapaneseCandleBarPropertyType.values().length))
		{
			return null;
		}
		return JapaneseCandleBarPropertyType.values()[index.intValue()];
	}
}
