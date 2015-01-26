package com.algotrado.mt4.tal.patterns.continuos;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class RisingOrFallingThreePattern extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 2) {
			return false;
		}
		boolean isBullishLastCandle = previousCandles[index].getClose() > previousCandles[index].getOpen();
		
		
		
		
		int firstIndexOfCandle = index;
		boolean firstCandleBullish = false;
		
		double minPriceRange = previousCandles[index].getHigh();
		double maxPriceRange = previousCandles[index].getLow();
		double maxBodySize = 0;
		int minNumOfCandles = 0;
		
		for (int i = index - 1; i >= 0; i--) {
			firstIndexOfCandle = i;
			firstCandleBullish = previousCandles[firstIndexOfCandle].getOpen() < previousCandles[firstIndexOfCandle].getClose();
			boolean currCandleBearish = previousCandles[firstIndexOfCandle].getOpen() > previousCandles[firstIndexOfCandle].getClose();
			minNumOfCandles++;
			if (firstCandleBullish && minNumOfCandles > 3) {//must have at least three smaller candles.
				break;
			} else if (!currCandleBearish) {
				return false;
			}
			
			if (previousCandles[firstIndexOfCandle].getHigh() > previousCandles[index].getHigh() || 
					previousCandles[firstIndexOfCandle].getLow() < previousCandles[index].getLow()) {
				return false;
			}
			
			double currCandleBody = previousCandles[firstIndexOfCandle].getOpen() - previousCandles[firstIndexOfCandle].getClose();
			if (currCandleBody > maxBodySize) {
				maxBodySize = currCandleBody;
			}
			
			if (minPriceRange > previousCandles[firstIndexOfCandle].getLow()) {
				minPriceRange = previousCandles[firstIndexOfCandle].getLow();
			}
			
			if (maxPriceRange < previousCandles[firstIndexOfCandle].getHigh()) {
				maxPriceRange = previousCandles[firstIndexOfCandle].getHigh();
			}
			
		}
		
		double candleBody = Math.abs(previousCandles[firstIndexOfCandle].getClose() - previousCandles[firstIndexOfCandle].getOpen());
		double candleThreads = previousCandles[firstIndexOfCandle].getHigh() - previousCandles[firstIndexOfCandle].getLow() - candleBody;
		boolean isBigBodySmallThreadsInFirstCandle = candleThreads <= candleBody;
		
		
		if (maxBodySize >= 0.8 * candleBody) {
			return false;
		}
		
		if (firstIndexOfCandle == index || previousCandles[firstIndexOfCandle + 1].getOpen() > previousCandles[firstIndexOfCandle].getClose()) {
			return false;
		}
			
		return isBullishLastCandle && isBigBodySmallThreadsInFirstCandle && firstCandleBullish &&
				(previousCandles[index].getOpen() > previousCandles[index - 1].getClose())
				&& (previousCandles[index].getClose() > previousCandles[firstIndexOfCandle].getClose()) &&
				(minPriceRange >= previousCandles[firstIndexOfCandle].getLow() && 
				maxPriceRange <= previousCandles[firstIndexOfCandle].getHigh());
		
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 5 || previousCandles.length <= index || index < 2) {
			return false;
		}
		boolean isBearishLastCandle = previousCandles[index].getClose() < previousCandles[index].getOpen();
		
		int firstIndexOfCandle = index;
		boolean firstCandleBearish = false;
		
		double minPriceRange = previousCandles[index].getHigh();
		double maxPriceRange = previousCandles[index].getLow();
		double maxBodySize = 0;
		int minNumOfCandles = 0;
		
		for (int i = index - 1; i >= 0; i--) {
			firstIndexOfCandle = i;
			firstCandleBearish = previousCandles[firstIndexOfCandle].getOpen() > previousCandles[firstIndexOfCandle].getClose();
			boolean currCandleBullish = previousCandles[firstIndexOfCandle].getOpen() < previousCandles[firstIndexOfCandle].getClose();
			minNumOfCandles++;
			if (firstCandleBearish && minNumOfCandles > 3) {//must have at least three smaller candles.
				break;
			} else if (!currCandleBullish) {
				return false;
			}
			
			if (previousCandles[firstIndexOfCandle].getHigh() > previousCandles[index].getHigh() || 
					previousCandles[firstIndexOfCandle].getLow() < previousCandles[index].getLow()) {
				return false;
			}
			
			double currCandleBody = previousCandles[firstIndexOfCandle].getClose() - previousCandles[firstIndexOfCandle].getOpen();
			if (currCandleBody > maxBodySize) {
				maxBodySize = currCandleBody;
			}
			
			if (minPriceRange > previousCandles[firstIndexOfCandle].getLow()) {
				minPriceRange = previousCandles[firstIndexOfCandle].getLow();
			}
			
			if (maxPriceRange < previousCandles[firstIndexOfCandle].getHigh()) {
				maxPriceRange = previousCandles[firstIndexOfCandle].getHigh();
			}
			
		}
		
		double candleBody = Math.abs(previousCandles[firstIndexOfCandle].getClose() - previousCandles[firstIndexOfCandle].getOpen());
		double candleThreads = previousCandles[firstIndexOfCandle].getHigh() - previousCandles[firstIndexOfCandle].getLow() - candleBody;
		boolean isBigBodySmallThreadsInFirstCandle = candleThreads <= candleBody;
		
		
		if (maxBodySize >= 0.8 * candleBody) {
			return false;
		}
		
		if (firstIndexOfCandle == index || previousCandles[firstIndexOfCandle + 1].getOpen() < previousCandles[firstIndexOfCandle].getClose()) {
			return false;
		}
			
		return isBearishLastCandle && isBigBodySmallThreadsInFirstCandle && firstCandleBearish &&
				(previousCandles[index].getOpen() < previousCandles[index - 1].getClose())
				&& (previousCandles[index].getClose() < previousCandles[firstIndexOfCandle].getClose()) &&
				(minPriceRange >= previousCandles[firstIndexOfCandle].getLow() && 
				maxPriceRange <= previousCandles[firstIndexOfCandle].getHigh());
		
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
