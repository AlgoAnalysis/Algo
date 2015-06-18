package com.algotrado.exit.strategy.EXT_0004;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.indicator.IND_0003.ZigZagUpdateData;
import com.algotrado.exit.strategy.ExitStrategyStatus;
import com.algotrado.exit.strategy.IExitStrategy;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.TradeManager;

public class EXT_0004 extends IExitStrategy {

	private PositionDirectionType positionDirectionType;
	private double firstLocalExtremumPointOfPrice;
	private double secondLocalExtremumPointOfPrice;
	private double currentPrice;
	
	private double zigzagMultiplier;
	
	
	public EXT_0004(PositionDirectionType positionDirectionType) {
		super();
		this.positionDirectionType = positionDirectionType;
		this.zigzagMultiplier = (positionDirectionType == PositionDirectionType.LONG) ? 1.0 : (-1.0);
		init();
	}

	private void init() {
		this.firstLocalExtremumPointOfPrice = -10000;
		this.secondLocalExtremumPointOfPrice = -10000;
	}

	public PositionDirectionType getPositionDirectionType() {
		return positionDirectionType;
	}

	public void setPositionDirectionType(PositionDirectionType positionDirectionType) {
		this.positionDirectionType = positionDirectionType;
	}

	public double getPrevLocalExtremumPointOfPrice() {
		return firstLocalExtremumPointOfPrice;
	}

	public void setPrevLocalExtremumPointOfPrice(
			double prevLocalExtremumPointOfPrice) {
		this.firstLocalExtremumPointOfPrice = prevLocalExtremumPointOfPrice;
	}

	public double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(double currentPrice) {
		this.currentPrice = currentPrice;
	}

	@Override
	public void setNewData(NewUpdateData[] newData) {
//		currentPrice = ((JapaneseCandleBar)newData[0]).getClose(); 
		this.exitStrategyStatus = ExitStrategyStatus.RUN;
		for (int i = 0; i < newData.length; i++) {
			if (i > 0 && (newData[i] instanceof ZigZagUpdateData)) {
				if (firstLocalExtremumPointOfPrice < 0) {// first zigzag assumed to be high/low according to direction
					firstLocalExtremumPointOfPrice = ((ZigZagUpdateData)newData[i]).getValue();
				} else {// Next zigzag fixes assumption if was wrong
					double prevLocalExtremumPointOfPriceForCalc = zigzagMultiplier * firstLocalExtremumPointOfPrice;
					double currLocalExtremumPointOfPrice = zigzagMultiplier * ((ZigZagUpdateData)newData[i]).getValue();
					firstLocalExtremumPointOfPrice = 
							(currLocalExtremumPointOfPrice > prevLocalExtremumPointOfPriceForCalc) ? 
									Math.abs(currLocalExtremumPointOfPrice) : firstLocalExtremumPointOfPrice;
				}
				// second zigzag low/high (contrary to previous)
				if (secondLocalExtremumPointOfPrice < 0 && firstLocalExtremumPointOfPrice > 0 &&
						(zigzagMultiplier * firstLocalExtremumPointOfPrice) > 
							(zigzagMultiplier * ((ZigZagUpdateData)newData[i]).getValue()) ) {
					secondLocalExtremumPointOfPrice = ((ZigZagUpdateData)newData[i]).getValue();
				}
				
			}
			// Identify price breach.
			if (secondLocalExtremumPointOfPrice > 0 && firstLocalExtremumPointOfPrice > 0 && i == 1 &&
					(newData[i] instanceof JapaneseCandleBar)) {
				currentPrice = ((JapaneseCandleBar)newData[i]).getClose(); 
				if ((zigzagMultiplier * currentPrice) >  
					(zigzagMultiplier * firstLocalExtremumPointOfPrice)) {// Close Price crossed previous high/low.
					this.exitStrategyStatus = ExitStrategyStatus.MOVE_STOP_LOSS;
					setNewStopLoss(secondLocalExtremumPointOfPrice);// set stop loss to second extremum point.
					init();
				}
			}
		}

	}

	@Override
	public void forceTrigger() {
		this.exitStrategyStatus = ExitStrategyStatus.TRIGGER;
	}

	@Override
	public int getStrategyIndex() {
		return TradeManager.EXIT_0004;
	}

}
