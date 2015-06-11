package com.algotrado.extract.data.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.SwingUtilities;

import com.algotrado.broker.Account;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.data.event.basic.minimal.time.frame.MinimalTimeFrame;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionOrderStatusType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.util.Setting;

public class FileDataExtractor extends IDataExtractorSubject implements MinimalTimeFrame{
	public final DataEventType dataEventType = DataEventType.NEW_QUOTE;
	public final DataSource dataSource = DataSource.FILE;
	public static final int NUM_OF_MILLIS_IN_MINUTES = 1000 * 60;
	public static final int NUM_OF_MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
	public static final int MAXIMUM_PADDING_TIME = NUM_OF_MILLIS_IN_DAY;
	private static Map<AssetType,FileDataExtractor> fileDataExtractorList;
	private static Integer nextPositionId;
	private static Map<Integer,FileTrade> tradeList;
	private static FileAccount fileAccount;

	private String filePath;
	//	private CandleBarsCollection dataList;
	private List<JapaneseCandleBar> dataList;
	private SubjectState subjectState;
	private JapaneseCandleBar prevCandle = null;
	private SimpleUpdateData newSimple;
	private int intervalTime;
	private List<SimpleUpdateData> recordDataList;
	private int recordDataIndex;
	private double minimumContractAmountMultiply = 1; // TODO
	private double contractAmount = 500; //TODO  
	private double spread = 0; // TODO : This fix was made to fit tal excel results.
	private List<FileTrade> assetTradeList;
	boolean runNewTask = false;
	
	public static void main(String [] args)
	{
		// Check that static method is working.
		List<Double> params = new ArrayList<Double>();
		params.add((double) 5.0);
		System.out.println(getSubjectDataExtractor(AssetType.USOIL, DataEventType.JAPANESE, params, "./root").getClass().getSimpleName());
	}

	static {
		fileDataExtractorList = new HashMap<AssetType,FileDataExtractor>();
		resetAccount();
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

		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(assetType);
		if(fileDataExtractor == null)
		{
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
							return name.endsWith("1.csv");
						}
					});
					if (intervalFileNames.length > 0) {
						fileDataExtractor =  new FileDataExtractor(assetType, dataEventType, parameters, assetDir.getAbsolutePath() + File.separator + intervalFileNames[0]);
						break;
					}
				}
			}
			if(fileDataExtractor != null)
			{
				fileDataExtractorList.put(assetType, fileDataExtractor);
			}
			else
			{
				throw new RuntimeException("File not exisit or not in expected interval!!!");
			}
		}		
		return fileDataExtractor;
	}
	
	public static void resetAccount()
	{
		fileAccount = new FileAccount(1E9,100,100,30);
		nextPositionId = 1;
		tradeList = new HashMap<Integer,FileTrade>();
		for(FileDataExtractor fileDataExtractor :fileDataExtractorList.values())
		{
			fileDataExtractor.resetSubjectDataExtractor();
		}
	}
	
	public static double getMinimumContractAmountMultiply(AssetType asset)
	{
		double minimumContractAmountMultiply = -1;
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(asset);
		if(fileDataExtractor != null)
		{
			minimumContractAmountMultiply = fileDataExtractor.minimumContractAmountMultiply;
		}		
		return minimumContractAmountMultiply;
	}
	
	public static double getContractAmount(AssetType asset)
	{
		double minimumContractAmountMultiply = -1;
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(asset);
		if(fileDataExtractor != null)
		{
			minimumContractAmountMultiply = fileDataExtractor.contractAmount;
		}		
		return minimumContractAmountMultiply;
	}
	
	public static int openPosition(AssetType asset, double contractAmount,PositionDirectionType direction, double stopLoss,double takeProfit)
	{
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(asset);
		if(fileDataExtractor == null)
		{
			return -1;
		}
		double numberOfMinContract = contractAmount/fileDataExtractor.minimumContractAmountMultiply;
		if((int)numberOfMinContract != numberOfMinContract)
		{
			return -1;
		}
		double assetPrice = fileDataExtractor.newSimple.getValue();
		if(!fileAccount.weCanEnter(assetPrice*fileDataExtractor.contractAmount*contractAmount))
		{
			return -1;
		}
		
		FileTrade fileTrade = new FileTrade(fileAccount,asset,fileDataExtractor.contractAmount * contractAmount, assetPrice,fileDataExtractor.newSimple.getTime(), stopLoss, takeProfit, direction, fileDataExtractor.spread);
		fileDataExtractor.assetTradeList.add(fileTrade);
		tradeList.put(nextPositionId, fileTrade);
		nextPositionId++;
		return nextPositionId - 1;
	}
	
	public static double getLiveSpread(AssetType asset) {
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(asset);
		if(fileDataExtractor == null)
		{
			return -1;
		}
		return fileDataExtractor.spread;
	}
	
	public static boolean closePosition(int positionId, double amountToClose)
	{
		Integer objPositionId = positionId;
		FileTrade fileTrade = tradeList.get(objPositionId);
		if(fileTrade == null)
		{
			return false;
		}
		if(fileTrade.getStatus() != FileTradeStatus.OPEN)
		{
			return false;
		}
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(fileTrade.getAssetType());
		if(fileDataExtractor == null)
		{
			throw new RuntimeException("The trade need to be in this list. need to find the bug!!!");
		}
		
		fileTrade.setAmount(fileDataExtractor.contractAmount * amountToClose);
		return true;
	}
	
	public static boolean closePosition(int positionId)
	{
		Integer objPositionId = positionId;
		FileTrade fileTrade = tradeList.get(objPositionId);
		if(fileTrade == null)
		{
			return false;
		}
		if(fileTrade.getStatus() != FileTradeStatus.OPEN)
		{
			return false;
		}
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(fileTrade.getAssetType());
		if(fileDataExtractor == null)
		{
			throw new RuntimeException("The trade need to be in this list. need to find the bug!!!");
		}
		
		fileTrade.closePosition();
		return true;
	}
	
	public static boolean modifyPosition(int positionId, double newStopLoss,double newTakeProfit)
	{
		Integer objPositionId = positionId;
		FileTrade fileTrade = tradeList.get(objPositionId);
		if(fileTrade == null)
		{
			return false;
		}
		if(fileTrade.getStatus() != FileTradeStatus.OPEN)
		{
			return false;
		}
		FileDataExtractor fileDataExtractor = fileDataExtractorList.get(fileTrade.getAssetType());
		if(fileDataExtractor == null)
		{
			throw new RuntimeException("The trade need to be in this list. need to find the bug!!!");
		}
		fileTrade.modifyPosition(newStopLoss,newTakeProfit);
		return true;
	}
	
	public static PositionStatus getPositionStatus(int positionId)
	{
		Integer objPositionId = positionId;
		FileTrade fileTrade = tradeList.get(objPositionId);
		if(fileTrade == null)
		{
			return null;
		}
		return new PositionStatus(fileTrade.getDirection(), fileTrade.getEntryPrice(), fileTrade.getCurrentPrice(), fileTrade.getStatus().getPositionOrderStatusType(), fileTrade.getTime());
	}
	
	public static double getCurrentAskPrice(AssetType asset)
	{
		FileDataExtractor fileDataExtractor= fileDataExtractorList.get(asset);
		if(fileDataExtractor == null)
		{
			throw new RuntimeException("Assent not observed");
		}
		
		return fileDataExtractor.newSimple.getValue();
			
		
	}
	
	public static Account getAccountStatus()
	{
		return fileAccount;
	}
	
	private void resetSubjectDataExtractor()
	{
		assetTradeList = new ArrayList<FileTrade>();
		recordDataIndex = 0;
	}

	public FileDataExtractor(AssetType assetType, DataEventType dataEventType,
			List<Double> parameters, String filePath) {
		super(DataSource.FILE,assetType, dataEventType, parameters);
		this.filePath = filePath;
		this.subjectState = SubjectState.RUNNING;
		intervalTime = 1;
		readFileToMemory();
		resetSubjectDataExtractor();
	}

	private void readFileToMemory()
	{
		List<JapaneseCandleBar> japaneseRecordDataList;
		dataList = new ArrayList<JapaneseCandleBar>();
		japaneseRecordDataList = new ArrayList<JapaneseCandleBar>();
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
				//				period = st.nextToken( ); 
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
					japaneseRecordDataList.add(newCandle);
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
		
		recordDataList = new ArrayList<SimpleUpdateData>();
		for(JapaneseCandleBar newCandle:japaneseRecordDataList)
		{
			double volume = newCandle.getVolume()/4;
			recordDataList.add(new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getOpen(),volume));
			recordDataList.add(new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getHigh(),volume));
			recordDataList.add(new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getLow(),volume));
			recordDataList.add(new SimpleUpdateData(assetType,newCandle.getTime(),newCandle.getClose(),volume));			
		}
	}

	@Override
	public void run() {
		subjectState = SubjectState.RUNNING;
		int recordDataSize = recordDataList.size();
		for(recordDataIndex = 0;recordDataIndex <recordDataSize - 1;recordDataIndex++)
		{
			internalNotifyObservers();
		}
		subjectState = SubjectState.END_OF_LIFE;
		internalNotifyObservers();
		runNewTask = false;
		this.observers = new ConcurrentLinkedQueue<IDataExtractorObserver>();
	}
	
	public void internalNotifyObservers()
	{
		newSimple = recordDataList.get(recordDataIndex);
		for(Iterator<FileTrade> iterator = assetTradeList.iterator(); iterator.hasNext() ;)
		{
			FileTrade trade = iterator.next();
			if (trade.getStatus().getPositionOrderStatusType() == PositionOrderStatusType.CLOSED)
			{
				iterator.remove();
				continue;  
			}
			trade.updatePriceTrade(newSimple.getValue(), spread);
		}
		notifyObservers(assetType, dataEventType, parameters);
		
	}
	
	@Override
	public IDataExtractorSubject registerObserver(IDataExtractorObserver observer) {
		
		if (!findElementInCollection(this.observers,observer)) {
			this.observers.add(observer);
			observer.setSubject(this);
		}
		if (runNewTask == false) {
			runNewTask = true;
			SwingUtilities.invokeLater(this);
//			new Thread(this).run();
		}
		return this;
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

	@Override // TODO - need to check if we need this override
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
