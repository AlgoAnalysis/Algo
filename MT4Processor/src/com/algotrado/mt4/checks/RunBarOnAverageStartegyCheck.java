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

import com.algotrado.mt4.impl.BarOnAverageStrategy;
import com.algotrado.mt4.impl.CandleDaySuccessStatistics;
import com.algotrado.mt4.impl.CandleHourSuccessStatistics;
import com.algotrado.mt4.impl.FileNameTimeFrame;
import com.algotrado.mt4.impl.GeneralBarStrategy;
import com.algotrado.mt4.impl.JapaneseCandleBar;

public class RunBarOnAverageStartegyCheck {

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
		      Double open = null, high = null, low = null, close = null, ma = null, bollingerTopBand = null, bollingerBottomBand = null;
		      
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
		        double volume = Double.valueOf(st.nextToken( ));
		        ma  =  Double.valueOf(st.nextToken( ));
		        bollingerTopBand = Double.valueOf(st.nextToken( ));
		        bollingerBottomBand = Double.valueOf(st.nextToken( ));
		        
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
		      	    FileWriter currentBarWriter = new FileWriter("C:\\Users\\ohad\\Google Drive\\מט''ח\\pinbar_calculation_results\\" + comodityName + "_pinbar_" + timeFrame + ".csv");
		      	    FileWriter totalResultsWriter = new FileWriter("C:\\Users\\ohad\\Google Drive\\מט''ח\\pinbarTotalResults.csv", appendTotalResults);
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
		    	    currentBarWriter.append("Did 1:1?");
		    	    currentBarWriter.append(',');
		    	    currentBarWriter.append("Did 2:1?");
		    	    currentBarWriter.append(',');
		    	    currentBarWriter.append("Did 3:1?");
		      	    currentBarWriter.append('\n');
			      
		      	  GeneralBarStrategy barStrategy = new BarOnAverageStrategy();
			      double pinbarIndex = 0, successfullPinbarsDidOneToOne = 0, successfullPinbarsDidTwoToOne = 0, successfullPinbarsDidThreeToOne = 0;
			      for (JapaneseCandleBar temp : datalist) {
			    	  if (barStrategy.isStrategyPattern(temp, candleBars, index, (fileName.contains("JPY")) ? 0.01 : 0.0001)) {
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
				        		
				        	    currentBarWriter.append(dateformatter.format(temp.getTime()));
				        	    currentBarWriter.append(',');
				        	    if (temp.getTime().getHours() == 12) {
				        	    	currentBarWriter.append("12:00");
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
				        	    boolean bullishPinbarStrategy = barStrategy.isLongStrategyPattern(temp, candleBars, index, (fileName.contains("JPY")) ? 0.01 : 0.0001);
								currentBarWriter.append(bullishPinbarStrategy ? "Long ↑" : "Short ↓");
				        	    currentBarWriter.append(',');
				        	    currentBarWriter.append("" + temp.getRisk());
				        	    currentBarWriter.append(',');
				        	    double pinbarGain = (double)Math.round( (bullishPinbarStrategy ? barStrategy.getLongGain(temp, candleBars, index) : barStrategy.getShortGain(temp, candleBars, index) ) * (double) 1000) / (double)1000;
								currentBarWriter.append("" + pinbarGain );
				        	    currentBarWriter.append(',');
				        	    currentBarWriter.append("" + (bullishPinbarStrategy ? barStrategy.getLongNumOfCandles(temp, candleBars, index) : barStrategy.getShortNumOfCandles(temp, candleBars, index) ) );
				        	    currentBarWriter.append(',');
				        	    currentBarWriter.append("" + Math.round(bullishPinbarStrategy ? barStrategy.getLongCorrectionBeforeHigh(temp, candleBars, index) : barStrategy.getShortCorrectionBeforeLow(temp, candleBars, index) ) );
				        	    currentBarWriter.append(',');
				        	    double riskGainRatio = (double)Math.round( (bullishPinbarStrategy ? barStrategy.getLongRiskGainRatio(temp, candleBars, index) : barStrategy.getShortRiskGainRatio(temp, candleBars, index)) * (double) 1000) / (double)1000;
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
								currentBarWriter.append("" + riskGainRatio);
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
			  	    
			  	    
			  	    if (timeFrame.equals(JapaneseCandleBar._4_HOUR)) {
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
		
			  	    } else if (timeFrame.equals(JapaneseCandleBar._1_DAY)) {
			  	    	
//			  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsEightToEight = new CandleHourSuccessStatistics(-1, 0, 0);
//			  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToFour = new CandleHourSuccessStatistics(-1, 0, 0);
//			  	    	CandleHourSuccessStatistics candleHourSuccessStatisticsFourToEight = new CandleHourSuccessStatistics(-1, 0, 0);
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
//					  	    if (candleHourSuccessStatistics.getHour() >= 8 && candleHourSuccessStatistics.getHour() <= 20) {
//					  	    	candleHourSuccessStatisticsEightToEight.setOccourancesNum(candleHourSuccessStatisticsEightToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//					  	    	candleHourSuccessStatisticsEightToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsEightToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//					  	    } 
//					  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 16){
//					  	    	candleHourSuccessStatisticsFourToFour.setOccourancesNum(candleHourSuccessStatisticsFourToFour.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//					  	    	candleHourSuccessStatisticsFourToFour.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToFour.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//					  	    }
//					  	    if (candleHourSuccessStatistics.getHour() >= 4 && candleHourSuccessStatistics.getHour() <= 20){
//					  	    	candleHourSuccessStatisticsFourToEight.setOccourancesNum(candleHourSuccessStatisticsFourToEight.getOccourancesNum() + candleHourSuccessStatistics.getOccourancesNum());
//					  	    	candleHourSuccessStatisticsFourToEight.setNumOfSuccessOccurances(candleHourSuccessStatisticsFourToEight.getNumOfSuccessOccurances() + candleHourSuccessStatistics.getNumOfSuccessOccurances());
//					  	    }
						}
				  	    
//				  	    writeSuccessRatesByHours(currentPinbarWriter,
//								candleHourSuccessStatisticsEightToEight,
//								candleHourSuccessStatisticsFourToFour,
//								candleHourSuccessStatisticsFourToEight, null, null);
//				  	    
//				  	    writeSuccessRatesByHours(totalResultsWriter,
//								candleHourSuccessStatisticsEightToEight,
//								candleHourSuccessStatisticsFourToFour,
//								candleHourSuccessStatisticsFourToEight, comodityName, timeFrame);
			  	    	
			  	    }
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
