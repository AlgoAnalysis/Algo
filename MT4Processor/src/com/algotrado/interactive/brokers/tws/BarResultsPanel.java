package com.algotrado.interactive.brokers.tws;

import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import apidemo.util.NewTabbedPanel.NewTabPanel;

import com.algotrado.util.Setting;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.ApiController;
import com.ib.controller.Bar;

public class BarResultsPanel extends NewTabPanel implements
		IHistoricalDataHandler, IRealTimeBarHandler {


	private static final int ONE_DAY_MILLIS = 24*60*60*1000;
	final ArrayList<Bar> m_rows = new ArrayList<Bar>();
	private ApiController controller;
	private Semaphore semaphore;
	private FileWriter destinationFile;
	private HistoryIBDataExtract historyDataRecorder;
	
	
	BarResultsPanel(ApiController controller, Semaphore semaphore, String filePath, HistoryIBDataExtract historyDataRecorder) {
		this.controller = controller;
		this.semaphore = semaphore;
		this.historyDataRecorder = historyDataRecorder;
		try {
			this.destinationFile = new FileWriter(filePath, false);
		} catch (IOException e) {
			Setting.errShow("Exception occoured while trying to open file for writing.");
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
			initialEndDateParsed = Bar.FORMAT.parse(historyDataRecorder.getInitialEndDateFormatted());
			barDate = Bar.FORMAT.parse(bar.formattedTime().replace("-", ""));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if ((initialEndDateParsed.getTime() % ONE_DAY_MILLIS) < (HistoryIBDataExtract.THOUSAND_EIGHT_HUNDRED_SECONDS + 1)) {
			Setting.errShow("New Day Initial Formatted End Date=" + historyDataRecorder.getInitialEndDateFormatted());
		}
			
		if (initialEndDateParsed.getTime() > (barDate.getTime() + HistoryIBDataExtract.THOUSAND_EIGHT_HUNDRED_SECONDS + 1)) {
			Setting.errShow("1. Bar 					 Date=" + bar.formattedTime());
			Setting.errShow("2. Initial 			 End Date=" + historyDataRecorder.getInitialEndDate());
			Setting.errShow("3. Initial Formatted End Date=" + historyDataRecorder.getInitialEndDateFormatted());
		} else if (bar.volume() > 0) {
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
			Setting.outShow("Error trying to append data to file." + this.getClass());
			e.printStackTrace();
		}
		semaphore.release();
	}

	@Override public void realtimeBar(Bar bar) {
	}
}
