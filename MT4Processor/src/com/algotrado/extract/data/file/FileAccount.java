package com.algotrado.extract.data.file;

import com.algotrado.broker.Account;

public class FileAccount implements Account {

	private double balance;
	private double equity;
	private double margin;
	private double leverage;
	private double marginCallLevel;
	private double stopOutLevel;
	

	public FileAccount(double balance, double leverage, double marginCallLevel, double stopOutLevel) {
		super();
		this.balance = balance;
		this.equity = balance;
		this.leverage = leverage;
		this.marginCallLevel = marginCallLevel;
		this.stopOutLevel = stopOutLevel;
	}

	@Override
	public double getBalance() {
		return balance;
	}

	@Override
	public double getEquity() {
		return equity;
	}

	@Override
	public double getMargin() {
		return margin;
	}

	@Override
	public double getFreeMargin() {
		return equity - margin;
	}

	@Override
	public double getMarginLevel() {
		return (margin == 0) ? Double.MAX_VALUE : 100*equity/margin;
	}

	public boolean weCanEnter(double wantedPrice) {
		double requiredMargin = wantedPrice/leverage;
		if(100*equity/(requiredMargin+margin) > marginCallLevel)
		{
			return true;
		}
		return false;
	}
	
	public double setNewTrade(double wantedPrice,double currentProfit)
	{
		double requiredMargin = wantedPrice/leverage;
		margin += requiredMargin;
		this.equity += currentProfit;
		return requiredMargin;
	}
	
	public void setCurrentProfitChange(double currentProfitChange)
	{
		this.equity += currentProfitChange;
		if(currentProfitChange < 0)
		{
			if(getMarginLevel() <= stopOutLevel)
			{
				// TODO - need to start exit from trades!!!!
			}
		}
	}
	
	public void updateBalanceAndMargin(double profit,double marginToFree)
	{
		margin -= marginToFree;
		balance += profit;
	}
}
