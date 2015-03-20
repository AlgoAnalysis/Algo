package com.algotrado.entry.strategy;

import java.util.HashMap;
import java.util.Map;

import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;

public enum EntryStrategyTriggerType {

	BUYING_CLOSE_PRICE{
		@Override
		public double getTriggerPrice(JapaneseCandleBar candle, boolean isLongStrategy) {
//			if (isLongStrategy) {
//				if (candle.getClose() > marginPrice) {
//					return true;
//				}
//			} else {
//				if (candle.getClose() < marginPrice) {
//					return true;
//				}
//			}
			return candle.getClose();
		}
	},
	BUYING_BREAK_PRICE{ /*This part does not take into consideration a case of Price Gap. We should think what to do in such cases.*/
		@Override
		public double getTriggerPrice(JapaneseCandleBar candle, boolean isLongStrategy) {
//			if (isLongStrategy) {
//				if (candle.getHigh() > marginPrice) {
//					return true;
//				}
//			} else {
//				if (candle.getLow() < marginPrice) {
//					return true;
//				}
//			}
			return (isLongStrategy) ? candle.getHigh() : candle.getLow();
		}
	};
	
	private static Map<Integer, EntryStrategyTriggerType> types = new HashMap<Integer, EntryStrategyTriggerType>();
	
	static { 
		types.put(0, BUYING_CLOSE_PRICE);
		types.put(1, BUYING_BREAK_PRICE);
	}
	
	public static EntryStrategyTriggerType getEntryStrategyTriggerType(Integer type) {
		return types.get(type);
	}
	
	public abstract double getTriggerPrice(JapaneseCandleBar candle, boolean isLongStrategy);
}
