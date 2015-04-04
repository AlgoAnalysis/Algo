package com.algotrado.trade;

public class PositionStatus {
	
	private PositionDirectionType positionDirectionType;
	private double entryPoint;
	private double currentPosition;
	private PositionOrderStatusType positionOrderStatusType;
	public PositionStatus(PositionDirectionType positionDirectionType,
			double entryPoint, double currentPosition, PositionOrderStatusType positionOrderStatusType) {
		super();
		this.positionDirectionType = positionDirectionType;
		this.entryPoint = entryPoint;
		this.currentPosition = currentPosition;
		this.positionOrderStatusType = positionOrderStatusType;
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
	
	/**
	 * Positive means a gain in money, negative means loss.
	 * @return
	 */
	public double getPositionCurrGain() {
		int directionMultiplier = (positionDirectionType.ordinal() > 0) ? (-1) : 1;
		return directionMultiplier * (currentPosition - entryPoint);
	}
}
