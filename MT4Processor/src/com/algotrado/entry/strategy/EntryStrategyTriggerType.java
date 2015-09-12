package com.algotrado.entry.strategy;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;

public enum EntryStrategyTriggerType {

	BUYING_CLOSE_PRICE{
		@Override
		public double getTriggerPrice(NewUpdateData newData) {
			return ((JapaneseCandleBar)newData).getClose();
		}
	},
	BUYING_BREAK_PRICE{ /*This part does not take into consideration a case of Price Gap. We should think what to do in such cases.*/
		@Override
		public double getTriggerPrice(NewUpdateData newData) {
			return ((SimpleUpdateData)newData).getValue();
		}
	};
	
	public static EntryStrategyTriggerType getEntryStrategyTriggerType(Integer type) {
		return EntryStrategyTriggerType.values()[type];
	}
	
	public abstract double getTriggerPrice(NewUpdateData newData);
}
