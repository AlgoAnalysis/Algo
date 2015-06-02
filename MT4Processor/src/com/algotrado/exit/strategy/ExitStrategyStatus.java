package com.algotrado.exit.strategy;

import com.algotrado.trade.TradeManager;

public enum ExitStrategyStatus {
	RUN(1) {
		@Override
		public void triggerExitState(IExitStrategy exitStrategy, TradeManager trade) {
			// Do nothing if triggered.
//			throw new RuntimeException("Method should not be called in this state.");
		}
	},
	TRIGGER(2) {
		@Override
		public void triggerExitState(IExitStrategy exitStrategy , TradeManager trade) {
			if (exitStrategy != null) {
				exitStrategy.triggerExit(trade);
			}
		}
	},
	MOVE_STOP_LOSS(4) {
		@Override
		public void triggerExitState(IExitStrategy exitStrategy, TradeManager trade) {
			if (exitStrategy != null) {
				exitStrategy.moveSL(trade);
			}
		}
	},
	TRIGGER_AND_MOVE_STOP_LOSS(6) {
		@Override
		public void triggerExitState(IExitStrategy exitStrategy, TradeManager trade) {
			if (exitStrategy != null) {
				exitStrategy.triggerExitAndMoveSL(trade);
			}
		}
	},
	ERROR(0) {
		@Override
		public void triggerExitState(IExitStrategy exitStrategy, TradeManager trade) {
			throw new RuntimeException("Method should not be called in this state.");
		}
	};
	
	private int value;
	
	private ExitStrategyStatus (int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	public abstract void triggerExitState(IExitStrategy exitStrategy, TradeManager trade); 
}
