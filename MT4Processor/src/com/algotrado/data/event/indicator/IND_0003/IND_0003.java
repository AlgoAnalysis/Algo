package com.algotrado.data.event.indicator.IND_0003;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.util.Setting;

public class IND_0003 extends IDataExtractorSubject implements
IDataExtractorObserver{

	public static void main(String[] args) {
		//////// change hare ///////////////////
		DataSource dataSource = DataSource.FILE;
		AssetType assetType = AssetType.USOIL;
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		int depth = 12;
		double deviation = 5;
		int backstep = 3;
		int maxHstoryLength = 4;
		
		String filePath = "C:\\Algo\\test\\Zigzag_on_" + assetType.name()+".csv";
		///////////////////////////////////////
		IDataExtractorObserver dataRecorder;
		List<Double> parameters = new ArrayList<Double>();
		
		parameters.add((double)japaneseTimeFrameType.getValueInMinutes());
		parameters.add((double)depth);
		parameters.add(deviation);
		parameters.add((double)backstep);
		parameters.add((double)maxHstoryLength);
		dataRecorder = new FileDataRecorder(filePath, null);
		RegisterDataExtractor.register(dataSource, assetType, DataEventType.ZIGZAG, parameters, dataRecorder);	
	}
	
	
	private final DataEventType dataEventType = DataEventType.ZIGZAG;
	private IDataExtractorSubject dataExtractorSubject;
	private int maxHstoryLength;
	private double point;
	private int depth;
	private double deviation;
	private int backstep;
	private Date newUpdateDataTime;
	private SimpleUpdateData newUpdateData;
	private SimpleUpdateData dataHistory[];
	private int dataHistoryIndex;
	private JapaneseCandleBar candleBarHistory[];
	private int candleBarHistoryIndex;
	private boolean bufferFull;
	
	private Double japaneseCandleInterval;
	private double lowestBufferValue;
	private double highstBufferValue;
	private double lastLow;
	private double lastHigh;
	private double lowBackStepBuffer[];
	private double highBackStepBuffer[];
	private Date timeBackStepBuffer[];
	private int backStepIndex;
	private SimpleUpdateData lowLastData;
	private SimpleUpdateData highLastData;
	private boolean directionBullish;
	
	public IND_0003(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Double> parameters) {
		super(dataSource, assetType, dataEventType, parameters);
		point = assetType.getPoint();
		dataHistory = new SimpleUpdateData[maxHstoryLength];
		dataHistoryIndex = 0;
		candleBarHistoryIndex = 0;
		candleBarHistory = new JapaneseCandleBar[depth];
		bufferFull = false;
		backStepIndex = 0;
		lowBackStepBuffer = new double[backstep];
		highBackStepBuffer = new double[backstep];
		timeBackStepBuffer = new Date[backstep];
		for(int cnt= 0;cnt<backstep;cnt++)
		{
			lowBackStepBuffer[cnt] = 0;
			highBackStepBuffer[cnt] = 0;
		}
		List<Double> japaneseParameters = new ArrayList<Double>();
		japaneseParameters.add(japaneseCandleInterval);
		japaneseParameters.add((double)(depth));
		RegisterDataExtractor.register(dataSource,assetType,DataEventType.JAPANESE,japaneseParameters,this);
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Double> parameters) {
		// TODO Auto-generated method stub
		JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar) dataExtractorSubject.getNewData();
		if(bufferFull)
		{
			// update the buffer and the lowestBufferValue,highstBufferValue if necessary 
			boolean needUpdateLowestVal = (candleBarHistory[candleBarHistoryIndex].getLow() == lowestBufferValue);
			boolean needUpdateHighstVal = (candleBarHistory[candleBarHistoryIndex].getHigh() == highstBufferValue);
			candleBarHistory[candleBarHistoryIndex] = japaneseCandleBar;
			if(needUpdateLowestVal){
				updateLowest();
			}
			else if(lowestBufferValue > japaneseCandleBar.getLow()){
				lowestBufferValue = japaneseCandleBar.getLow();
			}
			if(needUpdateHighstVal){
				updateHighst();
			}
			else if(highstBufferValue < japaneseCandleBar.getHigh()){
				highstBufferValue = japaneseCandleBar.getHigh();
			}
			candleBarHistoryIndex = (candleBarHistoryIndex + 1)%depth;
			
			// start zigzag algorithm 
			// Low
			double val = lowestBufferValue;
			if(val == lastLow){
				val = 0;
			}
			else{
				lastLow = val;
				if((japaneseCandleBar.getLow() - val) > (deviation * point)){
					val=0;
				}
				else{
					for(int cnt = 0;cnt<backstep;cnt++)
					{
						if(lowBackStepBuffer[cnt] > val){
							lowBackStepBuffer[cnt] = 0;
						}
					}
				}
					
			}
			lowBackStepBuffer[backStepIndex] = val;
			
			// High
			val = highstBufferValue;
			if(val == lastHigh){
				val = 0;
			}
			else{
				lastHigh = val;
				if((val - japaneseCandleBar.getHigh() ) > (deviation * point)){
					val=0;
				}
				else{
					for(int cnt = 0;cnt<backstep;cnt++)
					{
						if(highBackStepBuffer[cnt] < val){
							highBackStepBuffer[cnt] = 0;
						}
					}
				}
					
			}
			highBackStepBuffer[backStepIndex] = val;	
			// time
			timeBackStepBuffer[backStepIndex] = japaneseCandleBar.getTime();
			// cutoff
			backStepIndex = (backStepIndex+1)%backstep;
			if((highBackStepBuffer[backStepIndex] != 0) && (!directionBullish || (highBackStepBuffer[backStepIndex]>highLastData.getValue())))
			{
				if(!directionBullish)
				{
					directionBullish = true;
					newUpdateData = lowLastData;
					newUpdateDataTime = japaneseCandleBar.getTime();
					addToHistory(lowLastData);
					notifyObservers(this.assetType, this.dataEventType, this.parameters);
				}
				highLastData = new SimpleUpdateData(assetType,timeBackStepBuffer[backStepIndex],highBackStepBuffer[backStepIndex]);
			}
			if((lowBackStepBuffer[backStepIndex] != 0) && (directionBullish || (lowBackStepBuffer[backStepIndex]<lowLastData.getValue())))
			{
				if(directionBullish)
				{
					directionBullish = false;
					newUpdateData = highLastData;
					addToHistory(highLastData);
					newUpdateDataTime = japaneseCandleBar.getTime();
					addToHistory(lowLastData);
					notifyObservers(this.assetType, this.dataEventType, this.parameters);
				}
				lowLastData = new SimpleUpdateData(assetType,timeBackStepBuffer[backStepIndex],lowBackStepBuffer[backStepIndex]);
			}			
		}else
		{
			candleBarHistory[candleBarHistoryIndex] = japaneseCandleBar;
			candleBarHistoryIndex++;
			if(candleBarHistoryIndex == depth)
			{
				bufferFull = true;
				candleBarHistoryIndex = 0;
				updateLowest();
				updateHighst();
				boolean needUpdateLow = false;
				boolean needUpdateHigh = false;
				int highLocation = 0;
				int lowLocation = 0;
				for(int cnt = 0;cnt < depth;cnt++)
				{
					JapaneseCandleBar candle = candleBarHistory[cnt];
					if(!needUpdateLow && (lowestBufferValue == candle.getLow()))
					{
						lowLastData = new SimpleUpdateData(assetType, candle.getTime(), lowestBufferValue);
						needUpdateLow = true;
						lowLocation = cnt;
					}
					if(!needUpdateHigh && (lowestBufferValue == candle.getLow()))
					{
						highLastData = new SimpleUpdateData(assetType, candle.getTime(), lowestBufferValue);
						needUpdateHigh = true;
						highLocation = cnt;
					}
				}
				directionBullish = (highLocation>lowLocation);
			}
		}
	}
	
	private void addToHistory(SimpleUpdateData newData)
	{
		dataHistory[dataHistoryIndex] = newData;
		dataHistoryIndex = (dataHistoryIndex+1)%maxHstoryLength;
	}
	
	private void updateLowest()
	{
		double lowestVal = Double.MAX_VALUE;
		for(JapaneseCandleBar candle:candleBarHistory)
		{
			if(candle.getLow() < lowestVal)
			{
				lowestVal = candle.getLow();
			}
		}
		lowestBufferValue = lowestVal;
	}
	
	private void updateHighst()
	{
		double highstVal = -Double.MAX_VALUE;
		for(JapaneseCandleBar candle:candleBarHistory)
		{
			if(candle.getHigh() > highstVal)
			{
				highstVal = candle.getHigh();
			}
		}
		highstBufferValue = highstVal;		
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;	
	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		setSubject(null);
	}

	@Override
	public NewUpdateData getNewData() {
		return newUpdateData;
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		japaneseCandleInterval = parameters.get(0);
		depth = parameters.get(1).intValue();
		deviation = parameters.get(2);
		backstep = parameters.get(3).intValue();
		maxHstoryLength = parameters.get(4).intValue();
	}

	@Override
	public String getDataHeaders() {
		SimpleUpdateData temp = new SimpleUpdateData(null,null,0);
		return "Asset," + assetType.name() + "\n" +
				"Interval," + japaneseCandleInterval+ "\n" + 
				"Data Source," + this.dataSource.toString() + "\n" +
				"Zigzag\n" +
				"Depth," + depth + "\n" +
				"Deviation." + deviation + "\n" +
				"backstep," + backstep + "\n" +
				temp.getDataHeaders() + "," + Setting.getDateTimeHeader("update at ");
	}
	
	@Override
	public String toString() {
		return newUpdateData.toString() + "," + Setting.getDateTimeFormat(newUpdateDataTime); 
	}

	@Override
	public SubjectState getSubjectState() {
		return dataExtractorSubject.getSubjectState();
	}

	public SimpleUpdateData[] getDataHistory()
	{
		SimpleUpdateData ret[] = new SimpleUpdateData[maxHstoryLength];
		for(int cnt =0;cnt < maxHstoryLength;cnt++)
		{
			ret[cnt] = dataHistory[(dataHistoryIndex + cnt)%maxHstoryLength];
		}
		return ret;
	}
}