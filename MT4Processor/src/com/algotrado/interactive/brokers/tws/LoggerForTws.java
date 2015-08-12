package com.algotrado.interactive.brokers.tws;

import com.algotrado.util.Setting;
import com.ib.controller.ApiConnection.ILogger;

class LoggerForTws implements ILogger {

	@Override
	public void log(String valueOf) {
		Setting.errShow(valueOf);
	}
	
}