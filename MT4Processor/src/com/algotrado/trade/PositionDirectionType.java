package com.algotrado.trade;

public enum PositionDirectionType {
	LONG {
		@Override
		public boolean isValidStopLoss(double stoploss, double brokerSpread, double currAssetPrice) {
			return stoploss < currAssetPrice - brokerSpread;
		}
	},
	SHORT {
		@Override
		public boolean isValidStopLoss(double stoploss, double brokerSpread, double currAssetPrice) {
			return stoploss > currAssetPrice + brokerSpread;
		}
	};
	
	public abstract boolean isValidStopLoss(double stoploss, double brokerSpread, double currAssetPrice);
}
