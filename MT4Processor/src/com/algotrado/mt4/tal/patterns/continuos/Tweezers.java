package com.algotrado.mt4.tal.patterns.continuos;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class Tweezers extends Pattern {

	@Override
	public boolean isBullishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		double firstCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double firstCandleThreads = previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow() - firstCandleBody;
		boolean isFirstCandleBigBodySmallThreads = firstCandleBody >= 2 * firstCandleThreads;
		boolean isFirstCandleBearish = previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen();
		boolean didPatternCreateNewLow = Math.min(previousCandles[index].getLow(), previousCandles[index - 1].getLow()) < previousCandles[index - 2].getLow() && 
				previousCandles[index - 1].getHigh() < previousCandles[index - 2].getHigh();
		
		boolean isSecondCandleBullish = previousCandles[index].getClose() > previousCandles[index].getOpen();
		double secondCandleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double secondCandleThreads = previousCandles[index].getHigh() - previousCandles[index].getLow() - secondCandleBody;
		boolean secondCandleWithSmallBody = secondCandleBody <= secondCandleThreads;
		
		boolean isLegalTweezersButton = (Math.abs(previousCandles[index].getLow() - previousCandles[index - 1].getLow()) <= 2.0 * pipsValue) ||
				(Math.abs(previousCandles[index].getOpen() - previousCandles[index - 1].getClose()) <= 2.0 * pipsValue);
		
		boolean didPatternGetApproved = false;
		
		for (int i = 1; i < (previousCandles.length - index); i++) {
			if (previousCandles[index + i].getLow() < Math.min(previousCandles[index].getLow(), previousCandles[index - 1].getLow())) {
				
				break;
			} else if (previousCandles[index + i].getHigh() > previousCandles[index].getHigh()) {
				didPatternGetApproved = true;
				break;
			}
		}
		
		return isFirstCandleBigBodySmallThreads && isFirstCandleBearish && secondCandleWithSmallBody && isSecondCandleBullish
				&& isLegalTweezersButton && didPatternGetApproved && didPatternCreateNewLow;
	}

	@Override
	public boolean isBearishReversalPattern(
			SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		
		double firstCandleBody = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index - 1].getOpen());
		double firstCandleThreads = previousCandles[index - 1].getHigh() - previousCandles[index - 1].getLow() - firstCandleBody;
		boolean isFirstCandleBigBodySmallThreads = firstCandleBody >= 2 * firstCandleThreads;
		boolean isFirstCandleBullish = previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen();
		boolean didPatternCreateNewHigh = Math.max(previousCandles[index].getHigh(), previousCandles[index - 1].getHigh()) > previousCandles[index - 2].getHigh();
		
		boolean isSecondCandleBearish = previousCandles[index].getClose() < previousCandles[index].getOpen();
		double secondCandleBody = Math.abs(previousCandles[index].getClose() - previousCandles[index].getOpen());
		double secondCandleThreads = previousCandles[index].getHigh() - previousCandles[index].getLow() - secondCandleBody;
		boolean secondCandleWithSmallBody = secondCandleBody <= secondCandleThreads;
		
		boolean isLegalTweezersTop = (Math.abs(previousCandles[index].getHigh() - previousCandles[index - 1].getHigh()) <= 2.0 * pipsValue) ||
				(Math.abs(previousCandles[index].getClose() - previousCandles[index - 1].getOpen()) <= 2.0 * pipsValue);
		
		boolean didPatternGetApproved = false;
		
		for (int i = 1; i < (previousCandles.length - index); i++) {
			if (previousCandles[index + i].getLow() < previousCandles[index].getLow()) {
				didPatternGetApproved = true;
				break;
			} else if (previousCandles[index + i].getHigh() > Math.max(previousCandles[index].getHigh(), previousCandles[index - 1].getHigh())) {
				break;
			}
		}
		
		return isFirstCandleBigBodySmallThreads && isFirstCandleBullish && secondCandleWithSmallBody && isSecondCandleBearish
				&& isLegalTweezersTop && didPatternGetApproved && didPatternCreateNewHigh;
	}

	@Override
	public double getRisk(SingleCandleBarData[] previousCandles, int index,
			double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return -1;
		}
		if (isBearishReversalPattern(previousCandles, index, pipsValue))
			return Math.abs(Math.max(previousCandles[index - 1].getHigh(), previousCandles[index].getHigh()) - 
								previousCandles[index].getLow());
		if (isBullishReversalPattern(previousCandles, index, pipsValue))
			return Math.abs(previousCandles[index].getHigh() - 
								Math.min(previousCandles[index - 1].getLow(), previousCandles[index].getLow()));
		
		return -1;
	}
	
	@Override
	public double getPatternHigh(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			return PriceUtil.getMaxHigh(bars);
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getHigh();
		}
		return -1;
	}

	@Override
	public double getPatternLow(SingleCandleBarData[] previousCandles,
			int index, double pipsValue) {
		if (isBearishReversalPattern(previousCandles, index, pipsValue)) {
			return previousCandles[index].getLow();
		} else if (isBullishReversalPattern(previousCandles, index, pipsValue)) {
			List<JapaneseCandleBar> bars = new ArrayList<JapaneseCandleBar>();
			bars.add(previousCandles[index]);
			bars.add(previousCandles[index - 1]);
			return  PriceUtil.getMinLow(bars);
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
