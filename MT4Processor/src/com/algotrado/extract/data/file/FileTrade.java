package com.algotrado.extract.data.file;

import java.util.Date;

import com.algotrado.extract.data.AssetType;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionOrderStatusType;

public class FileTrade {
	private FileAccount fileAccount;
	private AssetType assetType;
	private double firstAmount;
	private double amount;
	private double entryPrice;
	private Date startTime;
	private Date closeTime;
	private double stopLoss;
	private double takeProfit;
	private PositionDirectionType direction;
	private double spread;
	private double currentPrice;
	private double firstMargin;
	private double oldProfit;
	private boolean internalClose;
	
	// for record only
	private double startBalance;
	private double startEquity;
	private double startMargin;
	private Integer positionId;
	private double firstStopLoss;
	
	FileTradeStatus status;
	public FileTrade(Integer positionId,FileAccount fileAccount, AssetType assetType,
			double amount, double assetPrice, Date startTime, double stopLoss,
			double takeProfit, PositionDirectionType direction, double spread) {
		super();
		oldProfit = 0;
		internalClose = false;
		this.fileAccount = fileAccount;
		this.assetType = assetType;
		this.amount = amount;
		this.firstAmount = amount;
		this.startTime = startTime;
		this.stopLoss = stopLoss;
		this.takeProfit = takeProfit;
		this.direction = direction;
		this.spread = spread;
		this.positionId = positionId;
		
		this.startBalance = fileAccount.getBalance();
		this.startEquity = fileAccount.getEquity();
		this.startMargin = fileAccount.getMargin();
		this.firstStopLoss = stopLoss;
		this.closeTime = new Date((long)0);
		
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
	
	public void setAmount(double amountToClose,Date curentTime)
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
			closeTime = curentTime;
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
			if((newStopLoss != 0) && (newStopLoss >= (currentPrice + spread)))
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

	public Date getStartTime() {
		return startTime;
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
	
	public void updatePriceTrade(double assetPrice,double spread,Date curentTime)
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
				closeTime = curentTime;
				internalClose = true;
			}
			else if((takeProfit != 0) && (takeProfit <= currentPrice))
			{
				currentPrice = takeProfit; // exit in the tack profit
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_TAKE_PROFIT;
				closeTime = curentTime;
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
				closeTime = curentTime;
				internalClose = true;
			}
			else if((takeProfit != 0) && (takeProfit >= currentPrice))
			{
				currentPrice = takeProfit; // exit in the tack profit
				needToCloseTrade = true;
				status = FileTradeStatus.CLOSE_BY_TAKE_PROFIT;
				closeTime = curentTime;
				internalClose = true;
			}
			fileAccount.setCurrentProfitChange(-(currentPrice - prevCurrentPrice)*amount);
		}
		if(needToCloseTrade)
		{
			closePosition(curentTime);
		}
	}

	public void closePosition(Date curentTime) {
		if((status.getPositionOrderStatusType() == PositionOrderStatusType.CLOSED) && (internalClose == false))
		{
			throw new RuntimeException("trade all reday closed!!!");
		}
		setAmount(amount,curentTime);
	}

	public double getStartBalance() {
		return startBalance;
	}

	public double getStartEquity() {
		return startEquity;
	}

	public double getStartMargin() {
		return startMargin;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public double getFirstAmount() {
		return firstAmount;
	}

	public double getFirstStopLoss() {
		return firstStopLoss;
	}

	public Date getCloseTime() {
		return closeTime;
	}
	
	
	
}
