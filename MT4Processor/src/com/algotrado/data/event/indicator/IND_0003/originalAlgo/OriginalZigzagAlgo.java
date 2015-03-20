package com.algotrado.data.event.indicator.IND_0003.originalAlgo;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;
import com.algotrado.output.file.FileDataRecorder;

public class OriginalZigzagAlgo extends IDataExtractorSubject implements
		IDataExtractorObserver {

	public static void main(String[] args) {
		
		////////change hare ///////////////////
		DataSource dataSource = DataSource.FILE;
		AssetType assetType = AssetType.USOIL;
		JapaneseTimeFrameType japaneseTimeFrameType = JapaneseTimeFrameType.FIVE_MINUTE;
		int depth = 12;
		double deviation = 5;
		int backstep = 3;
		double point = 0.001;
		String filePath = "C:\\Algo\\test\\zigzag_original.csv";
		////////////////////////////////////////////////////
		
		List<Double> parameters =new ArrayList<Double>();
		parameters.add((double)japaneseTimeFrameType.getValueInMinutes());
		parameters.add((double)depth);
		parameters.add(deviation);
		parameters.add((double)backstep);
		parameters.add(point);
		
		@SuppressWarnings("unused")
		OriginalZigzagAlgo algo = new OriginalZigzagAlgo(dataSource, assetType,DataEventType.ZIGZAG, parameters,filePath);
	}

	private Double japaneseCandleInterval;
	private int extDepth;
	private double extDeviation;
	private int extBackstep;
	private double point;
	
	FileDataRecorder dataRecorder;
	List<JapaneseCandleBar> candleBars;
	SubjectState subjectState;
	double low[];
	double high[];
	double extMapBuffer[];
	double extMapBuffer2[];
	private IDataExtractorSubject dataExtractorSubject;
	int outputIndex;
	int bars;
	
	double lowValue[];
	double lastLow[];
	double firstLowBuffer[];
	double secondLowBuffer[];
	double highValue[];
	double lastHigh[];
	double firstHighBuffer[];
	double secondHighBuffer[];
	double lowBufferCutting[];
	double highBufferCutting[];
	
	public OriginalZigzagAlgo(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Double> parameters,String filePath) {
		super(dataSource, assetType, dataEventType, parameters);
		
		candleBars = new ArrayList<JapaneseCandleBar>();
		subjectState = SubjectState.RUNNING;
		
		
		List<Double> japaneseParameters = new ArrayList<Double>();
		japaneseParameters.add(japaneseCandleInterval);
		japaneseParameters.add((double)0);
		RegisterDataExtractor.register(dataSource,assetType,DataEventType.JAPANESE,japaneseParameters,this);
		
		dataRecorder = new FileDataRecorder(filePath, null);
		registerObserver(dataRecorder);
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Double> parameters) {
		JapaneseCandleBar japaneseCandleBar = (JapaneseCandleBar) dataExtractorSubject.getNewData();
		candleBars.add(japaneseCandleBar);
		if(dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE)
		{
			bars = candleBars.size();
			lowValue = new double[bars];
			lastLow = new double[bars];
			firstLowBuffer = new double[bars];
			secondLowBuffer = new double[bars];
			highValue = new double[bars];
			lastHigh = new double[bars];
			firstHighBuffer = new double[bars];
			secondHighBuffer = new double[bars];
			lowBufferCutting = new double[bars];
			highBufferCutting = new double[bars];
			low = new double[bars];
			high = new double[bars];
			extMapBuffer = new double[bars];
			extMapBuffer2 = new double[bars];
			for(int cnt = 0;cnt < candleBars.size();cnt++)
			{
				low[cnt] = candleBars.get(candleBars.size()-1-cnt).getLow();
				high[cnt] = candleBars.get(candleBars.size()-1-cnt).getHigh();
			}
			start();
			for(outputIndex = 0;outputIndex <bars-1;outputIndex++)
			{
				notifyObservers(this.assetType, this.dataEventType, this.parameters);
			}
			subjectState = SubjectState.END_OF_LIFE;
			notifyObservers(this.assetType, this.dataEventType, this.parameters);
		}

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
		return null; // not need the record file work with the functions getDataHeaders and toString
	}

	@Override
	public DataEventType getDataEventType() {
		return DataEventType.ZIGZAG;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		japaneseCandleInterval = parameters.get(0);
		extDepth = parameters.get(1).intValue();
		extDeviation = parameters.get(2);
		extBackstep = parameters.get(3).intValue();
		point = parameters.get(4);
	}

	@Override
	public String getDataHeaders() {

		String ret = "Zigzag\n" + "Depth," + extDepth + "\nDeviation," + extDeviation + "\nBackstep," + extBackstep+"\n";
		ret += dataExtractorSubject.getDataHeaders();
		ret += ",Low value,last low,first low buffer,second low buffer,high value,last high,first high buffer,second high buffer,low buffer cutting,high buffer cutting,zigzag";
		return ret;
	}
	@Override
	public String toString()
	{
		int bufferLocation = candleBars.size() - outputIndex-1;
		String ret = candleBars.get(outputIndex).toString();
		ret +="," + lowValue[bufferLocation];
		ret +="," + lastLow[bufferLocation];
		ret +="," + firstLowBuffer[bufferLocation];
		ret +="," + secondLowBuffer[bufferLocation];
		ret +="," + highValue[bufferLocation];
		ret +="," + lastHigh[bufferLocation];
		ret +="," + firstHighBuffer[bufferLocation];
		ret +="," + secondHighBuffer[bufferLocation];
		ret +="," + lowBufferCutting[bufferLocation];
		ret +="," + highBufferCutting[bufferLocation];
		ret +="," + extMapBuffer[bufferLocation];
		return ret;
	}

	@Override
	public SubjectState getSubjectState() {
		return subjectState;
	}
	
	
	
	/////////////////////////////////// original algorithm ////////////////////////////////////////
	int Lowest(double buffer[],int depth,int shift)
	{
		int lowastLocation = shift;
		double lowstVal = buffer[shift];
		for(int cnt = shift+1;cnt < shift + depth;cnt++)
		{
			if(buffer[cnt]<lowstVal)
			{
				lowstVal = buffer[cnt];
				lowastLocation = cnt;
			}
		}
		return lowastLocation;			
	}
	int Highest(double buffer[],int depth,int shift)
	{
		int highstLocation = shift;
		double highstVal = buffer[shift];
		for(int cnt = shift+1;cnt < shift + depth;cnt++)
		{
			if(buffer[cnt]>highstVal)
			{
				highstVal = buffer[cnt];
				highstLocation = cnt;
			}
		}
		return highstLocation;			
	}
	void start()
	  {
	   int    shift, back,lasthighpos,lastlowpos;
	   double val,res;
	   double curlow,curhigh,lasthigh = 0,lastlow = 0;

	   for(shift=bars-extDepth; shift>=0; shift--)
	     {
	      val=low[Lowest(low,extDepth,shift)];
	      lowValue[shift] = val;
	      if(val==lastlow) val=0.0;
	      else 
	        { 
	         lastlow=val; 
	         if((low[shift]-val)>(extDeviation*point)) val=0.0;
	         else
	           {
	            for(back=1; back<=extBackstep; back++)
	              {
	               res=extMapBuffer[shift+back];
	               if((res!=0)&&(res>val)) 
	            	   {
	            	   	extMapBuffer[shift+back]=0.0;
	            	   	secondLowBuffer[shift+back] = 0;
	            	   }
	              }
	           }
	        }
	      lastLow[shift] = lastlow;
	      firstLowBuffer[shift] = val;
	      secondLowBuffer[shift] = val;
	      
	      extMapBuffer[shift]=val;
	      //--- high
	      val=high[Highest(high,extDepth,shift)];
	      highValue[shift] = val;
	      if(val==lasthigh) val=0.0;
	      else 
	        {
	         lasthigh=val;
	         if((val-high[shift])>(extDeviation*point)) val=0.0;
	         else
	           {
	            for(back=1; back<=extBackstep; back++)
	              {
	               res=extMapBuffer2[shift+back];
	               if((res!=0)&&(res<val)) {
	            	   extMapBuffer2[shift+back]=0.0;
	            	   secondHighBuffer[shift+back] = 0;
	               }
	              } 
	           }
	        }
	      lastHigh[shift] = lasthigh;
	      firstHighBuffer[shift] = val;
	      secondHighBuffer[shift] = val;
	      extMapBuffer2[shift]=val;
	     }

	   // final cutting 
	   lasthigh=-1; lasthighpos=-1;
	   lastlow=-1;  lastlowpos=-1;

	   for(shift=bars-extDepth; shift>=0; shift--)
	     {
	      curlow=extMapBuffer[shift];
	      curhigh=extMapBuffer2[shift];
	      if((curlow==0)&&(curhigh==0)) continue;
	      //---
	      if(curhigh!=0)
	        {
	         if(lasthigh>0) 
	           {
	            if(lasthigh<curhigh) extMapBuffer2[lasthighpos]=0;
	            else extMapBuffer2[shift]=0;
	           }
	         //---
	         if(lasthigh<curhigh || lasthigh<0)
	           {
	            lasthigh=curhigh;
	            lasthighpos=shift;
	           }
	         lastlow=-1;
	        }
	      //----
	      if(curlow!=0)
	        {
	         if(lastlow>0)
	           {
	            if(lastlow>curlow) extMapBuffer[lastlowpos]=0;
	            else extMapBuffer[shift]=0;
	           }
	         //---
	         if((curlow<lastlow)||(lastlow<0))
	           {
	            lastlow=curlow;
	            lastlowpos=shift;
	           } 
	         lasthigh=-1;
	        }
	      lowBufferCutting[shift] = extMapBuffer[shift];
	      highBufferCutting[shift] = extMapBuffer2[shift];
	     }
	  
	   for(shift=bars-1; shift>=0; shift--)
	     {
	      if(shift>=bars-extDepth) extMapBuffer[shift]=0.0;
	      else
	        {
	         res=extMapBuffer2[shift];
	         if(res!=0.0) extMapBuffer[shift]=res;
	        }
	     }
	  }
}
