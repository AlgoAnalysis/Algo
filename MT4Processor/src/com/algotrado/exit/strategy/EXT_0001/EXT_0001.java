package com.algotrado.exit.strategy.EXT_0001;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.entry.strategy.EntryStrategyStateStatus;
import com.algotrado.entry.strategy.IEntryStrategyLastState;
import com.algotrado.entry.strategy.IEntryStrategyState;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.TradeManager;

public class EXT_0001 extends IExitStrategy {
	private double fractionOfOriginalStopLoss;
	
	public EXT_0001(double bottomSpread, double topSpread) {
		this.bottomSpread = bottomSpread;
		this.topSpread = topSpread;
	}

	public EXT_0001(IEntryStrategyLastState entryLastState, double fractionOfOriginalStopLoss, 
			double bottomSpread, double topSpread, double currBrokerSpread, double currPrice) {
		super(entryLastState, bottomSpread, topSpread, currBrokerSpread, entryLastState.getStopLossPrice(), 
				entryLastState.getBuyOrderPrice());
		this.entryLastState = entryLastState;
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
		this.fractionOfOriginalStopLoss = fractionOfOriginalStopLoss;
		
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR) {
			if (isLongDirection) {
				if (currPrice > ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			} else if (isShortDirection) {
				if (currPrice < ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			}
		}
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
		if (this.exitStrategyStatus != ExitStrategyStatus.ERROR) {
			JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar)newData[0];
			if (isLongDirection) {
				if (japaneseCandleBar.getHigh() > ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			} else if (isShortDirection) {
				if (japaneseCandleBar.getLow() < ((2 * exitStrategyEntryPoint) - currStopLoss)) {
					this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
				}
			}
		}
	}

	/**
	 * If exit strategy 0007 is ran than we should force the move S.l of this Strategy.
	 */
	@Override
	public void forceTrigger() {
		if (isLongDirection) {
			this.currStopLoss = this.exitStrategyEntryPoint - (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) * fractionOfOriginalStopLoss); 
		} else if (isShortDirection) {
			this.currStopLoss = this.exitStrategyEntryPoint + (Math.abs(this.exitStrategyEntryPoint - this.currStopLoss) * fractionOfOriginalStopLoss); 
		} else {
			this.exitStrategyStatus = ExitStrategyStatus.ERROR;
			return;
		}
		this.exitStrategyStatus = ExitStrategyStatus.TRIGGER_AND_MOVE_STOP_LOSS;
	}
	
	@Override
	public int getStrategyIndex() {
		return TradeManager.EXIT_0001;
	}

}
