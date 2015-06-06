package com.algotrado.extract.data.file;

import java.util.Date;

import com.algotrado.extract.data.AssetType;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionOrderStatusType;

public class FileTrade {
	FileAccount fileAccount;
	AssetType assetType;
	double firstAmount;
	double amount;
	double entryPrice;
	Date time;
	double stopLoss;
	double takeProfit;
	PositionDirectionType direction;
	double spread;
	double currentPrice;
	double firstMargin;
	double oldProfit;
	boolean internalClose;
	
	FileTradeStatus status;
	public FileTrade(FileAccount fileAccount, AssetType assetType,
			double amount, double assetPrice, Date time, double stopLoss,
			double takeProfit, PositionDirectionType direction, double spread) {
		super();
		oldProfit = 0;
		internalClose = false;
		this.fileAccount = fileAccount;
		this.assetType = assetType;
		this.amount = amount;
		this.firstAmount = amount;
		this.time = time;
		this.stopLoss = stopLoss;
		this.takeProfit = takeProfit;
		this.direction = direction;
		this.spread = spread;
		if(direction == PositionDirectionType.LONG)
		{
			entryPrice = assetPrice;
			currentPrice = assetPrice-spread;
			if((stopLoss != 0) && (stopLoss >= currentPrice))
			{
				throw new RuntimeException("Error in stop loss!!!");
			}
			if((takeProfit != 0) && (takeProfit <= currentPrice))
			{
				throw new RuntimeException("Error in take profit!!!");
			}
		}
		else
		{
			entryPrice = assetPrice - spread;
			currentPrice = assetPrice;
			if((stopLoss != 0) && (stopLoss <= currentPrice))
			{
				throw new RuntimeException("Error in stop loss!!!");
			}
			if((takeProfit != 0) && (takeProfit >= currentPrice))
			{
				throw new RuntimeException("Error in take profit!!!");
			}
		}
		firstMargin = fileAccount.setNewTrade(entryPrice*amount,-spread*amount);
		status = FileTradeStatus.OPEN;
		
	}
	
	public double getTradeProfit()
	{
		double currentProfit = (currentPrice - entryPrice) * amount;
		if(direction == PositionDirectionType.SHORT)
		{
			currentProfit = -currentProfit;
		}
		return oldProfit + currentProfit;
	}
	
	public FileTradeStatus getStatus()
	{
		return status;
	}
	
	public AssetType getAssetType()
	{
		return assetType;
	}
	
	public void setAmount(double amountToClose)
	{
		if((status.getPositionOrderStatusType() == PositionOrderStatusType.CLOSED) && (internalClose == false))
		{
			throw new RuntimeException("trade all reday closed!!!");
		}
		internalClose = false;
		if(amountToClose > amount)
		{
			throw new RuntimeException("the amount in this trade is smoler from the amountToClose");
		}
		double marginToFree = firstMargin * amountToClose / firstAmount;
		double currentProfit = (currentPrice - entryPrice) * amountToClose;
		if(direction == PositionDirectionType.SHORT)
		{
			currentProfit = -currentProfit;
		}
		oldProfit += currentProfit;
		fileAccount.updateBalanceAndMargin(currentProfit,marginToFree);
		
		amount -= amountToClose;
		if((amount == 0) && (status == FileTradeStatus.OPEN))
		{
			status = FileTradeStatus.CLOSE_BY_USER;
		}
	}
	
	public void modifyPosition(double newStopLoss,double newTakeProfit)
	{
		if(status.getPositionOrderStatusType() == PositionOrderStatusType.CLOSED)
		{
			throw new RuntimeException("trade all reday closed!!!");
		}
		if(direction == PositionDirectionType.LONG)
		{
			if((newStopLoss != 0) && (newStopLoss >= currentPrice))
			{
				throw new RuntimeException("Error in stop loss!!!");
			}
			stopLoss = newStopLoss;
			if((newTakeProfit != 0) && (newTakeProfit <= currentPrice))
			{
				throw new RuntimeException("Error in tack profit!!!");
			}
			takeProfit = newTakeProfit;
		}
		else
		{
			if((newStopLoss != 0) && (newStopLoss <= currentPrice))
			{
				throw new RuntimeException("Error in stop loss!!!");
			}
			stopLoss = newStopLoss;
			if((newTakeProfit != 0) && (newTakeProfit >= currentPrice))
			{
				throw new RuntimeException("Error in tack profit!!!");
			}
			takeProfit = newTakeProfit;			
		}
	}

	public double getAmount() {
		return amount;
	}

	public double getEntryPrice() {
		return entryPrice;
	}

	public Date getTime() {
		return time;
	}

	public double getStopLoss() {
		return stopLoss;
	}

	public double getTakeProfit() {
		return takeProfit;
	}

	public PositionDirectionType getDirection() {
		return direction;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}
	
	public void updatePriceTrade(double assetPrice,double spread)
	{
		if(status.getPositionOrderStatusType() == PositionOrderStatusType.CLOSED)
		{
			throw new RuntimeException("trade already closed!!!");
		}
		double prevCurrentPrice = currentPrice;
		boolean needToCloseTrade = false;
		if(direction == PositionDirectionType.LONG)
		{
			currentPrice = assetPrice - spread;
			if((stopLoss != 0) && (stopLoss >= currentPrice))
			{
				currentPrice = stopLoss; // exit in the stop loss
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_STOP_LOSS;
				internalClose = true;
			}
			else if((takeProfit != 0) && (takeProfit <= currentPrice))
			{
				currentPrice = takeProfit; // exit in the tack profit
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_TAKE_PROFIT;
				internalClose = true;
			}
			fileAccount.setCurrentProfitChange((currentPrice - prevCurrentPrice)*amount);
		}
		else
		{
			currentPrice = assetPrice;
			if((stopLoss != 0) && (stopLoss <= currentPrice))
			{
				currentPrice = stopLoss; // exit in the stop loss
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_STOP_LOSS;
				internalClose = true;
			}
			else if((takeProfit != 0) && (takeProfit >= currentPrice))
			{
				currentPrice = takeProfit; // exit in the tack profit
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_TAKE_PROFIT;
				internalClose = true;
			}
			fileAccount.setCurrentProfitChange(-(currentPrice - prevCurrentPrice)*amount);
		}
		if(needToCloseTrade)
		{
			closePosition();
		}
	}

	public void closePosition() {
		if((status.getPositionOrderStatusType() == PositionOrderStatusType.CLOSED) && (internalClose == false))
		{
			throw new RuntimeException("trade all reday closed!!!");
		}
		setAmount(amount);
	}
	
}
