package com.algotrado.trade;

import java.util.Date;

public class PositionStatus {
	
	private PositionDirectionType positionDirectionType;
	private double entryPoint;
	private double currentPosition;
	private PositionOrderStatusType positionOrderStatusType;
	private Date date;
	public PositionStatus(PositionDirectionType positionDirectionType,
			double entryPoint, double currentPosition, PositionOrderStatusType positionOrderStatusType, Date date) {
		super();
		this.positionDirectionType = positionDirectionType;
		this.entryPoint = entryPoint;
		this.currentPosition = currentPosition;
		this.positionOrderStatusType = positionOrderStatusType;
		this.date = date;
	}
	public PositionDirectionType getPositionDirectionType() {
		return positionDirectionType;
	}
	public double getEntryPoint() {
		return entryPoint;
	}
	public double getCurrentPosition() {
		return currentPosition;
	}
	
	public PositionOrderStatusType getPositionStatus() {
		return positionOrderStatusType;
	}
	
	public Date getDate() {
		return date;
	}
	/**
	 * Positive means a gain in money, negative means loss.
	 * @return
	 */
	public double getPositionCurrGain() {
		int directionMultiplier = (positionDirectionType.ordinal() > 0) ? (-1) : 1;
		return directionMultiplier * (currentPosition - entryPoint);
	}
}
