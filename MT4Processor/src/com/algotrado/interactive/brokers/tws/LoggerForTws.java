package com.algotrado.interactive.brokers.tws;

import org.apache.log4j.Logger;

import com.ib.controller.ApiConnection.ILogger;

class LoggerForTws implements ILogger {
	
	
	final static LoggerForTws instance = new LoggerForTws();
	final static Logger logger = Logger.getLogger(LoggerForTws.class);

	@Override
	public void log(String valueOf) {
		logger.error(valueOf);
	}
	
	public static LoggerForTws getLogger() {
		return instance;
	}
	
	public static void main(String[] args) {
		LoggerForTws.getLogger().log("trying 5678");
	}
	
}