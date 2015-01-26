package com.algotrado.mt4.tal.patterns.continuos;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.mt4.impl.Doji;
import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;
import com.algotrado.util.PriceUtil;

public class TasukiUpOrDownPattern extends Pattern {

	@Override
	public boolean isBullishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		boolean isBullishFirstCandle = previousCandles[index - 2].getClose() > previousCandles[index - 2].getOpen();
		double candleBody = Math.abs(previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		double candleThreads = Math.abs(previousCandles[index - 2].getHigh() - previousCandles[index - 2].getLow()) - candleBody;
		boolean isBigBodySmallThreadsInFirstCandle = candleThreads <= candleBody;
		
		boolean isBullishSecondCandle = previousCandles[index - 1].getClose() > previousCandles[index - 1].getOpen();
		
		boolean isSecondCandleHigherThenFirstCandle = previousCandles[index - 2].getHigh() < previousCandles[index - 1].getLow();
		
		boolean isLegalSecondCandle = isBullishSecondCandle && isAbsoluteGAPBetween2Candles(previousCandles[index - 2], previousCandles[index - 1]) && 
				!(new Doji().isDoji(previousCandles[index - 1])) && isSecondCandleHigherThenFirstCandle;
		
		boolean isBearishThirdCandle = previousCandles[index].getClose() < previousCandles[index].getOpen();
		
		boolean thirdCandleOpenEqualsSecondCandleClose = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index].getOpen()) <= 2 * pipsValue;
		
		boolean isThirdCandleCloseAboveHalfWindow = (previousCandles[index].getClose() >= previousCandles[index - 1].getLow()) ||
				(previousCandles[index - 1].getLow() - previousCandles[index].getClose()) <= (previousCandles[index].getClose() - previousCandles[index - 2].getHigh());
		
		boolean isThirdCandleLowBelowHalfWindow = (previousCandles[index].getLow() >= previousCandles[index - 1].getLow()) ||
				(previousCandles[index - 1].getLow() - previousCandles[index].getLow()) <= (previousCandles[index].getLow() - previousCandles[index - 2].getHigh());
				
		return isBullishFirstCandle && isBigBodySmallThreadsInFirstCandle && isLegalSecondCandle &&
				isBearishThirdCandle && thirdCandleOpenEqualsSecondCandleClose && isThirdCandleCloseAboveHalfWindow && 
				isThirdCandleLowBelowHalfWindow;
	}

	@Override
	public boolean isBearishReversalPattern(SingleCandleBarData[] previousCandles, int index, double pipsValue) {
		if (previousCandles.length <= 4 || previousCandles.length <= index + 1 || index < 2) {
			return false;
		}
		boolean isBearishFirstCandle = previousCandles[index - 2].getClose() < previousCandles[index - 2].getOpen();
		double candleBody = Math.abs(previousCandles[index - 2].getClose() - previousCandles[index - 2].getOpen());
		double candleThreads = Math.abs(previousCandles[index - 2].getHigh() - previousCandles[index - 2].getLow()) - candleBody;
		boolean isBigBodySmallThreadsInFirstCandle = candleThreads <= candleBody;
		
		boolean isBearishSecondCandle = previousCandles[index - 1].getClose() < previousCandles[index - 1].getOpen();
		
		boolean isSecondCandleLowerThenFirstCandle = previousCandles[index - 1].getHigh() < previousCandles[index - 2].getLow();
		
		boolean isLegalSecondCandle = isBearishSecondCandle && isAbsoluteGAPBetween2Candles(previousCandles[index - 2], previousCandles[index - 1]) && 
				!(new Doji().isDoji(previousCandles[index - 1])) && isSecondCandleLowerThenFirstCandle;
		
		boolean isBullishThirdCandle = previousCandles[index].getClose() > previousCandles[index].getOpen();
		
		boolean thirdCandleOpenEqualsSecondCandleClose = Math.abs(previousCandles[index - 1].getClose() - previousCandles[index].getOpen()) <= 2 * pipsValue;
		
		boolean isThirdCandleCloseBelowHalfWindow = (previousCandles[index].getClose() <= previousCandles[index - 1].getHigh()) ||
				(previousCandles[index].getClose() - previousCandles[index - 1].getHigh()) <= (previousCandles[index - 2].getLow() - previousCandles[index].getClose());
		
		boolean isThirdCandleHighBelowHalfWindow = (previousCandles[index].getHigh() <= previousCandles[index - 1].getHigh()) ||
				(previousCandles[index].getHigh() - previousCandles[index - 1].getHigh()) <= (previousCandles[index - 2].getLow() - previousCandles[index].getHigh());
				
		return isBearishFirstCandle && isBigBodySmallThreadsInFirstCandle && isLegalSecondCandle &&
				isBullishThirdCandle && thirdCandleOpenEqualsSecondCandleClose && isThirdCandleCloseBelowHalfWindow && 
				isThirdCandleHighBelowHalfWindow;
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
