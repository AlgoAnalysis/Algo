package com.algotrado.interactive.brokers.tws;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	
	private static final int NUM_OF_MINUTES_IN_HOUR = 60;
	private static final int ONE_MINUTE_MILLIS = 60 * 1000;
	private static final int MAX_HISTORY_REQUESTS_PER_10_MINUTES = 60;
	public static final long HALF_HOUR_MILLIS = 30*ONE_MINUTE_MILLIS;
	public static final long THIRTY_ONE_DAYS_MILLIS = (long)31*24*60*ONE_MINUTE_MILLIS;
	private final ILogger m_inLogger = new LoggerForTws( );
	private final ILogger m_outLogger = new LoggerForTws( );
	private ApiController m_controller = new ApiControllerWrapper(this, m_inLogger, m_outLogger, "C:\\Algo\\Asset History Date\\QM\\oil_action_log_" + System.currentTimeMillis() + ".txt", true);
	private final ArrayList<String> m_acctList = new ArrayList<String>();
	
	private UpdatedAssetData updatedAssetData;
	private BarResultsPanel historyBarModel;
	private long historyRequestStartTime;
	
	private Semaphore semaphore = new Semaphore(0);
	
	private static HistoryIBDataExtract historyDataRecorder;
	private boolean isConnected = false;
	private boolean shouldOnlyDisconnect = false;
	private boolean retryDate = false;
	
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
		String contractExpireDate = "20150921 12:00:00";
		qmContract.expiry(contractExpireDate);
		qmContract.multiplier("500");
		
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMM"); // format for historical query
		
		String expiryStr = null;
		try {
			expiryStr = sdf.format(Bar.FORMAT.parse(contractExpireDate));
		} catch (ParseException e1) {
			Setting.errShow("Error parsing initial date.");
			e1.printStackTrace();
			return;
		}
		
		historyDataRecorder = new HistoryIBDataExtract(qmContract);
		historyDataRecorder.initialEndDateFormatted = "20140701 00:15:00";
		try {
			historyDataRecorder.initialEndDate = Bar.FORMAT.parse(historyDataRecorder.initialEndDateFormatted);
		} catch (ParseException e1) {
			Setting.errShow("Error parsing initial date.");
			e1.printStackTrace();
			return;
		}
		
		historyDataRecorder.setUpdatedAssetData(new UpdatedAssetData(qmContract.description()));
		String filePath = "C:\\Algo\\Asset History Date\\QM\\oil_QM_500_" + expiryStr + "_" + System.currentTimeMillis() + ".txt";
		TWSFileWriterSingleton.setFilePath(filePath);
		historyDataRecorder.historyBarModel = new BarResultsPanel(historyDataRecorder.controller(), historyDataRecorder.getSemaphore(), /*filePath,*/ historyDataRecorder, HALF_HOUR_MILLIS);
		
		new Thread(historyDataRecorder).start();
	}

	private void historyRequestsRunnable(NewContract qmContract) {
		boolean changeDate = false;
		long retry = 0;
		int numOfRequests = 0;
		long startRequestsTime = System.currentTimeMillis();
		this.historyBarModel.setInterval(THIRTY_ONE_DAYS_MILLIS);
		while (initialEndDate.before(new Date())) {
			/*if (numOfRequests % 60 == 0) {
				threadSleep((10 * ONE_MINUTE_MILLIS) - (System.currentTimeMillis() - startRequestsTime) + 1);
				startRequestsTime = System.currentTimeMillis();
			}*/
			
	//		String genericTickList = "";
			
			
	//		boolean snapshot = false;
	//		test.controller().reqTopMktData(contract, genericTickList, snapshot , test.getUpdatedAssetData());
			threadSleep(1);
			if (isConnected) {
				if (!retryDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(this.initialEndDate);
					calendar.add(Calendar.MONTH, 1);
					initialEndDateFormatted = Bar.FORMAT.format(calendar.getTime());
				}
				this.historyRequestStartTime = System.currentTimeMillis();
				this.controller().reqHistoricalData(qmContract, initialEndDateFormatted, 1, DurationUnit.MONTH, BarSize._30_mins, WhatToShow.TRADES, false, this.historyBarModel);
				numOfRequests++;
				try {
					if (this.getSemaphore().tryAcquire(5, TimeUnit.SECONDS)) {
						changeDate = true;
						retry = 0;
					}
					else
					{
						changeDate = false;
						retry++;
						if(retry == 5)
						{
							throw new RuntimeException("Error occoured while trying to withdraw history.");
						}
					}
				} catch (InterruptedException e) {
					show("Get History From Broker Process was interrupted.");
					e.printStackTrace();
				}//Block until request finishes.
				
//				threadSleep(10001 - (System.currentTimeMillis()-startTime));
				
				if (changeDate && !retryDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(this.initialEndDate);
					calendar.add(Calendar.MONTH, 1);
					this.initialEndDate = calendar.getTime();
				}
			}
			
			
		}
		
		this.historyBarModel.setInterval(HALF_HOUR_MILLIS);
		int numOfRequestBulks = this.historyBarModel.getHigherIntervalBars().size() / MAX_HISTORY_REQUESTS_PER_10_MINUTES;
		int timeToFinishInMinutes = numOfRequestBulks * 10;
		String totalRemainingTime = (timeToFinishInMinutes / NUM_OF_MINUTES_IN_HOUR) + ":" + (timeToFinishInMinutes % NUM_OF_MINUTES_IN_HOUR);
		show("Total Remaining Time for downloading history is : " + totalRemainingTime + " Hours:Minutes.");
		threadSleep(10000);
		
		for (Bar halfHourBar : this.historyBarModel.getHigherIntervalBars()) {
			if (numOfRequests % MAX_HISTORY_REQUESTS_PER_10_MINUTES == 0) {
				threadSleep((10 * ONE_MINUTE_MILLIS) - (System.currentTimeMillis() - startRequestsTime) + 1);
				startRequestsTime = System.currentTimeMillis();
			}
//			long startTime = System.currentTimeMillis();
			while (!isConnected) {
				threadSleep(1);
			}
			threadSleep(1);
			if (isConnected) {
				if (!retryDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(new Date(halfHourBar.time() * 1000));
					initialEndDate = calendar.getTime();
					calendar.add(Calendar.MINUTE, 30);
					initialEndDateFormatted = Bar.FORMAT.format(calendar.getTime());
				}
				this.historyRequestStartTime = System.currentTimeMillis();
				this.controller().reqHistoricalData(qmContract, initialEndDateFormatted, 1800, DurationUnit.SECOND, BarSize._1_secs, WhatToShow.TRADES, false, this.historyBarModel);
				numOfRequests++;
				try {
					if (this.getSemaphore().tryAcquire(5, TimeUnit.SECONDS)) {
						changeDate = true;
						retry = 0;
					}
					else
					{
						changeDate = false;
						retry++;
						if(retry == 5)
						{
							throw new RuntimeException("Error occoured while trying to withdraw history.");
						}
					}
				} catch (InterruptedException e) {
					show("Get History From Broker Process was interrupted.");
					e.printStackTrace();
				}//Block until request finishes.
				
//				threadSleep(10001 - (System.currentTimeMillis()-startTime));
			}
			
			
		}
		
		shouldOnlyDisconnect = true;
		this.controller().disconnect();
		
		Setting.outShow("Finished get history");
		
		TWSFileWriterSingleton.getExecutor().shutdown();
		
		try {
			boolean executorDone = TWSFileWriterSingleton.getExecutor().awaitTermination(1, TimeUnit.MINUTES);
			if (!executorDone) {
				Setting.errShow("There are more unfinished tasks.");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Display Request Time statistics:
		show("Request Time statistics:");
		show("Longest request time millis:" + historyBarModel.getLongestRequestTime());
		show("Shortest request time millis:" + historyBarModel.getShortestRequestTime());
		double averageRequestTime = historyBarModel.getSumRequestTime() / historyBarModel.getRequestTimesStatistics().size();
		show("Average request time millis:" + averageRequestTime);
		// Calculate Standard Deviation:
		double sumDistanceFromMeanSquared = 0;
		for (Long currRequestTime : historyBarModel.getRequestTimesStatistics()) {
			double currDistanceFromMeanSquarred = Math.pow(((double)currRequestTime - averageRequestTime), 2);
			sumDistanceFromMeanSquared += currDistanceFromMeanSquarred;
		}
		double standardDev = sumDistanceFromMeanSquared/ historyBarModel.getRequestTimesStatistics().size();
		show("Standard Deviation request time millis:" + standardDev);
		
		TWSFileWriterSingleton.closeFileWriter();
		/*try {
			this.historyBarModel.getDestinationFile().close();
		} catch (IOException e1) {
			show("Could not close file" + this.historyBarModel.getDestinationFile().toString());
			e1.printStackTrace();
		}*/
	}
	
	@Override
	public void connected() {
		show( "connected 123");
		isConnected = true;
//		releaseSemaphore();
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
		if (!shouldOnlyDisconnect) {
			connectAPIController();
		}
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
		handleErrors(errorCode, errorMsg);
		show("ID: "+ id + " Error Code: " + errorCode + " Error msg: " + errorMsg);
	}

	private void handleErrors(int errorCode, String errMsg) {
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
			if (errMsg.contains("data request pacing violation")){
				retryDate = true;
				threadSleep(10 * ONE_MINUTE_MILLIS);
			}
			releaseSemaphore();
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

public void setRetryDate(boolean retryDate) {
	this.retryDate = retryDate;
}

public long getHistoryRequestStartTime() {
	return historyRequestStartTime;
}

}
