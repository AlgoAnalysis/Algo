package com.algotrado.mt4.tal.patterns.continuos;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class WhiteSoldiers extends Pattern {

	/**
	 * White Soldiers Pattern
	 */
	@Override
	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		boolean areLegalCandles = true;
		for (int i = index; i >= index - 2; i--) {
			if(areLegalCandles) {
				boolean isBullishCandle = previousCandles[i].getClose() > previousCandles[i].getOpen();
				double candleBody = Math.abs(previousCandles[i].getClose() - previousCandles[i].getOpen());
				double candleThreads = Math.abs(previousCandles[i].getHigh() - previousCandles[i].getLow()) - candleBody;
				boolean isBigBodySmallThreadsInCandle = candleThreads < candleBody;
				
				boolean isLowerThreadBiggerThanUpperThread = ((previousCandles[i].getOpen() - previousCandles[i].getLow()) > (previousCandles[i].getHigh() - previousCandles[i].getClose())) ||
						((previousCandles[i].getOpen() - previousCandles[i].getLow()) == 0 && (previousCandles[i].getHigh() - previousCandles[i].getClose()) == 0);
				
				areLegalCandles = isBullishCandle && isBigBodySmallThreadsInCandle && isLowerThreadBiggerThanUpperThread;
			} else break;
		}
		
		boolean isSecondCandleOpenBelowOrEqualToPreviousCandleClose = false;
		boolean isThirdCandleOpenBelowOrEqualToPreviousCandleClose = false;
		boolean isSecondCandleCloseAboveFirstCandleClose = false;
		boolean isThirdCandleCloseAboveSecondCandleClose = false;
		
		if (areLegalCandles) {
			isSecondCandleOpenBelowOrEqualToPreviousCandleClose = 
					((previousCandles[index - 1].getOpen() < previousCandles[index - 2].getClose()) && 
							(previousCandles[index - 1].getOpen() > previousCandles[index - 2].getOpen()) &&
							(previousCandles[index - 1].getOpen() - previousCandles[index - 2].getOpen() >= previousCandles[index - 2].getClose() - previousCandles[index - 1].getOpen())) || 
					(Math.abs(previousCandles[index - 1].getOpen() - previousCandles[index - 2].getClose()) <= 2 * pipsValue);
		
		
			isThirdCandleOpenBelowOrEqualToPreviousCandleClose = 
					((previousCandles[index].getOpen() < previousCandles[index - 1].getClose()) && 
							(previousCandles[index].getOpen() > previousCandles[index - 1].getOpen()) &&
							(previousCandles[index].getOpen() - previousCandles[index - 1].getOpen() >= previousCandles[index - 1].getClose() - previousCandles[index].getOpen())) || 
					(Math.abs(previousCandles[index].getOpen() - previousCandles[index - 1].getClose()) <= 2 * pipsValue);
			
			isSecondCandleCloseAboveFirstCandleClose = previousCandles[index - 1].getClose() > previousCandles[index - 2].getClose();
			isThirdCandleCloseAboveSecondCandleClose = previousCandles[index].getClose() > previousCandles[index - 1].getClose();
		}
		
		return !isAbsoluteGAPBetween2Candles(previousCandles[index], previousCandles[index - 1]) && 
				!isAbsoluteGAPBetween2Candles(previousCandles[index - 2], previousCandles[index - 1]) && areLegalCandles && 
				isThirdCandleOpenBelowOrEqualToPreviousCandleClose && isSecondCandleOpenBelowOrEqualToPreviousCandleClose && 
				isSecondCandleCloseAboveFirstCandleClose && isThirdCandleCloseAboveSecondCandleClose;
	}

	/**
	 * Black Crows Pattern
	 */
	@Override
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		boolean areLegalCandles = true;
		for (int i = index; i >= index - 2; i--) {
			if(areLegalCandles) {
				boolean isBearishCandle = previousCandles[i].getClose() < previousCandles[i].getOpen();
				double candleBody = Math.abs(previousCandles[i].getClose() - previousCandles[i].getOpen());
				double candleThreads = Math.abs(previousCandles[i].getHigh() - previousCandles[i].getLow()) - candleBody;
				boolean isBigBodySmallThreadsInCandle = candleThreads < candleBody;
				
				boolean isUpperThreadBiggerThanLowerThread = ((previousCandles[i].getHigh() - previousCandles[i].getOpen()) > (previousCandles[i].getClose() - previousCandles[i].getLow()))||
						((previousCandles[i].getHigh() - previousCandles[i].getOpen()) == 0 && (previousCandles[i].getClose() - previousCandles[i].getLow()) == 0);
				
				areLegalCandles = isBearishCandle && isBigBodySmallThreadsInCandle && isUpperThreadBiggerThanLowerThread;
			} else break;
		}
		
		boolean isSecondCandleOpenAboveOrEqualToPreviousCandleClose = false;
		boolean isThirdCandleOpenAboveOrEqualToPreviousCandleClose = false;
		boolean isSecondCandleCloseLowerThenFirstCandleClose = false;
		boolean isThirdCandleCloseLowerThenSecondCandleClose = false;
		
		if (areLegalCandles) {
			
			isSecondCandleOpenAboveOrEqualToPreviousCandleClose = 
						((previousCandles[index - 1].getOpen() > previousCandles[index - 2].getClose()) && 
								(previousCandles[index - 1].getOpen() < previousCandles[index - 2].getOpen()) &&
								(previousCandles[index - 2].getOpen() - previousCandles[index - 1].getOpen() >= previousCandles[index - 1].getOpen() - previousCandles[index - 2].getClose())) || 
						(Math.abs(previousCandles[index - 1].getOpen() - previousCandles[index - 2].getClose()) <= 2 * pipsValue);
			
			
			isThirdCandleOpenAboveOrEqualToPreviousCandleClose = 
					((previousCandles[index].getOpen() > previousCandles[index - 1].getClose()) && 
							(previousCandles[index].getOpen() < previousCandles[index - 1].getOpen()) &&
							(previousCandles[index - 1].getOpen() - previousCandles[index].getOpen() >= previousCandles[index].getOpen() - previousCandles[index - 1].getClose())) || 
					(Math.abs(previousCandles[index].getOpen() - previousCandles[index - 1].getClose()) <= 2 * pipsValue);
			
			isSecondCandleCloseLowerThenFirstCandleClose = previousCandles[index - 1].getClose() < previousCandles[index - 2].getClose();
			isThirdCandleCloseLowerThenSecondCandleClose = previousCandles[index].getClose() < previousCandles[index - 1].getClose();
		}
		
		
		
		return !isAbsoluteGAPBetween2Candles(previousCandles[index], previousCandles[index - 1]) && 
				!isAbsoluteGAPBetween2Candles(previousCandles[index - 2], previousCandles[index - 1]) && areLegalCandles && 
				isThirdCandleOpenAboveOrEqualToPreviousCandleClose && isSecondCandleOpenAboveOrEqualToPreviousCandleClose && 
				isSecondCandleCloseLowerThenFirstCandleClose && isThirdCandleCloseLowerThenSecondCandleClose;
	}
	
	/*@Override
	public double getRisk(SingleCandleBarData[] previousCandles, int index,
			double pipsValue) {
		return -1;
	}*/
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			bars.add(previousCandles[index - 2]);
			return PriceUtil.getMaxHigh(bars);
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			bars.add(previousCandles[index - 2]);
			return PriceUtil.getMinLow(bars);
		}
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}

}
