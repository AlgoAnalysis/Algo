package com.algotrado.mt4.tal.strategy.check.pattern;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.algotrado.mt4.impl.Pattern;
import com.algotrado.mt4.tal.patterns.range.RangePattern;
import com.algotrado.mt4.tal.strategy.Strategy;

public class DoubleReversalPatternStrategy extends Strategy {
	
	private static final int MAX_NUM_OF_CANDLES_BETWEEN_PATTERNS = 30;
	private static List<Pattern> reversalPatterns;
	
	static {
		reversalPatterns = new ArrayList<Pattern>();
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.getContextClassLoader());
		classLoadersList.add(ClasspathHelper.getStaticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
		    .setScanners(new SubTypesScanner(/*false *//* don't exclude Object.class */), new ResourcesScanner())
		    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("com.algotrado.mt4.tal.patterns.reversal"))));
		
		Set<Class<? extends Pattern>> classes = reflections.getSubTypesOf(Pattern.class);
		
		for (Class<? extends Pattern> currClass : classes) {
			try {
				reversalPatterns.add(currClass.getConstructor(new Class<?> [0]).newInstance(new Object[0]));
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println(classes);
		System.out.println(reversalPatterns);
	}

	@Override
	public boolean isLongStrategyPattern(SingleCandleBarData[] candles,
			int index, double pipsValue) {
		if (index < 20) {
			return false;
		}
		boolean isFirstReversalPattern = false;
		boolean isLastReversalPattern = false;
		boolean isLastReversalPatternCandleTouch2LowerBands = false;
		boolean isLastReversalPatternCandleTouch1LowerBand = false;
		double lastPatternLow = 0, lastPatternRSI = 0;
		int numOfCandlesInLastPattern = 0;
		for (Pattern reversal : reversalPatterns) {
			if (reversal.isBullishReversalPattern(candles, index, pipsValue)) {
				lastPatternLow = reversal.getPatternLow(candles, index, pipsValue);
				lastPatternRSI = candles[index].getRsi();
				numOfCandlesInLastPattern = reversal.getNumOfCandlesInPattern();
				for (int i = 0; i < numOfCandlesInLastPattern; i++) {
					if (candles[index - i].getRsi() < lastPatternRSI) {
						lastPatternRSI = candles[index - i].getRsi();
					}
					
					if (!isLastReversalPatternCandleTouch2LowerBands && 
							candles[index - i].getLow() <= candles[index - i].getLower10Bollinger() &&
							candles[index - i].getLow() <= candles[index - i].getLower20Bollinger()) {
						isLastReversalPatternCandleTouch2LowerBands = true;
					} else if ((candles[index - i].getLow() <= candles[index - i].getLower10Bollinger()) ||
							(candles[index - i].getLow() <= candles[index - i].getLower20Bollinger())) {
						isLastReversalPatternCandleTouch1LowerBand = true;
						isLastReversalPatternCandleTouch2LowerBands = true;
					}
					
				}
				isLastReversalPattern = (lastPatternRSI > 30) && isLastReversalPatternCandleTouch2LowerBands &&
						isLastReversalPatternCandleTouch1LowerBand;
				break;
			}
		}
		
		if (!isLastReversalPattern) {
			return false;
		}
		
		// first pattern
		double firstPatternLow, firstPatternRSI;
		int numOfCandlesInFirstPattern = 0;
		boolean isFirstReversalPatternCandleTouch2LowerBands = false;
		boolean isFirstReversalPatternCandleTouch1LowerBand = false;
		int numOfIllegalFirstPatterns = 0;
		for (int j = numOfCandlesInLastPattern; index - j >= 20 && j <= MAX_NUM_OF_CANDLES_BETWEEN_PATTERNS; j++) {
			for (Pattern reversal : reversalPatterns) {
				if (reversal.isBullishReversalPattern(candles, index - j, pipsValue)) {
					firstPatternLow = reversal.getPatternLow(candles, index - j, pipsValue);
					firstPatternRSI = candles[index].getRsi();
					numOfCandlesInFirstPattern = reversal.getNumOfCandlesInPattern();
					for (int i = 0; (i < numOfCandlesInFirstPattern) && ((index - j - i) >= 0); i++) {
						if (candles[index - j - i].getRsi() < firstPatternRSI) {
							firstPatternRSI = candles[index - j - i].getRsi();
						}
						
						if (!isFirstReversalPatternCandleTouch2LowerBands && 
								candles[index - j - i].getLow() <= candles[index - j - i].getLower10Bollinger() &&
								candles[index - j - i].getLow() <= candles[index - j - i].getLower20Bollinger()) {
							isFirstReversalPatternCandleTouch2LowerBands = true;
						} else if (candles[index - j - i].getLow() <= candles[index - j - i].getLower10Bollinger() ||
								candles[index - j - i].getLow() <= candles[index - j - i].getLower20Bollinger()) {
							isFirstReversalPatternCandleTouch1LowerBand = true;
							isFirstReversalPatternCandleTouch2LowerBands = true;
						}
//						
					}
					isFirstReversalPattern = (firstPatternRSI < 30) && ((firstPatternLow <= lastPatternLow) || (Math.abs(firstPatternLow - lastPatternLow) <= 5 * pipsValue)) &&
							isFirstReversalPatternCandleTouch2LowerBands && isFirstReversalPatternCandleTouch1LowerBand;
					
					if (isFirstReversalPattern) {
						System.out.println("isFirstReversalPattern: RSI First Pattern = " + firstPatternRSI);
						System.out.println(" = " + firstPatternRSI);
					}
					
					if (!isFirstReversalPattern && isFirstReversalPatternCandleTouch2LowerBands && isFirstReversalPatternCandleTouch1LowerBand) {
						numOfIllegalFirstPatterns++;
					}
					
					if ((numOfIllegalFirstPatterns > 3 && !isFirstReversalPattern)  || (!isFirstReversalPattern && !((firstPatternLow <= lastPatternLow) || (Math.abs(firstPatternLow - lastPatternLow) <= 5 * pipsValue))) ) {
						return false;
					}
					
					break;
				}
			}
			if (isFirstReversalPattern) {
				break;
			}
		}
		
		//look for previous long pattern on Lower bands and check for RSI Values.
		
		return isFirstReversalPattern;
	}

	@Override
	public boolean isShortStrategyPattern(SingleCandleBarData[] candles, int index, double pipsValue) {
		if (index < 20) {
			return false;
		}
		boolean isFirstReversalPattern = false;
		boolean isLastReversalPattern = false;
		boolean isLastReversalPatternCandleTouch2HigherBands = false;
		boolean isLastReversalPatternCandleTouch1HigherBand = false;
		double lastPatternHigh = 0, lastPatternRSI = 0;
		int numOfCandlesInLastPattern = 0;
		for (Pattern reversal : reversalPatterns) {
			if (reversal.isBearishReversalPattern(candles, index, pipsValue)) {
				lastPatternHigh = reversal.getPatternHigh(candles, index, pipsValue);
				lastPatternRSI = candles[index].getRsi();
				numOfCandlesInLastPattern = reversal.getNumOfCandlesInPattern();
				for (int i = 0; i < numOfCandlesInLastPattern; i++) {
					if (candles[index - i].getRsi() > lastPatternRSI) {
						lastPatternRSI = candles[index - i].getRsi();
					}
					
					if (!isLastReversalPatternCandleTouch2HigherBands && 
							candles[index - i].getHigh() >= candles[index - i].getHigher10Bollinger() &&
							candles[index - i].getHigh() >= candles[index - i].getHigher20Bollinger()) {
						isLastReversalPatternCandleTouch2HigherBands = true;
					} else if ((candles[index - i].getHigh() >= candles[index - i].getHigher10Bollinger()) ||
							(candles[index - i].getHigh() >= candles[index - i].getHigher20Bollinger())) {
						isLastReversalPatternCandleTouch1HigherBand = true;
						isLastReversalPatternCandleTouch2HigherBands = true;
					}
					
				}
				isLastReversalPattern = (lastPatternRSI < 70) && isLastReversalPatternCandleTouch2HigherBands &&
						isLastReversalPatternCandleTouch1HigherBand;
				break;
			}
		}
		
		if (!isLastReversalPattern) {
			return false;
		}
		
		// first pattern
		double firstPatternHigh, firstPatternRSI;
		int numOfCandlesInFirstPattern = 0;
		boolean isFirstReversalPatternCandleTouch2HigherBands = false;
		boolean isFirstReversalPatternCandleTouch1HigherBand = false;
		int numOfIllegalFirstPatterns = 0;
		for (int j = numOfCandlesInLastPattern; index - j >= 20 && j <= MAX_NUM_OF_CANDLES_BETWEEN_PATTERNS; j++) {
			for (Pattern reversal : reversalPatterns) {
				if (reversal.isBearishReversalPattern(candles, index - j, pipsValue)) {
					firstPatternHigh = reversal.getPatternHigh(candles, index - j, pipsValue);
					firstPatternRSI = candles[index].getRsi();
					numOfCandlesInFirstPattern = reversal.getNumOfCandlesInPattern();
					for (int i = 0; (i < numOfCandlesInFirstPattern) && ((index - j - i) >= 0); i++) {
						if (candles[index - j - i].getRsi() > firstPatternRSI) {
							firstPatternRSI = candles[index - j - i].getRsi();
						}
						
						if (!isFirstReversalPatternCandleTouch2HigherBands && 
								candles[index - j - i].getHigh() >= candles[index - j - i].getHigher10Bollinger() &&
								candles[index - j - i].getHigh() >= candles[index - j - i].getHigher20Bollinger()) {
							isFirstReversalPatternCandleTouch2HigherBands = true;
						} else if (candles[index - j - i].getHigh() >= candles[index - j - i].getHigher10Bollinger() ||
								candles[index - j - i].getHigh() >= candles[index - j - i].getHigher20Bollinger()) {
							isFirstReversalPatternCandleTouch1HigherBand = true;
							isFirstReversalPatternCandleTouch2HigherBands = true;
						}
//						
					}
					isFirstReversalPattern = (firstPatternRSI > 70) && ((firstPatternHigh >= lastPatternHigh) || (Math.abs(firstPatternHigh - lastPatternHigh) <= 5 * pipsValue)) &&
							isFirstReversalPatternCandleTouch2HigherBands && isFirstReversalPatternCandleTouch1HigherBand;
					
					if (isFirstReversalPattern) {
						System.out.println("isFirstReversalPattern: RSI First Pattern = " + firstPatternRSI);
						System.out.println(" = " + firstPatternRSI);
					}
					
					if (!isFirstReversalPattern && isFirstReversalPatternCandleTouch2HigherBands && isFirstReversalPatternCandleTouch1HigherBand) {
						numOfIllegalFirstPatterns++;
					}
					
					if ((numOfIllegalFirstPatterns > 3 && !isFirstReversalPattern)  || (!isFirstReversalPattern && !((firstPatternHigh >= lastPatternHigh) || (Math.abs(firstPatternHigh - lastPatternHigh) <= 5 * pipsValue))) ) {
						return false;
					}
					
					break;
				}
			}
			if (isFirstReversalPattern) {
				break;
			}
		}
		
		//look for previous long pattern on Higher bands and check for RSI Values.
		
		return isFirstReversalPattern;
	}

}
