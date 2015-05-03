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

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseCandleDataExtractor;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.data.event.basic.minimal.time.frame.MinimalTimeFrame;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.util.Setting;

public class FileDataExtractor extends IDataExtractorSubject implements MinimalTimeFrame{
	public final DataEventType dataEventType = DataEventType.NEW_QUOTE;
	public final DataSource dataSource = DataSource.FILE;
	public static final int NUM_OF_MILLIS_IN_MINUTES = 1000 * 60;
	public static final int NUM_OF_MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	public static final int MAXIMUM_PADDING_TIME = NUM_OF_MILLIS_IN_DAY;
	private String filePath;
	//	private CandleBarsCollection dataList;
	private List<JapaneseCandleBar> dataList;
	private SubjectState subjectState;
	private JapaneseCandleBar prevCandle = null;
	private SimpleUpdateData newSimple;
	private int intervalTime = 1;
	
	private List<JapaneseCandleBar> recordDataList;
	public static void main(String [] args)
	{
		// Check that static method is working.
		List<Double> params = new ArrayList<Double>();
		params.add((double) 5.0);
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
	public static IDataExtractorSubject getSubjectDataExtractor(final AssetType assetType,DataEventType dataEventType,final List<Double> parameters, String dirPath){
		//		final AssetType assetTypeFinal = assetType;
		//		final List<Double> parametersFinal = parameters;
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
//						return name.endsWith(parameters.get(0).intValue() + ".csv");
						return name.endsWith("1.csv");
					}
				});
				if (intervalFileNames.length > 0) {
					return new FileDataExtractor(assetType, dataEventType, parameters, assetDir.getAbsolutePath() + File.separator + intervalFileNames[0],1);
				}
			}
		}

		return new JapaneseCandleDataExtractor(DataSource.FILE, assetType, dataEventType, parameters);
	}

	public FileDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Double> parameters, String filePath,int intervalTime) {
		super(DataSource.FILE,assetType, dataEventType, parameters);
		this.filePath = filePath;
		this.subjectState = SubjectState.RUNNING;
		this.intervalTime = intervalTime;
	}

	@Override
	public void run() {
		this.subjectState = SubjectState.RUNNING;
		if(recordDataList == null)
		{
			dataList = new ArrayList<JapaneseCandleBar>();
			recordDataList = new ArrayList<JapaneseCandleBar>();
			try
			{
				FileReader fr = new FileReader(filePath);
				BufferedReader br = new BufferedReader(fr);
				String stringRead = br.readLine();
				if (stringRead != null) {
					pipsValue = Double.valueOf(stringRead);
					//	    	  parameters.add(pipsValue);
					stringRead = br.readLine();
				}
				//	      int index = 0;
				String date = null;
				//	      String period = null;
				Double open = null, high = null, low = null, close = null;

				//2014.04.15 04:00:00
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				formatter.setTimeZone(Setting.getFileTimeZone());
				//	      SimpleDateFormat hourformatter = new SimpleDateFormat("HH:mm");
				//	      SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy");
				while( stringRead != null )
				{
					dataList.clear();
					StringTokenizer st = new StringTokenizer(stringRead, ";");
					date = st.nextToken( );
//					period = st.nextToken( ); 
					st.nextToken( ); 
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
						br.close();
						e.printStackTrace();
					}


					if (prevCandle != null) {
						if(prevCandle.getTime().after(formattedDate))
						{
							throw new RuntimeException("File error, the time are not in orders");
						}
						if(formattedDate.getTime() - prevCandle.getTime().getTime() < MAXIMUM_PADDING_TIME) 
						{
							long intervalPaddingTime = intervalTime * NUM_OF_MILLIS_IN_MINUTES;
							double preClose = prevCandle.getClose();
							for(long paddingTime = prevCandle.getTime().getTime() + intervalPaddingTime;paddingTime < formattedDate.getTime();paddingTime += intervalPaddingTime)
							{
								JapaneseCandleBar paddingCandle = new JapaneseCandleBar(preClose, preClose, preClose, preClose, 0, new Date(paddingTime), assetType.name());
								dataList.add(paddingCandle);
							}
						}
					}

					JapaneseCandleBar temp = new JapaneseCandleBar(open, close, high, low, volume, formattedDate, assetType.name());

					prevCandle = temp;

					dataList.add(temp);
					// I think that notifying the observers after each read may be bad due to large number of notifications in a short time.
					// However we do need to test this as well.
					// read the next line
					stringRead = br.readLine();
					for(JapaneseCandleBar newCandle:dataList)
					{
						recordDataList.add(newCandle);
					}
				}
				br.close( );

				//call all the observers when new data collection is ready.
				//	      notifyObservers(assetType, dataEventType, parameters);

			} catch (Exception e) {
				System.err.println("An error reading files has occoured\n");
				e.printStackTrace();
			}
			dataList = null;
			
		}
		int cnt;
		for(cnt = 0;cnt <recordDataList.size() - 1;cnt++)
		{
			candleToSimpleData(recordDataList.get(cnt),false);

		}
		
		candleToSimpleData(recordDataList.get(cnt),true);
	}

	private void candleToSimpleData(JapaneseCandleBar newCandle,boolean lastCandle) {
		double volume = newCandle.getVolume()/4;
		this.newSimple = new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getOpen(),volume);
		notifyObservers(assetType, dataEventType, parameters);
		this.newSimple = new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getHigh(),volume);
		notifyObservers(assetType, dataEventType, parameters);
		this.newSimple = new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getLow(),volume);
		notifyObservers(assetType, dataEventType, parameters);
		this.newSimple = new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getClose(),volume);
		if(lastCandle){
			this.subjectState = SubjectState.END_OF_LIFE;
		}
		notifyObservers(assetType, dataEventType, parameters);
	}

	@Override
	public NewUpdateData getNewData() {
		return newSimple;
	}

	@Override
	public String getDataHeaders() {
		SimpleUpdateData temp = new SimpleUpdateData(null,null,0,0);
		return "Asset," + assetType.name() + "\n" +
				"Interval," + JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
				"Data Source," + DataSource.FILE.toString() + "\n" + 
				temp.getDataHeaders();
	}

	@Override
	public String toString() {
		return newSimple.toString(); 
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}
	
	@Override
	public void unregisterObserver(IDataExtractorObserver observer) {
		this.observers.remove(observer);
		observer.removeSubject(this);
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		// no parameters
	}
}
