package com.algotrado.extract.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class FileDataExtractor extends IDataExtractorSubject {
	private String filePath;
	CandleBarsCollection dataList;

	public FileDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Float> parameters, String filePath) {
		super(assetType, dataEventType, parameters);
		this.filePath = filePath;
	}

	@Override
	public void run() {
//		List<SingleCandleBarData> datalist = new ArrayList<SingleCandleBarData>();
		dataList = new CandleBarsCollection();

	    try
	    {
	      FileReader fr = new FileReader(filePath);
	      BufferedReader br = new BufferedReader(fr);
	      String stringRead = br.readLine();
	      int index = 0;
	      String date = null, period = null;
	      Double open = null, high = null, low = null, close = null, sma20 = null, bollinger20TopBand = null, bollinger20BottomBand = null,
	    		  sma10 = null, bollinger10TopBand = null, bollinger10BottomBand = null, rsi = null;
	      
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
	        

	        SingleCandleBarData temp = new SingleCandleBarData(open, close, high, low, formattedDate, assetType.name(), sma20, bollinger20BottomBand, bollinger20TopBand,
	        		sma10, bollinger10BottomBand, bollinger10TopBand, rsi);
	        System.out.println(temp);
	        dataList.addCandleBar(temp);
	        // I think that notifying the observers after each read may be bad due to large number of notifications in a short time.
	        // However we do need to test this as well.
//	        notifyObservers(assetType, dataEventType, parameters);
	        
	        // read the next line
	        stringRead = br.readLine();
	      }
	      br.close( );
	      
	      //call all the observers when new data collection is ready.
	      notifyObservers(assetType, dataEventType, parameters);
	      
	    } catch (Exception e) {
	    	System.err.println("An error reading files has occoured\n");
	    	e.printStackTrace();
	    }
	      
	}

	@Override
	public NewUpdateData getNewData() {
		return dataList;
	}

}
