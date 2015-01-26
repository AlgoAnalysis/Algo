package com.algotrado.mt4.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.algotrado.mt4.impl.CandleHourSuccessStatistics;
import com.algotrado.mt4.impl.FileNameTimeFrame;
import com.algotrado.mt4.impl.JapaneseCandleBar;
import com.algotrado.mt4.tal.strategy.ExtremeConditionOutside25StandartDeviation;
import com.algotrado.mt4.tal.strategy.Strategy;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class Run2Point5DeviationExtremeCondition {

	private static final String PINBAR_DATA_FOLDER = "C:\\Users\\ohad\\AppData\\Roaming\\MetaQuotes\\Terminal\\1CF1A45F9E06881833077514E2850EFB\\MQL4\\Files\\pinbar_data_for_tal\\";

	public Run2Point5DeviationExtremeCondition(){}
	
	private double pipsValue = -1;
	
	public static void main(String [] args)
	  {
		File folder = new File(PINBAR_DATA_FOLDER);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".csv")) {
				System.out.println("File " + listOfFiles[i].getName());
				String comodityName = "";
				for (int j = 0; j < listOfFiles[i].getName().length(); j++) {
					if (Character.isLetter(listOfFiles[i].getName().toLowerCase().charAt(j))) {
						comodityName += listOfFiles[i].getName().charAt(j);
					} else {
						break;
					}
				}
				System.out.println("checking commodity: " + comodityName);
				boolean appendTotalResults = i != 0;
				new Run2Point5DeviationExtremeCondition().getPinbarStatisticsForFile(comodityName, listOfFiles[i].getName(), appendTotalResults);
			} 
		}
	  }

	public void getPinbarStatisticsForFile(String comodityName, String fileName, boolean appendTotalResults) {
		List<SingleCandleBarData> datalist = new ArrayList<SingleCandleBarData>();
//	    List<SingleCandleBarData> tempDatalist = new ArrayList<SingleCandleBarData>();
	    Map<Integer, CandleHourSuccessStatistics> intraDaystatistics = new HashMap<Integer, CandleHourSuccessStatistics>();
//	    Map<Integer, CandleDaySuccessStatistics> daystatistics = new HashMap<Integer, CandleDaySuccessStatistics>();

	    try
	    {
	    	SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm");
	    	SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
	    	int index = 0;
	    	readPinbarDataFromSingleFile(comodityName, fileName, datalist);
	      
	    	SingleCandleBarData[] candleBars = (SingleCandleBarData[])datalist.toArray(new SingleCandleBarData[0]);//new JapaneseCandleBar[datalist.size()];
	    	String timeFrame = FileNameTimeFrame.getTimeFrame(candleBars, fileName);

	    	System.out.println("Pinbars");
	    	try
	    	{
	    		String calculationResults = "C:\\Users\\ohad\\AppData\\Roaming\\MetaQuotes\\Terminal\\1CF1A45F9E06881833077514E2850EFB\\MQL4\\Files\\test_results_for_tal\\" + comodityName + "_25_extreme_deviation_" + timeFrame + ".csv";
	    		FileWriter currentBarWriter = new FileWriter(calculationResults);
	    		String totalResultsFile = "C:\\Users\\ohad\\AppData\\Roaming\\MetaQuotes\\Terminal\\1CF1A45F9E06881833077514E2850EFB\\MQL4\\Files\\test_results_for_tal\\pinbarTotalResults.csv";
	    		FileWriter totalResultsWriter = new FileWriter(totalResultsFile, appendTotalResults);
	    		currentBarWriter.append("time");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("hour");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("time-frame");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("high");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("low");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("open");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("close");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("direction");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Risk");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Gain");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Num of Candles until success");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Correction");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Risk/Gain Ratio");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Reached Midline");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Midline Gain");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Midline Risk Gain Ratio");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Did 1:1?");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Did 2:1?");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Did 3:1?");
	    		currentBarWriter.append('\n');

	    		Strategy barStrategy = new ExtremeConditionOutside25StandartDeviation();
	    		double pinbarIndex = 0, successfullPinbarsDidOneToOne = 0, successfullPinbarsDidTwoToOne = 0, successfullPinbarsDidThreeToOne = 0;
	    		double successfulMidline = 0;
	    		for (SingleCandleBarData temp : datalist) {
	    			//		    	double pipsValue = (fileName.contains("JPY")) ? 0.01 : 0.0001;
	    			if (barStrategy.isStrategyPattern(candleBars, index, pipsValue)) {
	    				System.out.println(temp);

	    				if (timeFrame.equals(JapaneseCandleBar._5_MINUTES) || timeFrame.equals(JapaneseCandleBar._15_MINUTES)) {
	    					if (intraDaystatistics.get(temp.getTime().getHours()) == null) {
	    						intraDaystatistics.put(temp.getTime().getHours(), new CandleHourSuccessStatistics(temp.getTime().getHours(), 0, 0));
	    					}

	    				} 

	    				currentBarWriter.append(dateformatter.format(temp.getTime()));
	    				currentBarWriter.append(',');
	    				if (temp.getTime().getHours() == 12) {
	    					currentBarWriter.append("12:" + ((temp.getTime().getMinutes() < 10) ? "0":"") + temp.getTime().getMinutes());
	    				} else {
	    					currentBarWriter.append(hourformatter.format(temp.getTime()));
	    				}
	    				currentBarWriter.append(',');
	    				currentBarWriter.append(timeFrame);
	    				currentBarWriter.append(',');
	    				currentBarWriter.append(temp.getHigh() + "," + temp.getLow());
	    				currentBarWriter.append(',');

	    				currentBarWriter.append("" + temp.getOpen());
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("" + temp.getClose());
	    				currentBarWriter.append(',');
	    				boolean bullishPinbarStrategy = barStrategy.isLongStrategyPattern(candleBars, index, pipsValue);
	    				currentBarWriter.append(bullishPinbarStrategy ? "Long ↑" : "Short ↓");
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("" + ((ExtremeConditionOutside25StandartDeviation)barStrategy).getRisk());
	    				currentBarWriter.append(',');
	    				double pinbarGain = (double)Math.round( (bullishPinbarStrategy ? barStrategy.getLongGain(candleBars, index + 1, ((ExtremeConditionOutside25StandartDeviation)barStrategy).getStopLoss(), ((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint() ) : barStrategy.getShortGain(candleBars, index + 1, ((ExtremeConditionOutside25StandartDeviation)barStrategy).getStopLoss(), ((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint()) ) * (double) 1000) / (double)1000;
	    				currentBarWriter.append("" + pinbarGain );
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("" + (bullishPinbarStrategy ? barStrategy.getLongNumOfCandles(temp, candleBars, index) : barStrategy.getShortNumOfCandles(temp, candleBars, index) ) );
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("" + Math.round(bullishPinbarStrategy ? barStrategy.getLongCorrectionBeforeHigh(temp, candleBars, index) : barStrategy.getShortCorrectionBeforeLow(temp, candleBars, index) ) );
	    				currentBarWriter.append(',');
	    				double riskGainRatio = (double)Math.round( (bullishPinbarStrategy ? barStrategy.getLongRiskGainRatio(candleBars, index, ((ExtremeConditionOutside25StandartDeviation)barStrategy).getStopLoss(), ((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint()) : barStrategy.getShortRiskGainRatio(candleBars, index, ((ExtremeConditionOutside25StandartDeviation)barStrategy).getStopLoss(), ((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint())) * (double) 1000) / (double)1000;
	    				if (/*timeFrame.equals(JapaneseCandleBar._4_HOUR) || */timeFrame.equals(JapaneseCandleBar._5_MINUTES) || timeFrame.equals(JapaneseCandleBar._15_MINUTES)) {
	    					intraDaystatistics.get(temp.getTime().getHours()).
	    					setOccourancesNum(intraDaystatistics.get(temp.getTime().getHours()).getOccourancesNum() + 1);
	    					if (riskGainRatio > 1) {
	    						intraDaystatistics.get(temp.getTime().getHours()).
	    						setNumOfSuccessOccurances(intraDaystatistics.get(temp.getTime().getHours()).getNumOfSuccessOccurances() + 1);
	    					}

	    				} /*else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
			        	    	daystatistics.get(temp.getTime().getDay()).
		        	    		setOccourancesNum(daystatistics.get(temp.getTime().getDay()).getOccourancesNum() + 1);
			        	    	if (riskGainRatio > 1) {
			        	    		daystatistics.get(temp.getTime().getDay()).
				        	    		setNumOfSuccessOccurances(daystatistics.get(temp.getTime().getDay()).getNumOfSuccessOccurances() + 1);
			        	    	}
			        	    }*/
	    				currentBarWriter.append("" + riskGainRatio);
	    				currentBarWriter.append(',');
	    				boolean reachedMidLine = (bullishPinbarStrategy) ? ((ExtremeConditionOutside25StandartDeviation)barStrategy).didReachMidLineLong(candleBars, index, riskGainRatio) : ((ExtremeConditionOutside25StandartDeviation)barStrategy).didReachMidLineShort(candleBars, index, riskGainRatio);
	    				double midLineGain = (bullishPinbarStrategy) ? (candleBars[index].getSMA20()) - ((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint() :
	    					(((ExtremeConditionOutside25StandartDeviation)barStrategy).getConfirmationPoint() - candleBars[index].getSMA20());
						currentBarWriter.append(reachedMidLine ? "Got To Midline" : "Didn't Reach Midline");
						successfulMidline += (reachedMidLine && midLineGain > 0) ? 1 : 0;
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("MidLineGain:" + (reachedMidLine ? midLineGain : ("-" + ((ExtremeConditionOutside25StandartDeviation)barStrategy).getRisk())));
	    				currentBarWriter.append(',');
	    				currentBarWriter.append("MidLineRiskGainRatio:" + (reachedMidLine ? midLineGain/((ExtremeConditionOutside25StandartDeviation)barStrategy).getRisk() : "-1"));
	    				currentBarWriter.append(',');
	    				boolean riskGainRatioBiggerThan1 = riskGainRatio >= 1;
	    				successfullPinbarsDidOneToOne += riskGainRatioBiggerThan1 ? 1 : 0;
	    				currentBarWriter.append(riskGainRatioBiggerThan1 ? "1" : "0");
	    				currentBarWriter.append(',');
	    				currentBarWriter.append(riskGainRatio >= 2 ? "1" : "0");
	    				successfullPinbarsDidTwoToOne += (riskGainRatio >= 2) ? 1 : 0;
	    				currentBarWriter.append(',');
	    				currentBarWriter.append(riskGainRatio >= 3 ? "1" : "0");
	    				successfullPinbarsDidThreeToOne += (riskGainRatio >= 3) ? 1 : 0;
	    				currentBarWriter.append('\n');
	    				pinbarIndex++;
	    			}
	    			index++;
	    		}
	    		writeSuccessPercentageAndExpectedValue(currentBarWriter,
	    				pinbarIndex, successfullPinbarsDidOneToOne,
	    				successfullPinbarsDidTwoToOne,
	    				successfullPinbarsDidThreeToOne, null, null);

	    		writeSuccessPercentageAndExpectedValue(totalResultsWriter,
	    				pinbarIndex, successfullPinbarsDidOneToOne,
	    				successfullPinbarsDidTwoToOne,
	    				successfullPinbarsDidThreeToOne, comodityName, timeFrame);
	    		
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("Success Percentage Reaching Midline:");
	    		currentBarWriter.append(',');
	    		double midlineSuccessPercentage = (double)successfulMidline/(double)pinbarIndex;
	    		currentBarWriter.append("" +  ((double)Math.round(midlineSuccessPercentage * (double)100000) / (double)1000) + "%");
	    		currentBarWriter.append(" Expected Value:");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("" +  (Math.round((midlineSuccessPercentage - ((double)1 - midlineSuccessPercentage)) * (double)10000) / (double)100) + " points");
	    		currentBarWriter.append("Num Of Successes:");
	    		currentBarWriter.append(',');
	    		currentBarWriter.append("" +  successfulMidline + " from " + pinbarIndex);
		  	    
		  	    /*if (timeFrame.equals(JapaneseCandleBar._5_MINUTES)) {
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight = new CandleHourSuccessStatistics(-1, 0, 0);
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour = new CandleHourSuccessStatistics(-1, 0, 0);
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight = new CandleHourSuccessStatistics(-1, 0, 0);
			  	    for (CandleHourSuccessStatistics candleHourSuccessStatistics : intraDaystatistics.values()) {
			  	    	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append("Percent: " + ((candleHourSuccessStatistics.getNumOfSuccessOccurances() * 100) / candleHourSuccessStatistics.getOccourancesNum()) + "%");
				      	currentBarWriter.append(',');
				      	currentBarWriter.append("hour:" +  candleHourSuccessStatistics.getHour() + ":00");
				  	    currentBarWriter.append(',');
				  	    currentBarWriter.append("Succeded " +  candleHourSuccessStatistics.getNumOfSuccessOccurances() + " from total of " + candleHourSuccessStatistics.getOccourancesNum() + " occourances.");
				  	    currentBarWriter.append(',');
				  	    currentBarWriter.append('\n');
				  	    if (candleHourSuccessStatistics.getHour() >= 8 && candleHourSuccessStatistics.getHour() <= 20) {
				  	    	candleHourSuccessStatisticsEightToEight.setOccourancesNum(candleHourSuccessStatisticsEightToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
				  	    	candleHourSuccessStatisticsEightToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsEightToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
				  	    } 
				  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 16){
				  	    	candleHourSuccessStatisticsFourToFour.setOccourancesNum(candleHourSuccessStatisticsFourToFour.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
				  	    	candleHourSuccessStatisticsFourToFour.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
				  	    }
				  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 20){
				  	    	candleHourSuccessStatisticsFourToEight.setOccourancesNum(candleHourSuccessStatisticsFourToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
				  	    	candleHourSuccessStatisticsFourToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
				  	    }
					}
			  	    
			  	    writeSuccessRatesByHours(currentBarWriter,
							candleHourSuccessStatisticsEightToEight,
							candleHourSuccessStatisticsFourToFour,
							candleHourSuccessStatisticsFourToEight, null, null);
			  	    
			  	    writeSuccessRatesByHours(totalResultsWriter,
							candleHourSuccessStatisticsEightToEight,
							candleHourSuccessStatisticsFourToFour,
							candleHourSuccessStatisticsFourToEight, comodityName, timeFrame);
	
		  	    } *//*else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
		  	    	
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight = new CandleHourSuccessStatistics(-1, 0, 0);
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour = new CandleHourSuccessStatistics(-1, 0, 0);
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight = new CandleHourSuccessStatistics(-1, 0, 0);
			  	    for (CandleDaySuccessStatistics candleDaySuccessStatistics : daystatistics.values()) {
			  	    	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append(',');
				      	currentBarWriter.append("Percent: " + ((candleDaySuccessStatistics.getNumOfSuccessOccurances() * 100) / candleDaySuccessStatistics.getOccourancesNum()) + "%");
				      	currentBarWriter.append(',');
				      	currentBarWriter.append("Day:" +  candleDaySuccessStatistics.getDay() );
				  	    currentBarWriter.append(',');
				  	    currentBarWriter.append("Succeded " +  candleDaySuccessStatistics.getNumOfSuccessOccurances() + " from total of " + candleDaySuccessStatistics.getOccourancesNum() + " occourances.");
				  	    currentBarWriter.append(',');
				  	    currentBarWriter.append('\n');
//				  	    if (candleHourSuccessStatistics.getHour() >= 8 && candleHourSuccessStatistics.getHour() <= 20) {
//				  	    	candleHourSuccessStatisticsEightToEight.setOccourancesNum(candleHourSuccessStatisticsEightToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//				  	    	candleHourSuccessStatisticsEightToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsEightToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//				  	    } 
//				  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 16){
//				  	    	candleHourSuccessStatisticsFourToFour.setOccourancesNum(candleHourSuccessStatisticsFourToFour.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//				  	    	candleHourSuccessStatisticsFourToFour.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//				  	    }
//				  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 20){
//				  	    	candleHourSuccessStatisticsFourToEight.setOccourancesNum(candleHourSuccessStatisticsFourToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//				  	    	candleHourSuccessStatisticsFourToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//				  	    }
					}
			  	    
//			  	    writeSuccessRatesByHours(currentPinbarWriter,
//							candleHourSuccessStatisticsEightToEight,
//							candleHourSuccessStatisticsFourToFour,
//							candleHourSuccessStatisticsFourToEight, null, null);
//			  	    
//			  	    writeSuccessRatesByHours(totalResultsWriter,
//							candleHourSuccessStatisticsEightToEight,
//							candleHourSuccessStatisticsFourToFour,
//							candleHourSuccessStatisticsFourToEight, comodityName, timeFrame);
		  	    	
		  	    }*/
	    		currentBarWriter.flush();
	    		currentBarWriter.close();

	    		totalResultsWriter.flush();
	    		totalResultsWriter.close();
	    	}
	    	catch(IOException e)
	    	{
	    		e.printStackTrace();
	    	} 
	    }
	    catch(IOException ioe){
	    	ioe.printStackTrace();
	    }
	}

	public void readPinbarDataFromSingleFile(String comodityName,
			String fileName, List<SingleCandleBarData> datalist)
			throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(PINBAR_DATA_FOLDER + fileName);
		BufferedReader br = new BufferedReader(fr);
		String stringRead = br.readLine();
		if (stringRead != null) {
			pipsValue = Double.valueOf(stringRead);
			stringRead = br.readLine();
		}
//		int index = 0;
		String date = null, period = null;
		Double open = null, high = null, low = null, close = null, sma20 = null, bollinger20TopBand = null, bollinger20BottomBand = null,
				sma10 = null, bollinger10TopBand = null, bollinger10BottomBand = null, rsi = null;

		//2014.04.15 04:00:00
		//SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		while( stringRead != null )
		{
			//StringTokenizer st = new StringTokenizer(stringRead, ",");
			StringTokenizer st = new StringTokenizer(stringRead, ";");
			date = st.nextToken( );
			period = st.nextToken( );  
			open = Double.valueOf(st.nextToken( )); 
			high = Double.valueOf(st.nextToken( ));
			low = Double.valueOf(st.nextToken( ));  
			close = Double.valueOf(st.nextToken( ));
			double volume = Double.valueOf(st.nextToken( ));
			bollinger20TopBand = Double.valueOf(st.nextToken( ));
			sma20  =  Double.valueOf(st.nextToken( ));
			bollinger20BottomBand = Double.valueOf(st.nextToken( ));
			bollinger10TopBand = Double.valueOf(st.nextToken( ));
			sma10  =  Double.valueOf(st.nextToken( ));
			bollinger10BottomBand = Double.valueOf(st.nextToken( ));
			rsi = Double.valueOf(st.nextToken( ));

			Date formattedDate = null;
			try {
				formattedDate = formatter.parse(date /*+ " " + hour*/);
			} catch (ParseException e) {
				e.printStackTrace();
			}


			SingleCandleBarData temp = new SingleCandleBarData(open, close, high, low, formattedDate, comodityName, sma20, bollinger20BottomBand, bollinger20TopBand,
					sma10, bollinger10BottomBand, bollinger10TopBand, rsi);
			System.out.println(temp);
			datalist.add(temp);

			// read the next line
			stringRead = br.readLine();
		}
		br.close( );
//		return index;
	}

	public static void writeSuccessPercentageAndExpectedValue(
			FileWriter currentPinbarWriter, double pinbarIndex,
			double successfullPinbarsDidOneToOne,
			double successfullPinbarsDidTwoToOne,
			double successfullPinbarsDidThreeToOne, String commodity, String timeFrame) throws IOException {
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Success Percentage");
		currentPinbarWriter.append(',');
		double oneToOneSuccessPercentage = (double)successfullPinbarsDidOneToOne/(double)pinbarIndex;
		currentPinbarWriter.append("" +  ((double)Math.round(oneToOneSuccessPercentage * (double)100000) / (double)1000) + "%");
		currentPinbarWriter.append(',');
		double twoToOneSuccessPercentage = (double)successfullPinbarsDidTwoToOne/(double)pinbarIndex;
		currentPinbarWriter.append("" +  ((double)Math.round(twoToOneSuccessPercentage * (double)100000) / (double)1000) + "%");
		currentPinbarWriter.append(',');
		double threeToOneSuccessPercentage = (double)successfullPinbarsDidThreeToOne/(double)pinbarIndex;
		currentPinbarWriter.append("" +  ((double)Math.round(threeToOneSuccessPercentage * (double)100000) / (double)1000) + "%");
		currentPinbarWriter.append('\n');
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		currentPinbarWriter.append(',');
		currentPinbarWriter.append(" Expected Value");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  (Math.round((oneToOneSuccessPercentage - ((double)1 - oneToOneSuccessPercentage)) * (double)10000) / (double)100) + " points");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  (Math.round(((twoToOneSuccessPercentage * 2)- ((double)1 - twoToOneSuccessPercentage)) * (double)10000) / (double)100) + " points");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  (Math.round(((threeToOneSuccessPercentage * 3) - ((double)1 - threeToOneSuccessPercentage)) * (double)10000) / (double)100) + " points");
		currentPinbarWriter.append('\n');
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Num Of Successes");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  successfullPinbarsDidOneToOne + " from " + pinbarIndex);
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  successfullPinbarsDidTwoToOne + " from " + pinbarIndex);
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("" +  successfullPinbarsDidThreeToOne + " from " + pinbarIndex);
		currentPinbarWriter.append('\n');
	}

	public static void writeSuccessRatesByHours(
			FileWriter currentPinbarWriter,
			CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight,
			CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour,
			CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight, 
			String commodity, String timeFrame)
			throws IOException {
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		
		currentPinbarWriter.append(',');
		int numOfSuccessOccurances = candleHourSuccessStatisticsEightToEight.getNumOfSuccessOccurances();
		currentPinbarWriter.append("Percent: " + ((numOfSuccessOccurances == 0) ? "0" : ((numOfSuccessOccurances * 100) / candleHourSuccessStatisticsEightToEight.getOccourancesNum())) + "%");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("hour: 08:00-20:00");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Succeded " +  numOfSuccessOccurances + " from total of " + candleHourSuccessStatisticsEightToEight.getOccourancesNum() + " occourances.");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append('\n');
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Percent: " + ((candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() == 0) ? "0" : ((candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() * 100) / candleHourSuccessStatisticsFourToFour.getOccourancesNum())) + "%");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("hour: 04:00-16:00");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Succeded " +  candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() + " from total of " + candleHourSuccessStatisticsFourToFour.getOccourancesNum() + " occourances.");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append('\n');
		if (commodity == null) {
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(',');
		} else {
			currentPinbarWriter.append(commodity);
			currentPinbarWriter.append(',');
			currentPinbarWriter.append(timeFrame);
		}
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Percent: " + ((candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() == 0) ? "0" : ((candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() * 100) / candleHourSuccessStatisticsFourToEight.getOccourancesNum())) + "%");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("hour: 04:00-20:00");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append("Succeded " +  candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() + " from total of " + candleHourSuccessStatisticsFourToEight.getOccourancesNum() + " occourances.");
		currentPinbarWriter.append(',');
		currentPinbarWriter.append('\n');
	}

}
