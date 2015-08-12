package com.algotrado.interactive.brokers.tws;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.algotrado.util.Setting;
import com.ib.client.EClientErrors;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.Bar;
import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.WhatToShow;

public class HistoryIBDataExtract implements IConnectionHandler, Runnable {
	
	public static final int THOUSAND_EIGHT_HUNDRED_SECONDS = 1800000;
	private final ILogger m_inLogger = new LoggerForTws( );
	private final ILogger m_outLogger = new LoggerForTws( );
	private ApiController m_controller = new ApiControllerWrapper(this, m_inLogger, m_outLogger, "C:\\Algo\\Asset History Date\\QM\\oil_action_log_" + System.currentTimeMillis() + ".txt", true);
	private final ArrayList<String> m_acctList = new ArrayList<String>();
	
	private UpdatedAssetData updatedAssetData;
	private BarResultsPanel historyBarModel;
	
	private Semaphore semaphore = new Semaphore(0);
	
	private static HistoryIBDataExtract historyDataRecorder;
	private boolean isConnected = false;
	
	private Date initialEndDate = null;
	private String initialEndDateFormatted = null;
	
	public static HistoryIBDataExtract getHistoryDataRecorder() {
		return historyDataRecorder;
	}
	
	private NewContract qmContract;
	
	
	public HistoryIBDataExtract(NewContract qmContract) {
		m_controller.connect( "127.0.0.1", 7496, 1);
		
		this.qmContract = qmContract;
		
		threadSleep(5000);
	}
	
	public ApiController controller() 		{ return m_controller; }
	
	public void controller(ApiController controller) {
		this.m_controller = controller;
	}

	public UpdatedAssetData getUpdatedAssetData() {
		return updatedAssetData;
	}

	public void setUpdatedAssetData(UpdatedAssetData updatedAssetData) {
		this.updatedAssetData = updatedAssetData;
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
		qmContract.expiry("20150819 12:00:00");
		qmContract.multiplier("500");
		
		
		historyDataRecorder = new HistoryIBDataExtract(qmContract);
		historyDataRecorder.initialEndDateFormatted = "20150725 00:15:00";
		try {
			historyDataRecorder.initialEndDate = Bar.FORMAT.parse(historyDataRecorder.initialEndDateFormatted);
		} catch (ParseException e1) {
			Setting.errShow("Error parsing initial date.");
			e1.printStackTrace();
			return;
		}
		
		historyDataRecorder.setUpdatedAssetData(new UpdatedAssetData(qmContract.description()));
		String filePath = "C:\\Algo\\Asset History Date\\QM\\oil_" + System.currentTimeMillis() + ".txt";
		historyDataRecorder.historyBarModel = new BarResultsPanel(historyDataRecorder.controller(), historyDataRecorder.getSemaphore(), filePath, historyDataRecorder);
		
		new Thread(historyDataRecorder).start();
	}

	private void historyRequestsRunnable(NewContract qmContract) {
		while (initialEndDate.before(new Date())) {
			long startTime = System.currentTimeMillis();
	//		String genericTickList = "";
			
			
	//		boolean snapshot = false;
	//		test.controller().reqTopMktData(contract, genericTickList, snapshot , test.getUpdatedAssetData());
			threadSleep(1);
			if (isConnected) {
			
				this.controller().reqHistoricalData(qmContract, initialEndDateFormatted, 1800, DurationUnit.SECOND, BarSize._1_secs, WhatToShow.TRADES, false, this.historyBarModel);
			
				try {
					if (this.getSemaphore().tryAcquire(1, TimeUnit.SECONDS)) {
						
					} else {
						
					}
				} catch (InterruptedException e) {
					show("Get History From Broker Process was interrupted.");
					e.printStackTrace();
				}//Block until request finishes.
				
				threadSleep(10001 - (System.currentTimeMillis()-startTime));
				
				this.initialEndDate = new Date(this.initialEndDate.getTime() + THOUSAND_EIGHT_HUNDRED_SECONDS);

				initialEndDateFormatted = Bar.FORMAT.format(initialEndDate);
			}
			
			
		}
		this.controller().disconnect();
		
		Setting.outShow("Finished get history");
		
		try {
			this.historyBarModel.getDestinationFile().close();
		} catch (IOException e1) {
			show("Could not close file" + this.historyBarModel.getDestinationFile().toString());
			e1.printStackTrace();
		}
	}
	
	@Override
	public void connected() {
		show( "connected 123");
		isConnected = true;
		releaseSemaphore();
	}

	private void releaseSemaphore() {
		if (this.getSemaphore().availablePermits() <= 0) {
			this.getSemaphore().release();
		}
	}

	@Override
	public void disconnected() {
		show( "disconnected 123");
		isConnected = false;
		connectAPIController();
	}

	@Override
	public void accountList(ArrayList<String> list) {
		show( "Received account list");
		m_acctList.clear();
		m_acctList.addAll( list);
	}

	@Override 
	public void error(Exception e) {
		show( e.toString() );
	}
	
	@Override 
	public void message(int id, int errorCode, String errorMsg) {
		// Handle errors:
		handleErrors(errorCode);
		show("ID: "+ id + " Error Code: " + errorCode + " Error msg: " + errorMsg);
	}

	private void handleErrors(int errorCode) {
		switch(errorCode) {
		case EClientErrors.CONNECT_FAILED_CONST: //
			Setting.errShow("Could not connect to TWS, Check that it is working.");
			restartHistoryRecorder();
		break;
		case 1: 
		break;
		case 2108://EClientErrors.NOT_CONNECTED_CONST:
			restartHistoryRecorder();
			break;
		case 162://162 Historical Market Data Service error message:HMDS query returned no data: QMU5@NYMEX Trades
			Setting.errShow("Error 162, date tried to withdraw:" + historyDataRecorder.initialEndDateFormatted);
			break;
		case 2103: //Market data farm connection is broken, Disconnect and connect again.
			restartHistoryRecorder();
			break;
		case 1100://Connectivity between IB and TWS BeTrader has been lost.
		case 2110:
			isConnected = false;
			break;
		case 1101:
		case 1102:
			isConnected = true;
			break;
		default:
		}
	}

	private void restartHistoryRecorder() {
		isConnected = false;
		HistoryIBDataExtract.getHistoryDataRecorder().controller().disconnect();
		long startTimestamp = System.currentTimeMillis();
		threadSleep(startTimestamp);
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

	private void connectAPIController() {
		HistoryIBDataExtract.getHistoryDataRecorder().controller(new ApiController( this, m_inLogger, m_outLogger));
		HistoryIBDataExtract.getHistoryDataRecorder().controller().connect( "127.0.0.1", 7496, 1);
	}

	@Override
	public void show(String string) {
		Setting.outShow(string);
	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

@Override
public void run() {
	try 
	{
		while (! (HistoryIBDataExtract.getHistoryDataRecorder().controller().getClientConnection().isConnected())){
			Thread.sleep (1);
		}
	} catch (Exception e) 
	{
	};
	
	historyRequestsRunnable(qmContract);
}

public String getInitialEndDateFormatted() {
	return initialEndDateFormatted;
}

public Date getInitialEndDate() {
	return initialEndDate;
}

}
