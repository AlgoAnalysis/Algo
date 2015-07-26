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
	
	private final ILogger m_inLogger = new LoggerForTws( );
	private final ILogger m_outLogger = new LoggerForTws( );
	private ApiController m_controller = new ApiController( this, m_inLogger, m_outLogger);
	private final ArrayList<String> m_acctList = new ArrayList<String>();
	
	private UpdatedAssetData updatedAssetData;
	private BarResultsPanel historyBarModel;
	
//	private Calendar calendar = GregorianCalendar.getInstance();
	private Semaphore semaphore = new Semaphore(0);
	
	private static TestIbDataExtract historyDataRecorder;
	
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
		historyDataRecorder.initialEndDateFormatted = "20150624 14:59:00";
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
			
			
			this.controller().reqHistoricalData(qmContract, initialEndDateFormatted, 1000, DurationUnit.SECOND, BarSize._1_secs, WhatToShow.TRADES, false, this.historyBarModel);
		
			this.initialEndDate = new Date(this.initialEndDate.getTime() + 1000000);
			
//			test.getCalendar().setTime(initialEndDate);
			
			initialEndDateFormatted = Bar.FORMAT.format(initialEndDate);
			
			try {
				this.getSemaphore().acquire();
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.out.println("Get History From Broker Process was interrupted.");
				e.printStackTrace();
			}//Block until request finishes.
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
		
		
		BarResultsPanel( ApiController controller, Semaphore semaphore, String filePath) {
			this.controller = controller;
			this.semaphore = semaphore;
			try {
				this.destinationFile = new FileWriter(filePath, false);
			} catch (IOException e) {
				System.out.println("Exception occoured while trying to open file for writing.");
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
			m_rows.add( bar);
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
			System.out.println(valueOf);
		}
		
	}
	
	@Override
	public void connected() {
		show( "connected");
	}

	@Override
	public void disconnected() {
		show( "disconnected");
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
		case EClientErrors.NOT_CONNECTED_CONST:
		case 162://162 Historical Market Data Service error message:HMDS query returned no data: QMU5@NYMEX Trades
			// move date forward and try extracting more data.
//			TestIbDataExtract.getHistoryDataRecorder().initialEndDate = 
//				new Date(TestIbDataExtract.getHistoryDataRecorder().initialEndDate.getTime() + 1000000);
			restartHistoryRecorder();
			break;
		case 2103: //Market data farm connection is broken, Disconnect and connect again.
			restartHistoryRecorder();
		break;
		default:
//			restartHistoryRecorder();
		}
	}

	private void restartHistoryRecorder() {
		TestIbDataExtract.getHistoryDataRecorder().controller().disconnect();
		TestIbDataExtract.getHistoryDataRecorder().controller(new ApiController( this, m_inLogger, m_outLogger));
		TestIbDataExtract.getHistoryDataRecorder().controller().connect( "127.0.0.1", 7496, 1);
		new Thread(TestIbDataExtract.getHistoryDataRecorder()).start();
	}

	@Override
	public void show(String string) {
		System.err.println(string);
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
