package com.algotrado.interactive.brokers.tws;

import java.io.FileWriter;
import java.io.IOException;

import com.ib.controller.ApiConnection.ILogger;
import com.ib.controller.ApiController;
import com.ib.controller.NewContract;
import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.DurationUnit;
import com.ib.controller.Types.WhatToShow;

public class ApiControllerWrapper extends ApiController {
	
	private FileWriter destinationFile;
	private boolean shouldLog;

	public ApiControllerWrapper(IConnectionHandler handler, ILogger inLogger,
			ILogger outLogger, String logFilePath, boolean shouldLog) {
		super(handler, inLogger, outLogger);
		this.shouldLog = shouldLog;
		if (this.shouldLog) {
			try {
				this.destinationFile = new FileWriter(logFilePath, true);
			} catch (IOException e) {
				System.out.println("Exception occoured while trying to open file for writing.");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void reqHistoricalData( NewContract contract, String endDateTime, int duration, DurationUnit durationUnit, BarSize barSize, WhatToShow whatToShow, boolean rthOnly, IHistoricalDataHandler handler) {
		if (this.shouldLog) {
			try {
				this.destinationFile.append("Contract Details:" + contract);
				this.destinationFile.append("\n");
				this.destinationFile.append("endDateTime:" + endDateTime);
				this.destinationFile.append("\n");
				this.destinationFile.append("duration:" + duration);
				this.destinationFile.append("\n");
				this.destinationFile.append("durationUnit:" + durationUnit);
				this.destinationFile.append("\n");
				this.destinationFile.append("barSize:" + barSize);
				this.destinationFile.append("\n");
				this.destinationFile.append("whatToShow:" + whatToShow);
				this.destinationFile.append("\n");
				this.destinationFile.append("rthOnly:" + rthOnly);
				this.destinationFile.append("\n");
				this.destinationFile.append("handler:" + handler);
				this.destinationFile.append("\n");
				this.destinationFile.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	super.reqHistoricalData(contract, endDateTime, duration, durationUnit, barSize, whatToShow, rthOnly, handler);
    }

	public FileWriter getDestinationFile() {
		return destinationFile;
	}

}
