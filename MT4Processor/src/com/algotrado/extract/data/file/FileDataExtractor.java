package com.algotrado.extract.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.algotrado.data.event.CandleBarsCollection;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.TimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.LargerTimeFrameDataExtractor;
import com.algotrado.extract.data.SubjectState;

public class FileDataExtractor extends IDataExtractorSubject {
	public static final int NUM_OF_MILLIS_IN_DAY = 60 * 1000 * 60 * 24;
	private String filePath;
	private CandleBarsCollection dataList;
	private SubjectState subjectState;
	private JapaneseCandleBar prevCandle = null;
	
	public static void main(String [] args)
	  {
		// Check that static method is working.
		List<Float> params = new ArrayList<Float>();
		params.add((float) 5.0);
		System.out.println(getSubjectDataExtractor(AssetType.USOIL, DataEventType.JAPANESE, params, "./root").getClass().getSimpleName());
	  }
	
	/**
	 * Should return correct subject file data extractor / or larger time frame data extractor. 
	 * @param assetType
	 * @param dataEventType
	 * @param parameters
	 * @param observer
	 * @param dirPath
	 * @return
	 */
	public static IDataExtractorSubject getSubjectDataExtractor(final AssetType assetType,DataEventType dataEventType,final List<Float> parameters, String dirPath){
//		final AssetType assetTypeFinal = assetType;
//		final List<Float> parametersFinal = parameters;
		if (dirPath == null || dirPath.equals("")) {
			throw new RuntimeException("FileDataExtractor = > getSubjectDataExtractor() : dirPath must not be null.");
		}
		File root = new File(dirPath);
		if (!root.exists())  {
			throw new RuntimeException("FileDataExtractor = > getSubjectDataExtractor() : dirPath = " + dirPath + " , does not exist.");
		}
		
		String [] fileNames = root.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(assetType.name());
			}
		});
		
		for (String assetName : fileNames) {
			File assetDir = new File(root.getAbsolutePath() + File.separator + assetName);
			if (assetDir.isDirectory()) {
				String [] intervalFileNames = assetDir.list(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(parameters.get(0).intValue() + ".csv");
					}
				});
				if (intervalFileNames.length > 0) {
					return new FileDataExtractor(assetType, dataEventType, parameters, assetDir.getAbsolutePath() + File.separator + intervalFileNames[0]);
				}
			}
		}
		
		return new LargerTimeFrameDataExtractor(DataSource.FILE, assetType, dataEventType, parameters);
	}

	public FileDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Float> parameters, String filePath) {
		super(DataSource.FILE,assetType, dataEventType, parameters);
		this.filePath = filePath;
		this.subjectState = SubjectState.RUNNING;
	}

	@Override
	public void run() {
		dataList = new CandleBarsCollection();
		try
	    {
	      FileReader fr = new FileReader(filePath);
	      BufferedReader br = new BufferedReader(fr);
	      String stringRead = br.readLine();
	      if (stringRead != null) {
	    	  pipsValue = Float.valueOf(stringRead);
//	    	  parameters.add(pipsValue);
	    	  stringRead = br.readLine();
	      }
	      int index = 0;
	      String date = null, period = null;
	      Double open = null, high = null, low = null, close = null;
	      
	      //2014.04.15 04:00:00
	      SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
//	      SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm");
//	      SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
	      while( stringRead != null )
	      {
	    	dataList.getCandleBars().clear();
	    	StringTokenizer st = new StringTokenizer(stringRead, ";");
	        date = st.nextToken( );
	        period = st.nextToken( );  
	        open = Double.valueOf(st.nextToken( )); 
	        high = Double.valueOf(st.nextToken( ));
	        low = Double.valueOf(st.nextToken( ));  
	        close = Double.valueOf(st.nextToken( ));
	        double volume = Double.valueOf(st.nextToken( ));
	       
//	        bollinger20TopBand = Double.valueOf(st.nextToken( ));
//	        sma20  =  Double.valueOf(st.nextToken( ));
//	        bollinger20BottomBand = Double.valueOf(st.nextToken( ));
//	        bollinger10TopBand = Double.valueOf(st.nextToken( ));
//	        sma10  =  Double.valueOf(st.nextToken( ));
//	        bollinger10BottomBand = Double.valueOf(st.nextToken( ));
//	        rsi = Double.valueOf(st.nextToken( ));
	        
	        Date formattedDate = null;
	        try {
				formattedDate = formatter.parse(date /*+ " " + hour*/);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	        

	        if (prevCandle != null) {
	        	//check for time gaps and add padding. Note: This solution only supports 1 minute timeframe input file. => we should check in the future if this is good.
	        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
	    		calendar.setTime(formattedDate);
	    		Calendar prevCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
	    		prevCalendar.setTime(prevCandle.getTime());
	    		Calendar originalPrevCalendar = GregorianCalendar.getInstance(); // creates a new calendar instance
	    		originalPrevCalendar.setTime(prevCandle.getTime());
	    		// If gap is more than 24 hour do not fill the gap
	    		if ((formattedDate.getTime() - prevCandle.getTime().getTime()) < NUM_OF_MILLIS_IN_DAY &&
    				formattedDate.after(prevCandle.getTime())) { // add padding
	    			for (int dayIndex = prevCalendar.get(Calendar.DAY_OF_YEAR); dayIndex <= calendar.get(Calendar.DAY_OF_YEAR); dayIndex++) {
	    				prevCalendar.set(Calendar.DAY_OF_YEAR, dayIndex);
	    				int maxHour = (dayIndex == calendar.get(Calendar.DAY_OF_YEAR)) ? calendar.get(Calendar.HOUR_OF_DAY) : 23;
//	    				int maxMinute = calendar.get(Calendar.MINUTE) - 1;
	    				for (int hourIndex = prevCalendar.get(Calendar.HOUR_OF_DAY); hourIndex <= maxHour; hourIndex++) {
	    					prevCalendar.set(Calendar.HOUR_OF_DAY, hourIndex);
	    					int maxMinute = (dayIndex == calendar.get(Calendar.DAY_OF_YEAR) && hourIndex == calendar.get(Calendar.HOUR_OF_DAY)) ? (calendar.get(Calendar.MINUTE) - 1) : 59;
	    					int startMinute = (dayIndex == prevCalendar.get(Calendar.DAY_OF_YEAR) && hourIndex == originalPrevCalendar.get(Calendar.HOUR_OF_DAY))? 
	    							prevCalendar.get(Calendar.MINUTE) + 1 : 0;
	    					for(int minuteIndex = startMinute; minuteIndex <= maxMinute; minuteIndex++) {
	    						prevCalendar.set(Calendar.MINUTE, minuteIndex);
	    						JapaneseCandleBar paddingCandle = new JapaneseCandleBar(prevCandle.getClose(), prevCandle.getClose(), prevCandle.getClose(), prevCandle.getClose(), 0, prevCalendar.getTime(), assetType.name());
	    						dataList.addCandleBar(paddingCandle);
	    					}
	    					prevCalendar.set(Calendar.MINUTE, 0);
	    				}
	    				prevCalendar.set(Calendar.HOUR_OF_DAY, 0);
	    			}
	    		}
	        }

	        JapaneseCandleBar temp = new JapaneseCandleBar(open, close, high, low, volume, formattedDate, assetType.name());
	        
	        prevCandle = temp;
	        
	        dataList.addCandleBar(temp);
	        // I think that notifying the observers after each read may be bad due to large number of notifications in a short time.
	        // However we do need to test this as well.
	        // read the next line
	        stringRead = br.readLine();
	        
	        if (stringRead == null) {
	        	this.subjectState = SubjectState.END_OF_LIFE;
	        }
	        
	        notifyObservers(assetType, dataEventType, parameters);
	      }
	      br.close( );
	      
	      //call all the observers when new data collection is ready.
//	      notifyObservers(assetType, dataEventType, parameters);
	      
	    } catch (Exception e) {
	    	System.err.println("An error reading files has occoured\n");
	    	e.printStackTrace();
	    }
	      
	}
	

	@Override
	public NewUpdateData getNewData() {
		return dataList/*.getCandleBars().get(dataList.getCandleBars().size() - 1)*/;
	}

	@Override
	public String getDataHeaders() {
		return "Asset," + assetType.name() + "\n" +
				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.toString() + "\n" + 
				"Date,Time, " + getNewData().getDataHeaders();
	}
	
	@Override
	public String toString() {
		String toStringRet = null;
		for (Iterator<JapaneseCandleBar> jpnCandleIterator = dataList.getCandleBars().iterator(); jpnCandleIterator.hasNext(); ) {
			if (toStringRet == null) {
				toStringRet = "";
			}
			JapaneseCandleBar candle = jpnCandleIterator.next();
			SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
			toStringRet += dateformatter.format(candle.getTime()) + " , " + candle.getOpen() + " , " + candle.getHigh() + " , " 
					+ candle.getLow() + " , " + candle.getClose() + " , " + candle.getVolume() + ((!jpnCandleIterator.hasNext()) ? "" : "\n");
		}
		return toStringRet; 
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}

}
