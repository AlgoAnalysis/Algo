package com.algotrado.exit.strategy.EXT_0007;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.TradeManager;

public class EXT_0007 extends IExitStrategy{
	private double exitPoint;
	
	public EXT_0007(double bottomSpread, double topSpread) {
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
	}
	
	public EXT_0007(IEntryStrategyLastState entryLastState, double xFactor, double bottomSpread, double topSpread, double currBrokerSpread, double currPrice) {
		super(entryLastState, bottomSpread, topSpread, currBrokerSpread, entryLastState.getStopLossPrice(), entryLastState.getBuyOrderPrice());
		this.exitPoint = ( ((1 + xFactor) * exitStrategyEntryPoint) - (xFactor * currStopLoss) );
		SimpleUpdateData quote = new SimpleUpdateData(null,null,currPrice,0);
		setNewData(new NewUpdateData[] {quote});
	}
	
	@Override
	public void setNewData(NewUpdateData[] newData) {
		SimpleUpdateData quote = (SimpleUpdateData)newData[0];
		if (isLongDirection) {
			if (quote.getValue() > exitPoint) {
				this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
			}
		} else {
			if (quote.getValue() <  exitPoint) {
				this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
			}
		}
	}

	@Override
	public void forceTrigger() {
		this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
	}

	@Override
	public int getStrategyIndex() {
		return TradeManager.EXIT_0007;
	}
}
