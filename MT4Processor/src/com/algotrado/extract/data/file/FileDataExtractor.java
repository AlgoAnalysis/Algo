package com.algotrado.extract.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.CandleBarsCollection;
import com.algotrado.extract.data.DataEventType;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.LargerTimeFrameDataExtractor;
import com.algotrado.extract.data.NewUpdateData;
import com.algotrado.mt4.tal.strategy.check.pattern.SingleCandleBarData;

public class FileDataExtractor extends IDataExtractorSubject {
	private String filePath;
	CandleBarsCollection dataList;
	
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
					return new FileDataExtractor(assetType, dataEventType, parameters, root.getAbsolutePath() + File.separator + intervalFileNames[0]);
				}
			}
		}
		
		return new LargerTimeFrameDataExtractor(assetType, dataEventType, parameters);
	}

	public FileDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Float> parameters, String filePath) {
		super(assetType, dataEventType, parameters);
		this.filePath = filePath;
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
	      Double open = null, high = null, low = null, close = null, sma20 = null, bollinger20TopBand = null, bollinger20BottomBand = null,
	    		  sma10 = null, bollinger10TopBand = null, bollinger10BottomBand = null, rsi = null;
	      
	      //2014.04.15 04:00:00
	      SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	      SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm");
	      SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
	      while( stringRead != null )
	      {
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
