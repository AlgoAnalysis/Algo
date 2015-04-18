package com.algotrado.extract.data.file;

import com.algotrado.trade.PositionOrderStatusType;

public enum FileTradeStatus {
	PENDING(PositionOrderStatusType.PENDING),
	OPEN(PositionOrderStatusType.OPENED),
	CLOSE_BY_STOP_LOSS(PositionOrderStatusType.CLOSED),
	CLOSE_BY_TAKE_PROFIT(PositionOrderStatusType.CLOSED),
	CLOSE_BY_USER(PositionOrderStatusType.CLOSED);
	
	private PositionOrderStatusType positionOrderStatusType;
	private FileTradeStatus(PositionOrderStatusType positionOrderStatusType)
	{
		this.positionOrderStatusType = positionOrderStatusType;
	}
	
	public PositionOrderStatusType getPositionOrderStatusType() {
		return positionOrderStatusType;
	}
}
