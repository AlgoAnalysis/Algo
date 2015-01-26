package com.algotrado.mt4.tal.patterns.continuos;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.Candle;
import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class MatHoldPattern extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 2) {
			return false;
		}
		boolean isBullishLastCandle = Candle.isBullishCandle(previousCandles[index]);
		boolean isBigBodyLastCandle = Candle.isBigBodySmallThreadsCandle(previousCandles[index]);
		
		double highestHighExceptLastCandle = previousCandles[index - 1].getHigh();
		int firstIndexOfCandle = index;
		boolean firstCandleBullish = false;
		double maxBodySize = 0, maxCandleSize = 0;
		int minNumOfCandles = 0;
		for (int i = index - 1; i >= 0; i--) {
			firstIndexOfCandle = i;
			firstCandleBullish = Candle.isBullishCandle(previousCandles[firstIndexOfCandle]);
			boolean currCandleBearish = Candle.isBearishCandle(previousCandles[firstIndexOfCandle]);
			minNumOfCandles++;
			
			if (highestHighExceptLastCandle < previousCandles[firstIndexOfCandle].getHigh()) {
				highestHighExceptLastCandle = previousCandles[firstIndexOfCandle].getHigh();
			}
			
			if (firstCandleBullish && minNumOfCandles > 3) {//must have at least three smaller candles.
				break;
			} else if (!currCandleBearish) {
				return false;
			}
			
			double currCandleBody = Candle.getBodySize(previousCandles[firstIndexOfCandle]);
			if (currCandleBody > maxBodySize) {
				maxBodySize = currCandleBody;
			}
			
			double currCandleSize = Candle.getCandleSize(previousCandles[firstIndexOfCandle]);
			if (currCandleSize > maxCandleSize) {
				maxCandleSize = currCandleSize;
			}
			
		}
		
		if (firstIndexOfCandle == index) {
			return false;
		}
		
		double firstCandleBody = Candle.getBodySize(previousCandles[firstIndexOfCandle]);
		boolean isBigBodySmallThreadsInFirstCandle = Candle.isBigBodySmallThreadsCandle(previousCandles[firstIndexOfCandle]);
		
		boolean isSecondCandleGapFromFirstCandle = 
				previousCandles[firstIndexOfCandle].getClose() < previousCandles[firstIndexOfCandle + 1].getOpen() && 
				previousCandles[firstIndexOfCandle].getClose() < previousCandles[firstIndexOfCandle + 1].getClose();
		
		if ( (maxBodySize >= 0.8 * firstCandleBody) || 
				(maxCandleSize >= 0.6 * Candle.getCandleSize(previousCandles[firstIndexOfCandle])) ||
				(maxCandleSize >= 0.6 * Candle.getCandleSize(previousCandles[index])) ||
				(previousCandles[firstIndexOfCandle].getLow() > previousCandles[index].getLow()) ) {
			return false;
		}
		
		if (highestHighExceptLastCandle >= previousCandles[index].getClose()) {
			return false;
		}
		
		return isBigBodySmallThreadsInFirstCandle && firstCandleBullish && 
				isSecondCandleGapFromFirstCandle && isBigBodyLastCandle && isBullishLastCandle;
		
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 2) {
			return false;
		}
		boolean isBearishLastCandle = Candle.isBearishCandle(previousCandles[index]);
		boolean isBigBodyLastCandle = Candle.isBigBodySmallThreadsCandle(previousCandles[index]);
		
		double lowestLowExceptLastCandle = previousCandles[index - 1].getLow();
		
		int firstIndexOfCandle = index;
		boolean firstCandleBearish = false;
		
		double maxBodySize = 0, maxCandleSize = 0;
		int minNumOfCandles = 0;
		
		for (int i = index - 1; i >= 0; i--) {
			firstIndexOfCandle = i;
			firstCandleBearish = Candle.isBearishCandle(previousCandles[firstIndexOfCandle]);
			boolean currCandleBullish = Candle.isBullishCandle(previousCandles[firstIndexOfCandle]);
			minNumOfCandles++;
			
			if (lowestLowExceptLastCandle > previousCandles[firstIndexOfCandle].getLow()) {
				lowestLowExceptLastCandle = previousCandles[firstIndexOfCandle].getLow();
			}
			
			if (firstCandleBearish && minNumOfCandles > 3) {//must have at least three smaller candles.
				break;
			} else if (!currCandleBullish) {
				return false;
			}
			
			double currCandleBody = Candle.getBodySize(previousCandles[firstIndexOfCandle]);
			if (currCandleBody > maxBodySize) {
				maxBodySize = currCandleBody;
			}
			
			double currCandleSize = Candle.getCandleSize(previousCandles[firstIndexOfCandle]);
			if (currCandleSize > maxCandleSize) {
				maxCandleSize = currCandleSize;
			}
			
		}
		
		if (firstIndexOfCandle == index) {
			return false;
		}
		
		double firstCandleBody = Candle.getBodySize(previousCandles[firstIndexOfCandle]);
		boolean isBigBodySmallThreadsInFirstCandle = Candle.isBigBodySmallThreadsCandle(previousCandles[firstIndexOfCandle]);
		
		boolean isSecondCandleGapFromFirstCandle = 
				previousCandles[firstIndexOfCandle].getClose() > previousCandles[firstIndexOfCandle + 1].getOpen() && 
				previousCandles[firstIndexOfCandle].getClose() > previousCandles[firstIndexOfCandle + 1].getClose();
		
		
		if ( (maxBodySize >= 0.8 * firstCandleBody) || 
				(maxCandleSize >= 0.6 * Candle.getCandleSize(previousCandles[firstIndexOfCandle])) ||
				//(maxCandleSize >= 0.6 * Candle.getCandleSize(previousCandles[index])) ||
				(previousCandles[firstIndexOfCandle].getHigh() < previousCandles[index].getHigh()) ) {
			return false;
		}
		
		if (lowestLowExceptLastCandle <= previousCandles[index].getClose()) {
			return false;
		}
		
			
		return isBigBodySmallThreadsInFirstCandle && firstCandleBearish && 
				isSecondCandleGapFromFirstCandle && isBigBodyLastCandle && isBearishLastCandle;
	}
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
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
