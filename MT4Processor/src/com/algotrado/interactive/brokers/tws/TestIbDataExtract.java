package com.algotrado.interactive.brokers.tws;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import javax.swing.SwingUtilities;

import apidemo.util.NewTabbedPanel.NewTabPanel;

import com.ib.client.EClientErrors;
import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.Bar;
import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.SecType;
import com.ib.controller.Types.WhatToShow;

public class TestIbDataExtract implements IConnectionHandler, Runnable {
	
	private static final int THOUSAND_SECONDS = 1000000;
	private final ILogger m_inLogger = new LoggerForTws( );
	private final ILogger m_outLogger = new LoggerForTws( );
	private ApiController m_controller = new ApiControllerWrapper(this, m_inLogger, m_outLogger, "C:\\Algo\\Asset History Date\\QM\\oil_action_log_" + System.currentTimeMillis() + ".txt", true);
	private final ArrayList<String> m_acctList = new ArrayList<String>();
	
	private UpdatedAssetData updatedAssetData;
	private BarResultsPanel historyBarModel;
	
//	private Calendar calendar = GregorianCalendar.getInstance();
	private Semaphore semaphore = new Semaphore(0);
	
	private static TestIbDataExtract historyDataRecorder;
	private boolean isConnected = false;
	
	private Date initialEndDate = null;
	private String initialEndDateFormatted = null;
	
	public static TestIbDataExtract getHistoryDataRecorder() {
		return historyDataRecorder;
	}
	
	private NewContract qmContract;
	
	
	public TestIbDataExtract(NewContract qmContract) {
		m_controller.connect( "127.0.0.1", 7496, 1);
		
		this.qmContract = qmContract;
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
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
		
		/*NewContract contract = new NewContract();
		
		contract.currency("USD");
		contract.exchange("SMART");
		contract.primaryExch("ISLAND");
		contract.secType(SecType.STK);
		contract.symbol("IBM");*/
		
		NewContract qmContract = new NewContract();
		
		qmContract.currency("USD");
		qmContract.exchange("NYMEX");
		//	qmContract.primaryExch("ISLAND");
		qmContract.secType(SecType.FUT);
		qmContract.symbol("QM");
		qmContract.localSymbol("QMQ5");
		qmContract.tradingClass("QM");
		qmContract.expiry("20150819 12:00:00");
		qmContract.multiplier("500");
		
		
		historyDataRecorder = new TestIbDataExtract(qmContract);
		historyDataRecorder.initialEndDateFormatted = "20150101 00:00:00";
		try {
			historyDataRecorder.initialEndDate = Bar.FORMAT.parse(historyDataRecorder.initialEndDateFormatted);
		} catch (ParseException e1) {
			System.out.println("Error parsing initial date.");
			e1.printStackTrace();
			return;
		}
		
		historyDataRecorder.setUpdatedAssetData(new UpdatedAssetData(qmContract.description()));
		String filePath = "C:\\Algo\\Asset History Date\\QM\\oil_" + System.currentTimeMillis() + ".txt";
		historyDataRecorder.historyBarModel = new BarResultsPanel(historyDataRecorder.controller(), historyDataRecorder.getSemaphore(), filePath);
		
		new Thread(historyDataRecorder).start();
	}

	private void historyRequestsRunnable(NewContract qmContract) {
		while (initialEndDate.before(new Date())) {
	//		String genericTickList = "";
			
			
	//		boolean snapshot = false;
	//		test.controller().reqTopMktData(contract, genericTickList, snapshot , test.getUpdatedAssetData());
			if (isConnected) {
			
				this.controller().reqHistoricalData(qmContract, initialEndDateFormatted, 1000, DurationUnit.SECOND, BarSize._1_secs, WhatToShow.TRADES, false, this.historyBarModel);
			
				try {
					this.getSemaphore().acquire();
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					System.out.println("Get History From Broker Process was interrupted.");
					e.printStackTrace();
				}//Block until request finishes.
				
				if (isConnected) {
					this.initialEndDate = new Date(this.initialEndDate.getTime() + THOUSAND_SECONDS);
				
	//			test.getCalendar().setTime(initialEndDate);
					
					initialEndDateFormatted = Bar.FORMAT.format(initialEndDate);
				}
			}
			
			
		}
		this.controller().disconnect();
		
		try {
			this.historyBarModel.getDestinationFile().close();
		} catch (IOException e1) {
			System.out.println("Could not close file" + this.historyBarModel.getDestinationFile().toString());
			e1.printStackTrace();
		}
	}
	
	static class BarResultsPanel extends NewTabPanel implements IHistoricalDataHandler, IRealTimeBarHandler {
//		final BarModel m_model = new BarModel();
		final ArrayList<Bar> m_rows = new ArrayList<Bar>();
//		final boolean m_historical;
//		final Chart m_chart = new Chart( m_rows);
		private ApiController controller;
		private Semaphore semaphore;
		private FileWriter destinationFile;
		private Bar prevBar = null;
		
		
		BarResultsPanel( ApiController controller, Semaphore semaphore, String filePath) {
			this.controller = controller;
			this.semaphore = semaphore;
			try {
				this.destinationFile = new FileWriter(filePath, false);
			} catch (IOException e) {
				System.err.println("Exception occoured while trying to open file for writing.");
				e.printStackTrace();
			}
		}

		public FileWriter getDestinationFile() {
			return destinationFile;
		}

		/** Called when the tab is first visited. */
		@Override 
		public void activated() {
		}

		/** Called when the tab is closed by clicking the X. */
		@Override 
		public void closed() {
			this.controller.cancelHistoricalData( this);
		}

		@Override public void historicalData(Bar bar, boolean hasGaps) {
			
			
			Date initialEndDateParsed = null;
			Date barDate = null;
			try {
				initialEndDateParsed = Bar.FORMAT.parse(historyDataRecorder.initialEndDateFormatted);
				barDate = Bar.FORMAT.parse(bar.formattedTime().replace("-", ""));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if ((initialEndDateParsed.getTime() % 86400000) < (THOUSAND_SECONDS + 1)) {
				System.err.println("New Day Initial Formatted End Date=" + historyDataRecorder.initialEndDateFormatted);
			}
				
			if (initialEndDateParsed.getTime() > (barDate.getTime() + THOUSAND_SECONDS + 1) /*||
					historyDataRecorder.initialEndDate.getTime() > (barDate.getTime() + THOUSAND_SECONDS + 1)*/) {
				System.err.println("1. Bar 					 Date=" + bar.formattedTime());
				System.err.println("2. Initial 			 End Date=" + historyDataRecorder.initialEndDate);
				System.err.println("3. Initial Formatted End Date=" + historyDataRecorder.initialEndDateFormatted);
			} else if ((prevBar == null) || (bar.close() != prevBar.close()) ||
						(bar.open() != prevBar.open()) ||
						(bar.high() != prevBar.high()) ||
						(bar.low() != prevBar.low()) || 
						(bar.volume() > 0)) {
//				if (prevBar == null) {
					prevBar = bar;
//				}
				m_rows.add(bar);
			}
		}
		
		@Override public void historicalDataEnd() {
			String dataForWriting = "";
			for (Bar bar : this.m_rows) {
				dataForWriting += bar.toString() + "\n";
			}
			try {
				destinationFile.append(dataForWriting);
				destinationFile.flush();
				this.m_rows.clear();
			} catch (IOException e) {
				System.out.println("Error trying to append data to file." + this.getClass());
				e.printStackTrace();
			}
			semaphore.release();
		}

		@Override public void realtimeBar(Bar bar) {
		}
		

	}
	
	private class LoggerForTws implements ILogger {

		@Override
		public void log(String valueOf) {
			System.err.println(valueOf);
		}
		
	}
	
	@Override
	public void connected() {
		show( "connected 123");
		isConnected = true;
		if (this.getSemaphore().availablePermits() <= 0) {
			this.getSemaphore().release();
		}
	}

	@Override
	public void disconnected() {
		show( "disconnected 123");
		isConnected = false;
		connectAPIController();
//		restartHistoryRecorder();
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
		show( id + " " + errorCode + " " + errorMsg);
	}

	private void handleErrors(int errorCode) {
		switch(errorCode) {
		case EClientErrors.CONNECT_FAILED_CONST: //
			System.out.println("Could not connect to TWS, Check that it is working.");
		break;
		case 1: 
		break;
		case 2108://EClientErrors.NOT_CONNECTED_CONST:
		case 162://162 Historical Market Data Service error message:HMDS query returned no data: QMU5@NYMEX Trades
			// move date forward and try extracting more data.
//			TestIbDataExtract.getHistoryDataRecorder().initialEndDate = 
//				new Date(TestIbDataExtract.getHistoryDataRecorder().initialEndDate.getTime() + 1000000);
			restartHistoryRecorder();
			break;
		case 2103: //Market data farm connection is broken, Disconnect and connect again.
			restartHistoryRecorder();
			break;
		case 1100:
		case 2110:
			isConnected = false;
			break;
		case 1101:
		case 1102:
			isConnected = true;
			break;
		default:
//			restartHistoryRecorder();
		}
	}

	private void restartHistoryRecorder() {
		TestIbDataExtract.getHistoryDataRecorder().controller().disconnect();
		long startTimestamp = System.currentTimeMillis();
		while (System.currentTimeMillis() < (startTimestamp + 10000)) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
//				e.printStackTrace();
			}
		}
//		connectAPIController();
//		new Thread(TestIbDataExtract.getHistoryDataRecorder()).start();
		
	}

	private void connectAPIController() {
		TestIbDataExtract.getHistoryDataRecorder().controller(new ApiController( this, m_inLogger, m_outLogger));
		TestIbDataExtract.getHistoryDataRecorder().controller().connect( "127.0.0.1", 7496, 1);
	}

	@Override
	public void show(String string) {
		System.err.print(string);
	}

//	public Calendar getCalendar() {
//		return calendar;
//	}

	public Semaphore getSemaphore() {
		return semaphore;
	}

@Override
public void run() {
//	Date initialEndDate = null;
//	String initialEndDateFormatted = "20150620 12:00:00";
//	try {
//		initialEndDate = Bar.FORMAT.parse(initialEndDateFormatted);
//	} catch (ParseException e1) {
//		System.out.println("Error parsing initial date.");
//		e1.printStackTrace();
//		return;
//	}
	
	try 
	{
		// Thread.sleep (1000);
		while (! (TestIbDataExtract.getHistoryDataRecorder().controller().getClientConnection().isConnected()));
	} catch (Exception e) 
	{
	};
	
	historyRequestsRunnable(qmContract);
}

}
