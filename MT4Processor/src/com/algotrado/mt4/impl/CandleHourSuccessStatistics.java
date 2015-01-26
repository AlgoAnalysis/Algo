package com.algotrado.mt4.impl;

public class CandleHourSuccessStatistics {
	private int hour;
	private int occourancesNum;
	private int numOfSuccessOccurances;
	public CandleHourSuccessStatistics(int hour, int occourancesNum,
			int numOfSuccessOccurances) {
		super();
		this.hour = hour;
		this.occourancesNum = occourancesNum;
		this.numOfSuccessOccurances = numOfSuccessOccurances;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getOccourancesNum() {
		return occourancesNum;
	}
	public void setOccourancesNum(int occourancesNum) {
		this.occourancesNum = occourancesNum;
	}
	public int getNumOfSuccessOccurances() {
		return numOfSuccessOccurances;
	}
	public void setNumOfSuccessOccurances(int numOfSuccessOccurances) {
		this.numOfSuccessOccurances = numOfSuccessOccurances;
	}
	
}
