package com.algotrado.interactive.brokers.tws;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.NewUpdateData;
import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;
import com.ib.controller.NewTickType;

public class IBBrokerSubjectDataExtrator extends IDataExtractorSubject {
	
	public final DataEventType dataEventType = DataEventType.NEW_QUOTE;
	private SimpleUpdateData newSimple;
	private SubjectState subjectState;
	private static Map<AssetType,IBBrokerSubjectDataExtrator> brokerDataExtractorList;
	private TopMarketDataHandlerImpl topMarketDataHandlerImpl;
//	private NewContract qmContract;
	
	static {
		brokerDataExtractorList = new HashMap<AssetType, IBBrokerSubjectDataExtrator>();
	}

	public IBBrokerSubjectDataExtrator(AssetType assetType, DataEventType dataEventType, 
										List<Double> parameters/*, NewContract qmContract*/) {
		super(DataSource.IB_TWS_BROKER, assetType, dataEventType, parameters);
		this.subjectState = SubjectState.RUNNING;
//		this.qmContract = qmContract;
	}
	
	public void init() {
		IBBrokerConnector.getInstance().requestLiveMarketData(this, assetType);
	}

	@Override
	public NewUpdateData getNewData() {
		return newSimple;
	}

	@Override
	public DataEventType getDataEventType() {
		return dataEventType;
	}

	@Override
	public void setParameters(List<Double> parameters) {
		// no parameters
	}

	@Override
	public String getDataHeaders() {
		SimpleUpdateData temp = new SimpleUpdateData(null,null,0,0);
		return "Asset," + assetType.name() + "\n" +
		"Interval," + JapaneseTimeFrameType.getTimeFrameFromInterval(parameters.get(0)).getValueString() + "\n" + 
		"Data Source," + DataSource.IB_TWS_BROKER.toString() + "\n" + 
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

	public void setSimpleUpdateData(TickData tickData, Date updateTime) {
		this.newSimple = new SimpleUpdateData(assetType, updateTime, tickData.getPrice(), tickData.getSize());
		// Update new data after it was set.
		notifyObservers(assetType, dataEventType, parameters);
	}
	
	public static IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,
			DataEventType dataEventType, List<Double> parameters) {
		IBBrokerSubjectDataExtrator dataExtractorSubject = getIBBrokerSubjectDataExtractor(assetType);
		if (dataExtractorSubject == null) {
			dataExtractorSubject = new IBBrokerSubjectDataExtrator(assetType, dataEventType, parameters);
			dataExtractorSubject.init();
			brokerDataExtractorList.put(assetType, dataExtractorSubject);
		}
		return dataExtractorSubject;
	}

	public static IBBrokerSubjectDataExtrator getIBBrokerSubjectDataExtractor(
			AssetType assetType) {
		return brokerDataExtractorList.get(assetType);
	}

	public void setTopMarketDataHandlerImpl(TopMarketDataHandlerImpl topMarketDataHandlerImpl) {
		this.topMarketDataHandlerImpl = topMarketDataHandlerImpl;
	}

	/*@Override
	public List<JapaneseCandleBar> getHistory(AssetType assetTypes,
			JapaneseTimeFrameType timeFrame) {
		// TODO Auto-generated method stub
		return null;
	}*/

	/*@Override
	public int openPosition(AssetType asset, double amount,
			PositionDirectionType direction, double stopLoss, double takeProfit) {
		// TODO Auto-generated method stub
		return 0;
	}*/

	/*@Override
	public boolean closePosition(int positionId, double amountToClose) {
		// TODO Auto-generated method stub
		return false;
	}*/

	/*@Override
	public boolean closePosition(int positionId) {
		// TODO Auto-generated method stub
		return false;
	}*/

	/*@Override
	public PositionStatus getPositionStatus(int positionId) {
		// TODO Auto-generated method stub
		return null;
	}*/

	/*@Override
	public boolean modifyPosition(int positionId, double newStopLoss,
			double newTakeProfit) {
		// TODO Auto-generated method stub
		return false;
	}*/

	public double getLiveSpread() {
		if (topMarketDataHandlerImpl.getTickData(NewTickType.ASK) == null ||
				topMarketDataHandlerImpl.getTickData(NewTickType.BID) == null) {
			RuntimeException runtimeException = new RuntimeException("Did not get Ask/Bid prices");
			runtimeException.printStackTrace();
			throw runtimeException;
		}
		return topMarketDataHandlerImpl.getTickData(NewTickType.ASK).getPrice() - 
				topMarketDataHandlerImpl.getTickData(NewTickType.BID).getPrice();
	}

	public double getCurrentAskPrice() {
		if (topMarketDataHandlerImpl.getTickData(NewTickType.ASK) == null) {
			RuntimeException runtimeException = new RuntimeException("Did not get Ask price");
			runtimeException.printStackTrace();
			throw runtimeException;
		}
		return topMarketDataHandlerImpl.getTickData(NewTickType.ASK).getPrice();
	}

	/*@Override
	public double getContractAmount(AssetType asset) {
		// TODO Auto-generated method stub
		return 0;
	}*/

	/*@Override
	public double getMinimumContractAmountMultiply(AssetType asset) {
		// TODO Auto-generated method stub
		return 0;
	}*/

	/*@Override
	public Account getAccountStatus() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
