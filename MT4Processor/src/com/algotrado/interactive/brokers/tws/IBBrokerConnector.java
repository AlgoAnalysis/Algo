package com.algotrado.interactive.brokers.tws;

import java.util.ArrayList;
import java.util.List;

import com.algotrado.broker.Account;
import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.util.Setting;
import com.ib.client.EClientErrors;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.NewContract;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.SecType;

public class IBBrokerConnector implements IConnectionHandler {

	private final ILogger m_inLogger = new LoggerForTws();
	private final ILogger m_outLogger = new LoggerForTws();
	private ApiController m_controller = new ApiControllerWrapper(this, m_inLogger, m_outLogger, "C:\\Algo\\Asset History Date\\QM\\oil_action_log_" + System.currentTimeMillis() + ".txt", true);
//	private NewContract qmContract;
	private TopMarketDataHandlerImpl topMarketDataHandlerImpl;
//	private static int clientId;
	
//	private static Map<AssetType, IBBrokerConnector> ibBrokerImplDataExtractors;
	
	private static IBBrokerConnector ibBrokerImpl;
	
	static {
		ibBrokerImpl = new IBBrokerConnector(/*qmContract*/);
//		ibBrokerImplDataExtractors = new HashMap<AssetType,IBBrokerConnector>();
//		clientId = 2;
	}
	
	public IBBrokerConnector(/*NewContract qmContract*/) {
		super();
		m_controller.connect( "127.0.0.1", 7496, 1);
		
//		this.qmContract = qmContract;
		
		threadSleep(5000);
	}
	
	public static IBBrokerConnector getInstance(/*AssetType asset*//*, NewContract qmContract*/) {
//		IBBrokerConnector ibBrokerImpl = ibBrokerImplDataExtractors.get(asset);
//		if (ibBrokerImpl == null) {
//			ibBrokerImplDataExtractors.put(asset, ibBrokerImpl);
//		}
		return ibBrokerImpl;
	}
	
	private void threadSleep(long howLongToWait) {
		long startTimestamp = System.currentTimeMillis();
		while ((System.currentTimeMillis() - startTimestamp) < howLongToWait) {
			try {
				Thread.sleep(howLongToWait - (System.currentTimeMillis() - startTimestamp));
			} catch (InterruptedException e) {
			}
		}
	}


	public static void main(String[] args) {

		NewContract qmContract = new NewContract();
		qmContract.currency("USD");
		qmContract.exchange("NYMEX");
		//	qmContract.primaryExch("ISLAND");
		qmContract.secType(SecType.FUT);
		qmContract.symbol("QM");
		qmContract.localSymbol("QMU5");
		qmContract.tradingClass("QM");
		String contractExpireDate = "20150921 12:00:00";
		qmContract.expiry(contractExpireDate);
		qmContract.multiplier("500");

		IBBrokerConnector ibBrokerImpl = new IBBrokerConnector(/*qmContract*/);
		
		ibBrokerImpl.requestLiveMarketData(null, AssetType.USOIL);
		
	}

	public synchronized void requestLiveMarketData(IBBrokerSubjectDataExtrator dataExtractorSubject,AssetType assetType) {
		String genericTickList = "";
		boolean snapshot = false;
		TopMarketDataHandlerImpl topMarketDataHandler = new TopMarketDataHandlerImpl("Test", /*ibBrokerImpl,*/ dataExtractorSubject/*, assetType*/);
		topMarketDataHandler.init();
		this.setTopMarketDataHandlerImpl(topMarketDataHandler);
		this.controller().reqTopMktData(assetType.getAssetContractDetails(), genericTickList, snapshot, topMarketDataHandler);
	}


	public ApiController controller() {
		return m_controller;
	}


	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void accountList(ArrayList<String> list) {
		// TODO Auto-generated method stub
		
	}


	@Override 
	public void error(Exception e) {
		show( e.toString() );
	}
	
	@Override 
	public void message(int id, int errorCode, String errorMsg) {
		// Handle errors:
		handleErrors(errorCode, errorMsg);
		show("ID: "+ id + " Error Code: " + errorCode + " Error msg: " + errorMsg);
	}
	
	private void handleErrors(int errorCode, String errMsg) {
		switch(errorCode) {
		case EClientErrors.CONNECT_FAILED_CONST: //
			Setting.errShow("Could not connect to TWS, Check that it is working.");
//			restartHistoryRecorder();
		break;
		case 1: 
		break;
		case 2108://EClientErrors.NOT_CONNECTED_CONST:
//			restartHistoryRecorder();
			break;
		case 162://162 Historical Market Data Service error message:HMDS query returned no data: QMU5@NYMEX Trades
//			Setting.errShow("Error 162, date tried to withdraw:" + historyDataRecorder.initialEndDateFormatted);
//			if (errMsg.contains("data request pacing violation")){
//				retryDate = true;
//				threadSleep(10 * ONE_MINUTE_MILLIS);
//			}
//			releaseSemaphore();
			break;
		case 2103: //Market data farm connection is broken, Disconnect and connect again.
//			restartHistoryRecorder();
			break;
		case 1100://Connectivity between IB and TWS BeTrader has been lost.
		case 2110:
//			isConnected = false;
			break;
		case 1101:
		case 1102:
//			isConnected = true;
			break;
		default:
		}
	}
	
	@Override
	public void show(String string) {
		Setting.outShow(string);
	}

	

	public void setTopMarketDataHandlerImpl(TopMarketDataHandlerImpl topMarketDataHandlerImpl) {
		this.topMarketDataHandlerImpl = topMarketDataHandlerImpl;
	}
}
