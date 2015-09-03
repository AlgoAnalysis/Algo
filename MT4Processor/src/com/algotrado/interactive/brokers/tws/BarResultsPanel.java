package com.algotrado.interactive.brokers.tws;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import apidemo.util.NewTabbedPanel.NewTabPanel;

import com.algotrado.util.Setting;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IHistoricalDataHandler;
import com.ib.controller.ApiController.IRealTimeBarHandler;
import com.ib.controller.Bar;

public class BarResultsPanel extends NewTabPanel implements
		IHistoricalDataHandler, IRealTimeBarHandler {


	private static final int ONE_DAY_MILLIS = 24*60*60*1000;
	ArrayList<Bar> m_rows = new ArrayList<Bar>();
	final ArrayList<Bar> higherIntervalBars = new ArrayList<Bar>();
	private ArrayList<Long> requestTimesStatistics = new ArrayList<Long>();
	private Long longestRequestTime;
	private Long shortestRequestTime;
	private Long sumRequestTime = (long)0;
	private ApiController controller;
	private Semaphore semaphore;
	private HistoryIBDataExtract historyDataRecorder;
	private long interval;
	
	public BarResultsPanel(ApiController controller) {
		this.controller = controller;
	}
	
	
	public BarResultsPanel(ApiController controller, Semaphore semaphore, HistoryIBDataExtract historyDataRecorder, long interval) {
		this(controller);
		this.interval = interval;
		this.historyDataRecorder = historyDataRecorder;
		this.semaphore = semaphore;
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
		if ((initialEndDateParsed.getTime() % ONE_DAY_MILLIS) < (interval + 1)) {
			Setting.errShow("New Day Initial Formatted End Date=" + historyDataRecorder.getInitialEndDateFormatted());
		}
			
		if (historyDataRecorder.getInitialEndDate().getTime() > (barDate.getTime())) {
			Setting.errShow("1. Bar 					 Date=" + bar.formattedTime());
			Setting.errShow("2. Initial 			 End Date=" + historyDataRecorder.getInitialEndDate());
			Setting.errShow("3. Initial Formatted End Date=" + historyDataRecorder.getInitialEndDateFormatted());
		} else if (bar.volume() > 0) {
			m_rows.add(bar);
		}
	}
	
	@Override public void historicalDataEnd() {
		// Gather request times statistics
		long currRequestTime = System.currentTimeMillis() - historyDataRecorder.getHistoryRequestStartTime();
		sumRequestTime += currRequestTime;
		if (requestTimesStatistics.isEmpty()) {
			longestRequestTime = currRequestTime;
			shortestRequestTime = currRequestTime;
		} else if (longestRequestTime < currRequestTime) {
			longestRequestTime = currRequestTime;
		} else if (shortestRequestTime > currRequestTime) {
			shortestRequestTime = currRequestTime;
		}
		requestTimesStatistics.add(currRequestTime);
		if (interval == HistoryIBDataExtract.HALF_HOUR_MILLIS) {
			ArrayList<Bar> destBars = new ArrayList<Bar>();
			destBars.addAll(m_rows);
			TWSFileWriterSingleton.writeHistoryBarsToFile(destBars);
			/*String dataForWriting = "";
			for (Bar bar : this.m_rows) {
				dataForWriting += bar.toString() + "\n";
			}
			try {
				destinationFile.append(dataForWriting);
				destinationFile.flush();
			} catch (IOException e) {
				Setting.outShow("Error trying to append data to file." + this.getClass());
				e.printStackTrace();
			}*/
		} else {// Save data not in half hour interval of seconds in array list.
			higherIntervalBars.addAll(this.m_rows);
		}
		this.m_rows = new ArrayList<Bar>();
		historyDataRecorder.setRetryDate(false);
		semaphore.release();
	}

	public ArrayList<Bar> getHigherIntervalBars() {
		return higherIntervalBars;
	}

	@Override 
	public void realtimeBar(Bar bar) {
		
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public ArrayList<Long> getRequestTimesStatistics() {
		return requestTimesStatistics;
	}

	public Long getLongestRequestTime() {
		return longestRequestTime;
	}

	public Long getShortestRequestTime() {
		return shortestRequestTime;
	}

	public Long getSumRequestTime() {
		return sumRequestTime;
	}
}
