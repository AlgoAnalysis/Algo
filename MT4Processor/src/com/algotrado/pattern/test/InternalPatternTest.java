package com.algotrado.pattern.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.JapaneseCandleBar;
import com.algotrado.data.event.TimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;
import com.algotrado.pattern.PTN_0001.PTN_0001_S1;

public class InternalPatternTest implements IGUIController , IDataExtractorObserver, Runnable {

	private long timeMili;
	IDataExtractorSubject dataExtractorSubject;
	IDataExtractorObserver dataRecorder;
	
	public InternalPatternTest()
	{
		timeMili = System.currentTimeMillis();
		PTN_0001_S1 state = new PTN_0001_S1(1);
		String filePath = "C:\\Algo\\test\\" + state.getCode() + ".csv";
		DataSource dataSource = DataSource.FILE;
		AssetType assetType = AssetType.USOIL;
		DataEventType dataEventType = DataEventType.JAPANESE;
		List<Float> parameters = new ArrayList<Float>();
		parameters.add((float) 5);
		parameters.add((float) 0); // TODO - check if we want history
		RegisterDataExtractor.setDataSource(dataSource);
		dataRecorder = new FileDataRecorder(filePath, this);		
	}

	
	@Override
	public void setErrorMessage(String ErrorMsg, boolean endProgram) {
		System.out.println("Error: " + ErrorMsg);
		if (endProgram) {
			throw new RuntimeException	("");
		}
		
	}

	@Override
	public void resetGUI() {
		Float deffTime = Float.valueOf((float)(System.currentTimeMillis() - timeMili)/1000);
		System.out.println(deffTime.toString() + " Sec");
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		// TODO Auto-generated method stub
		
	}
	
	
//	@Override
//	public String getDataHeaders() {
////		return "Asset," + assetType.name() + "\n" +
////				"Interval," + TimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
////				"Data Source," + DataSource.FILE.getValueString() + "\n" + 
////				"Date,Time, " + getNewData().getDataHeaders();
//		return null;
//	}
	
	@Override
	public String toString() {
		String toStringRet = null;
//		for (Iterator<JapaneseCandleBar> jpnCandleIterator = dataList.getCandleBars().iterator(); jpnCandleIterator.hasNext(); ) {
//			if (toStringRet == null) {
//				toStringRet = "";
//			}
//			JapaneseCandleBar candle = jpnCandleIterator.next();
//			SimpleDateFormat dateformatter = new SimpleDateFormat("dd/MM/yyyy,HH:mm:ss");
//			toStringRet += dateformatter.format(candle.getTime()) + " , " + candle.getOpen() + " , " + candle.getHigh() + " , " 
//					+ candle.getLow() + " , " + candle.getClose() + " , " + candle.getVolume() + ((!jpnCandleIterator.hasNext()) ? "" : "\n");
//		}
		return toStringRet; 
	}
}
