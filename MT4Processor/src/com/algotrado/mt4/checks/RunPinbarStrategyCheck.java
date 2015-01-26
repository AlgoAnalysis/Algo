package com.algotrado.mt4.checks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.algotrado.mt4.impl.CandleDaySuccessStatistics;
import com.algotrado.mt4.impl.CandleHourSuccessStatistics;
import com.algotrado.mt4.impl.FileNameTimeFrame;
import com.algotrado.mt4.impl.JapaneseCandleBar;

public class RunPinbarStrategyCheck {

	//"C:\\pinbar_data\\" 
	private static final String PINBAR_DATA_FOLDER = "C:\\Users\\ohad\\AppData\\Roaming\\MetaQuotes\\Terminal\\29746934E106AEFC52FF48DA9C54503F\\MQL4\\Files\\pinbar_data\\";

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
				getPinbarStatisticsForFile(comodityName, listOfFiles[i].getName(), appendTotalResults);
			} 
		}
	  }

	public static void getPinbarStatisticsForFile(String comodityName, String fileName, boolean appendTotalResults) {
		List<JapaneseCandleBar> datalist = new ArrayList<JapaneseCandleBar>();
	    List<JapaneseCandleBar> tempDatalist = new ArrayList<JapaneseCandleBar>();
	    Map<Integer, CandleHourSuccessStatistics> intraDaystatistics = new HashMap<Integer, CandleHourSuccessStatistics>();
	    Map<Integer, CandleDaySuccessStatistics> daystatistics = new HashMap<Integer, CandleDaySuccessStatistics>();

	    try
	    {
	      FileReader fr = new FileReader(PINBAR_DATA_FOLDER + fileName);
	      BufferedReader br = new BufferedReader(fr);
	      String stringRead = br.readLine();
	      int index = 0;
	      String date = null, period = null;
	      Double open = null, high = null, low = null, close = null, ma = null;
	      
	      //2014.04.15 04:00:00
	      //SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	      SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	      SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm");
	      SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
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
	        ma  =  Double.valueOf(st.nextToken( ));
	        
	        Date formattedDate = null;
	        try {
				formattedDate = formatter.parse(date /*+ " " + hour*/);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        

	        JapaneseCandleBar temp = new JapaneseCandleBar(open, close, high, low, formattedDate, comodityName, ma);
	        System.out.println(temp);
	        datalist.add(temp);
	        
	        // read the next line
	        stringRead = br.readLine();
	      }
	      br.close( );
	      
	      JapaneseCandleBar[] candleBars = (JapaneseCandleBar[])datalist.toArray(new JapaneseCandleBar[0]);//new JapaneseCandleBar[datalist.size()];
	      String timeFrame = FileNameTimeFrame.getTimeFrame(candleBars, fileName);
	      
	      if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
		      JapaneseCandleBar sundayBar = null;
		      for (Iterator<JapaneseCandleBar> iterator = datalist.iterator(); iterator.hasNext();) {
				JapaneseCandleBar japaneseCandleBar = iterator.next();
				Calendar calendar = Calendar.getInstance();
	
				calendar.setTime(japaneseCandleBar.getTime());
	
			    if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			    	sundayBar = new JapaneseCandleBar(japaneseCandleBar);
			    	iterator.remove();
				} else if (sundayBar != null && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
					//add sunday candle to monday and remove the sunday candle.
					japaneseCandleBar = japaneseCandleBar.addPreviousJapaneseCandleBar(sundayBar);
					tempDatalist.add(japaneseCandleBar);
					sundayBar = null;
				} else {
					tempDatalist.add(japaneseCandleBar);
				}
		      }
		      datalist = tempDatalist;
		      candleBars = (JapaneseCandleBar[])datalist.toArray(new JapaneseCandleBar[0]);
	      }
	      
	      
	      System.out.println("Pinbars");
	      try
	      	{
	      	    FileWriter currentPinbarWriter = new FileWriter("C:\\Users\\ohad\\Google Drive\\�?ט''ח\\pinbar_calculation_results\\" + comodityName + "_pinbar_" + timeFrame + ".csv");
	      	    FileWriter totalResultsWriter = new FileWriter("C:\\Users\\ohad\\Google Drive\\�?ט''ח\\pinbarTotalResults.csv", appendTotalResults);
	      	    currentPinbarWriter.append("time");
	      	    currentPinbarWriter.append(',');
	      	    currentPinbarWriter.append("hour");
	      	    currentPinbarWriter.append(',');
	      	    currentPinbarWriter.append("time-frame");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("high");
	    	    currentPinbarWriter.append(',');
	      	    currentPinbarWriter.append("low");
	      	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("open");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("close");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("direction");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Risk");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Gain");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Num of Candles until success");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Correction");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Risk/Gain Ratio");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Did 1:1?");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Did 2:1?");
	    	    currentPinbarWriter.append(',');
	    	    currentPinbarWriter.append("Did 3:1?");
	      	    currentPinbarWriter.append('\n');
		      
		      double pinbarIndex = 0, successfullPinbarsDidOneToOne = 0, successfullPinbarsDidTwoToOne = 0, successfullPinbarsDidThreeToOne = 0;
		      for (JapaneseCandleBar temp : datalist) {
		    	  if (temp.pinbarStrategy.isStrategyPattern(temp, candleBars, index, (fileName.contains("JPY")) ? 0.0001 : 0.01)) {
			        	System.out.println(temp);
			        	
			        		if (timeFrame.equals(JapaneseCandleBar._4_HOUR)) {
			        			if (intraDaystatistics.get(temp.getTime().getHours()) == null) {
			        				intraDaystatistics.put(temp.getTime().getHours(), new CandleHourSuccessStatistics(temp.getTime().getHours(), 0, 0));
			        			}
			        			
			        		} else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
			        			if (daystatistics.get(temp.getTime().getDay()) == null) {
			        				daystatistics.put(temp.getTime().getDay(), new CandleDaySuccessStatistics(temp.getTime().getDay(), 0, 0));
			        			}
			        	    }
			        		
			        	    currentPinbarWriter.append(dateformatter.format(temp.getTime()));
			        	    currentPinbarWriter.append(',');
			        	    if (temp.getTime().getHours() == 12) {
			        	    	currentPinbarWriter.append("12:00");
			        	    } else {
			        	    	currentPinbarWriter.append(hourformatter.format(temp.getTime()));
			        	    }
			        	    currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append(timeFrame);
			                currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append(temp.getHigh() + "," + temp.getLow());
			                currentPinbarWriter.append(',');
			         
			        	    currentPinbarWriter.append("" + temp.getOpen());
			        	    currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append("" + temp.getClose());
			        	    currentPinbarWriter.append(',');
			        	    boolean bullishPinbarStrategy = temp.pinbarStrategy.isLongStrategyPattern(temp, candleBars, index, (fileName.contains("JPY")) ? 0.0001 : 0.01);
							currentPinbarWriter.append(bullishPinbarStrategy ? "Long ↑" : "Short ↓");
			        	    currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append("" + temp.getRisk());
			        	    currentPinbarWriter.append(',');
			        	    double pinbarGain = (double)Math.round( (bullishPinbarStrategy ? temp.pinbarStrategy.getLongGain(temp, candleBars, index) : temp.pinbarStrategy.getShortGain(temp, candleBars, index) ) * (double) 1000) / (double)1000;
							currentPinbarWriter.append("" + pinbarGain );
			        	    currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append("" + (bullishPinbarStrategy ? temp.pinbarStrategy.getLongNumOfCandles(temp, candleBars, index) : temp.pinbarStrategy.getShortNumOfCandles(temp, candleBars, index) ) );
			        	    currentPinbarWriter.append(',');
			        	    currentPinbarWriter.append("" + Math.round(bullishPinbarStrategy ? temp.pinbarStrategy.getLongCorrectionBeforeHigh(temp, candleBars, index) : temp.pinbarStrategy.getShortCorrectionBeforeLow(temp, candleBars, index) ) );
			        	    currentPinbarWriter.append(',');
			        	    double riskGainRatio = (double)Math.round( (bullishPinbarStrategy ? temp.pinbarStrategy.getLongRiskGainRatio(temp, candleBars, index) : temp.pinbarStrategy.getShortRiskGainRatio(temp, candleBars, index)) * (double) 1000) / (double)1000;
			        	    if (timeFrame.equals(JapaneseCandleBar._4_HOUR)) {
			        	    	intraDaystatistics.get(temp.getTime().getHours()).
			        	    		setOccourancesNum(intraDaystatistics.get(temp.getTime().getHours()).getOccourancesNum() + 1);
			        	    	if (riskGainRatio > 1) {
				        	    	intraDaystatistics.get(temp.getTime().getHours()).
				        	    		setNumOfSuccessOccurances(intraDaystatistics.get(temp.getTime().getHours()).getNumOfSuccessOccurances() + 1);
			        	    	}
			        	    	
			        	    } else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
			        	    	daystatistics.get(temp.getTime().getDay()).
		        	    		setOccourancesNum(daystatistics.get(temp.getTime().getDay()).getOccourancesNum() + 1);
			        	    	if (riskGainRatio > 1) {
			        	    		daystatistics.get(temp.getTime().getDay()).
				        	    		setNumOfSuccessOccurances(daystatistics.get(temp.getTime().getDay()).getNumOfSuccessOccurances() + 1);
			        	    	}
			        	    }
							currentPinbarWriter.append("" + riskGainRatio);
			        	    currentPinbarWriter.append(',');
			        	    boolean riskGainRatioBiggerThan1 = riskGainRatio >= 1;
			        	    successfullPinbarsDidOneToOne += riskGainRatioBiggerThan1 ? 1 : 0;
							currentPinbarWriter.append(riskGainRatioBiggerThan1 ? "1" : "0");
							currentPinbarWriter.append(',');
							currentPinbarWriter.append(riskGainRatio >= 2 ? "1" : "0");
							successfullPinbarsDidTwoToOne += (riskGainRatio >= 2) ? 1 : 0;
							currentPinbarWriter.append(',');
							currentPinbarWriter.append(riskGainRatio >= 3 ? "1" : "0");
							successfullPinbarsDidThreeToOne += (riskGainRatio >= 3) ? 1 : 0;
			        	    currentPinbarWriter.append('\n');
			        	    pinbarIndex++;
			      }
		    	  index++;
		      }
		      	writeSuccessPercentageAndExpectedValue(currentPinbarWriter,
						pinbarIndex, successfullPinbarsDidOneToOne,
						successfullPinbarsDidTwoToOne,
						successfullPinbarsDidThreeToOne, null, null);
		      	
		      	writeSuccessPercentageAndExpectedValue(totalResultsWriter,
						pinbarIndex, successfullPinbarsDidOneToOne,
						successfullPinbarsDidTwoToOne,
						successfullPinbarsDidThreeToOne, comodityName, timeFrame);
		  	    
		  	    
		  	    if (timeFrame.equals(JapaneseCandleBar._4_HOUR)) {
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight = new CandleHourSuccessStatistics(-1, 0, 0);
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour = new CandleHourSuccessStatistics(-1, 0, 0);
		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight = new CandleHourSuccessStatistics(-1, 0, 0);
			  	    for (CandleHourSuccessStatistics candleHourSuccessStatistics : intraDaystatistics.values()) {
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
				      	currentPinbarWriter.append(',');
				      	currentPinbarWriter.append("Percent: " + ((candleHourSuccessStatistics.getNumOfSuccessOccurances() * 100) / candleHourSuccessStatistics.getOccourancesNum()) + "%");
				      	currentPinbarWriter.append(',');
				      	currentPinbarWriter.append("hour:" +  candleHourSuccessStatistics.getHour() + ":00");
				  	    currentPinbarWriter.append(',');
				  	    currentPinbarWriter.append("Succeded " +  candleHourSuccessStatistics.getNumOfSuccessOccurances() + " from total of " + candleHourSuccessStatistics.getOccourancesNum() + " occourances.");
				  	    currentPinbarWriter.append(',');
				  	    currentPinbarWriter.append('\n');
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
			  	    
			  	    writeSuccessRatesByHours(currentPinbarWriter,
							candleHourSuccessStatisticsEightToEight,
							candleHourSuccessStatisticsFourToFour,
							candleHourSuccessStatisticsFourToEight, null, null);
			  	    
			  	    writeSuccessRatesByHours(totalResultsWriter,
							candleHourSuccessStatisticsEightToEight,
							candleHourSuccessStatisticsFourToFour,
							candleHourSuccessStatisticsFourToEight, comodityName, timeFrame);
	
		  	    } else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
		  	    	
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight = new CandleHourSuccessStatistics(-1, 0, 0);
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour = new CandleHourSuccessStatistics(-1, 0, 0);
//		  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight = new CandleHourSuccessStatistics(-1, 0, 0);
			  	    for (CandleDaySuccessStatistics candleDaySuccessStatistics : daystatistics.values()) {
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
				      	currentPinbarWriter.append(',');
				      	currentPinbarWriter.append("Percent: " + ((candleDaySuccessStatistics.getNumOfSuccessOccurances() * 100) / candleDaySuccessStatistics.getOccourancesNum()) + "%");
				      	currentPinbarWriter.append(',');
				      	currentPinbarWriter.append("Day:" +  candleDaySuccessStatistics.getDay() );
				  	    currentPinbarWriter.append(',');
				  	    currentPinbarWriter.append("Succeded " +  candleDaySuccessStatistics.getNumOfSuccessOccurances() + " from total of " + candleDaySuccessStatistics.getOccourancesNum() + " occourances.");
				  	    currentPinbarWriter.append(',');
				  	    currentPinbarWriter.append('\n');
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
		  	    	
		  	    }
		      	currentPinbarWriter.flush();
	  	    	currentPinbarWriter.close();
	  	    	
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
