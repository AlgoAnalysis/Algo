package com.algotrado.mt4.tal.patterns.reversal;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class HaramiPattern extends Pattern {
	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 3 && previousCandles.length > index + 1) {
			return false;
		}
		
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		if ((previousCandles[index - 1].getLower20Bollinger() > previousCandles[index - 1].getLow() &&
				previousCandles[index - 1].getLower20Bollinger() < previousCandles[index - 1].getHigh())
				|| (previousCandles[index - 1].getLower10Bollinger() > previousCandles[index - 1].getLow() &&
						previousCandles[index - 1].getLower10Bollinger() < previousCandles[index - 1].getHigh())) {
			
			boolean curCandleInPrevCandleBody = (previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen()) ?
					(previousCandles[index - 1].getClose() >= previousCandles[index].getHigh() && previousCandles[index - 1].getOpen() <= previousCandles[index].getLow()) :
						(previousCandles[index - 1].getClose() <= previousCandles[index].getLow() && previousCandles[index - 1].getOpen() >= previousCandles[index].getHigh());
			
					
			boolean isHaramiPattern = false;
			boolean didPatternGetApproved = false;
			boolean didCreateNewLow = previousCandles[index - 1].getLow() < previousCandles[index - 2].getLow();
			
			if (curCandleInPrevCandleBody && didCreateNewLow) {		
				boolean upperThreadFiftyPercentBiggerThenLowerThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) >= 1.5 * (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) >= 1.5 * (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean upperThreadThirtyPercentBiggerThenLowerThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) >= 1.3 * (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) >= 1.3 * (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean upperThreadTwiceThanBody = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) >= 2.0 * (previousCandles[index].getClose() - previousCandles[index].getOpen()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) >= 2.0 * (previousCandles[index].getOpen() - previousCandles[index].getClose());
				boolean LowerThreadBiggerOrEqualsThanUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) <= (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) <= (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean body20PercentMoreThenUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) * 1.2 <= (previousCandles[index].getClose() - previousCandles[index].getOpen()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) * 1.2 <= (previousCandles[index].getOpen() - previousCandles[index].getClose());;
				if (upperThreadFiftyPercentBiggerThenLowerThread  && upperThreadTwiceThanBody) {//second candle.
					isHaramiPattern = false;
				} else if (upperThreadFiftyPercentBiggerThenLowerThread && body20PercentMoreThenUpperThread) {
					isHaramiPattern = true;
				} else if(upperThreadTwiceThanBody && LowerThreadBiggerOrEqualsThanUpperThread) {
					isHaramiPattern = true;
				} else isHaramiPattern = !(upperThreadFiftyPercentBiggerThenLowerThread || upperThreadTwiceThanBody && upperThreadThirtyPercentBiggerThenLowerThread);
			
			
			
				for (int i = 1; i < (previousCandles.length - index); i++) {
					if (previousCandles[index + i].getHigh() > previousCandles[index - 1].getHigh()) {
						didPatternGetApproved = true;
						break;
					} else if (previousCandles[index + i].getLow() < previousCandles[index - 1].getLow()) {
	//					didPatternGetApproved = false;
						break;
					}
				}
			}
			
			return isHaramiPattern && didPatternGetApproved;
		}
		return false;
	}
	
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (index <= 3 && previousCandles.length > index + 1) {
			return false;
		}
		
		boolean curCandleInPrevCandleBody = (previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen()) ?
				(previousCandles[index - 1].getClose() >= previousCandles[index].getHigh() && previousCandles[index - 1].getOpen() <= previousCandles[index].getLow()) :
					(previousCandles[index - 1].getClose() <= previousCandles[index].getLow() && previousCandles[index - 1].getOpen() >= previousCandles[index].getHigh());
		
		boolean didCreateNewHigh = previousCandles[index - 1].getHigh() > previousCandles[index - 2].getHigh();
				
		/**
		 * Check that first candle penetrates Bollinger band.
		 */
		boolean isHaramiPattern = false;
		boolean didPatternGetApproved = false;
		if (curCandleInPrevCandleBody && didCreateNewHigh) {
			if ((previousCandles[index - 1].getHigher20Bollinger() > previousCandles[index - 1].getLow() &&
					previousCandles[index - 1].getHigher20Bollinger() < previousCandles[index - 1].getHigh())
					|| (previousCandles[index - 1].getHigher10Bollinger() > previousCandles[index - 1].getLow() &&
							previousCandles[index - 1].getHigher10Bollinger() < previousCandles[index - 1].getHigh())) {
				boolean lowerThreadFiftyPercentBiggerThenUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						1.5 * (previousCandles[index].getHigh() - previousCandles[index].getClose()) <= (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
							1.5 * (previousCandles[index].getHigh() - previousCandles[index].getOpen()) <= (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean lowerThreadThirtyPercentBiggerThenUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						1.3 * (previousCandles[index].getHigh() - previousCandles[index].getClose()) <= (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
							1.3 * (previousCandles[index].getHigh() - previousCandles[index].getOpen()) <= (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean lowerThreadTwiceThanBody = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getOpen() - previousCandles[index].getLow()) >= 2.0 * (previousCandles[index].getClose() - previousCandles[index].getOpen()) :
						(previousCandles[index].getClose() - previousCandles[index].getLow()) >= 2.0 * (previousCandles[index].getOpen() - previousCandles[index].getClose());
				boolean LowerThreadBiggerOrEqualsThanUpperThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getHigh() - previousCandles[index].getClose()) <= (previousCandles[index].getOpen() - previousCandles[index].getLow()) :
						(previousCandles[index].getHigh() - previousCandles[index].getOpen()) <= (previousCandles[index].getClose() - previousCandles[index].getLow());
				boolean body20PercentMoreThenLowerThread = (previousCandles[index].getClose() > previousCandles[index].getOpen()) ?
						(previousCandles[index].getOpen() - previousCandles[index].getLow()) * 1.2 <= (previousCandles[index].getClose() - previousCandles[index].getOpen()) :
						(previousCandles[index].getClose() - previousCandles[index].getLow()) * 1.2 <= (previousCandles[index].getOpen() - previousCandles[index].getClose());;
				if (lowerThreadFiftyPercentBiggerThenUpperThread  && lowerThreadTwiceThanBody) {//second candle.
					isHaramiPattern = false;
				} else if (lowerThreadFiftyPercentBiggerThenUpperThread && body20PercentMoreThenLowerThread) {
					isHaramiPattern = true;
				} else if(lowerThreadTwiceThanBody && LowerThreadBiggerOrEqualsThanUpperThread) {
					isHaramiPattern = true;
				} else isHaramiPattern = !(lowerThreadFiftyPercentBiggerThenUpperThread || lowerThreadTwiceThanBody && lowerThreadThirtyPercentBiggerThenUpperThread);
				
			}
			
			for (int i = 1; i < (previousCandles.length - index); i++) {
				if (previousCandles[index + i].getLow() < previousCandles[index - 1].getLow()) {
					didPatternGetApproved = true;
					break;
				} else if (previousCandles[index + i].getHigh() > previousCandles[index - 1].getHigh()) {
	//				didPatternGetApproved = false;
					break;
				}
			}
		}
		
		return isHaramiPattern && didPatternGetApproved;
	}
	
	/*public double getRisk(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isReversalPattern(previousCandles, index, pipsValue))
			return previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow();
		// This is not reversal pattern.
		return -1;
		
	}*/
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getHigh() ;
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue) || isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getLow();
		}
		return -1;
	}

	@Override
	public double getPatternApprovalPoint(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index - 1].getHigh();
		}
		return -1;
	}
	
	@Override
	public int getNumOfCandlesInPattern() {
		return 2;
	}
}
