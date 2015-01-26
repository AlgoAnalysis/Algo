package com.algotrado.mt4.impl;

public class CandleDaySuccessStatistics {
	private int day;
	private int occourancesNum;
	private int numOfSuccessOccurances;
	public CandleDaySuccessStatistics(int day, int occourancesNum,
			int numOfSuccessOccurances) {
		super();
		this.day = day;
		this.occourancesNum = occourancesNum;
		this.numOfSuccessOccurances = numOfSuccessOccurances;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
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
