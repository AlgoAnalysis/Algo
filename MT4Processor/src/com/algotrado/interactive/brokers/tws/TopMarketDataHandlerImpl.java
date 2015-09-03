package com.algotrado.interactive.brokers.tws;

import static com.ib.controller.Formats.fmtPct;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.util.Setting;
import com.ib.controller.ApiController.ITopMktDataHandler;
import com.ib.controller.NewTickType;
import com.ib.controller.Types.MktDataType;

public class TopMarketDataHandlerImpl implements ITopMktDataHandler {
	String m_description;
	double m_bid;
	double m_ask;
	double m_last;
	double m_high;
	double m_low;
	int m_last_size;
	long m_lastTime;
	int m_bidSize;
	int m_askSize;
	double m_close;
	double m_open;
	int m_volume;
	boolean m_frozen;
	IBBrokerSubjectDataExtrator dataExtractorSubject;
	private Map<NewTickType, TickData> tickDatum;
//	private IBBrokerConnector brokerImpl;
//	private AssetType asset;
	
	TopMarketDataHandlerImpl(String description, /*IBBrokerConnector brokerImpl,*/ IBBrokerSubjectDataExtrator dataExtractorSubject/*, AssetType asset*/) {
		m_description = description;
		tickDatum = new ConcurrentHashMap<NewTickType, TickData>();
//		this.brokerImpl = brokerImpl;
		this.dataExtractorSubject= dataExtractorSubject;
//		this.asset = asset;
	}
	
	public void init() {
		if (this.dataExtractorSubject != null) {
			this.dataExtractorSubject.setTopMarketDataHandlerImpl(this);
		}
	}

	public String change() {
		return m_close == 0	? null : fmtPct( (m_last - m_close) / m_close);
	}

	@Override public void tickPrice( NewTickType tickType, double price, int canAutoExecute) {
		switch( tickType) {
			case BID:
				m_bid = price;
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case ASK:
				m_ask = price;
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case LAST:
				m_last = price;
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case CLOSE:
				m_close = price;
				tickDatum.put(tickType, new TickData(tickType, price, -1, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case OPEN:
				m_open = price;
				tickDatum.put(tickType, new TickData(tickType, price, -1, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case HIGH:
				m_high = price;
				tickDatum.put(tickType, new TickData(tickType, price, -1, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			case LOW:
				m_low = price;
				tickDatum.put(tickType, new TickData(tickType, price, -1, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				break;
			default:
				Setting.outShow("Tick Type = " + tickType.name()  + " , Price = " + price + ", timestamp=" + System.currentTimeMillis());
				RuntimeException runtimeException = new RuntimeException("Tick type was not addressed : " + tickType.name());
				runtimeException.printStackTrace();
				throw runtimeException;
		}
	}

	@Override public void tickSize( NewTickType tickType, int size) {
		switch( tickType) {
			case BID_SIZE:
				m_bidSize = size;
				tickDatum.put(NewTickType.BID, new TickData(tickType, m_bid, size, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Size = " + size + ", timestamp=" + System.currentTimeMillis());
				break;
			case ASK_SIZE:
				m_askSize = size;
				tickDatum.put(NewTickType.ASK, new TickData(tickType, m_ask, size, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Size = " + size + ", timestamp=" + System.currentTimeMillis());
				break;
			case VOLUME:
				m_volume = size;
				tickDatum.put(tickType, new TickData(tickType, -1, size, new Date()));
				Setting.outShow("Tick Type = " + tickType.name()  + " , Size = " + size + ", timestamp=" + System.currentTimeMillis());
				break;
			case LAST_SIZE:
				m_last_size = size;
				TickData tickData = new TickData(NewTickType.LAST, m_last, size, new Date());
				tickDatum.put(NewTickType.LAST, tickData);
				dataExtractorSubject.setSimpleUpdateData(tickData, new Date());
				Setting.outShow("Tick Type = " + tickType.name()  + " , Size = " + size + ", timestamp=" + System.currentTimeMillis());
				break;
			default:
				Setting.outShow("Tick Type = " + tickType.name()  + " , Size = " + size + ", timestamp=" + System.currentTimeMillis());
				RuntimeException runtimeException = new RuntimeException("Tick type was not addressed : " + tickType.name());
				runtimeException.printStackTrace();
				throw runtimeException;
		}
	}
	
	@Override public void tickString(NewTickType tickType, String value) {
		switch( tickType) {
			case LAST_TIMESTAMP:
				m_lastTime = Long.parseLong( value) * 1000;
				Setting.outShow("Tick Type = " + tickType.name()  + " , Time = " + m_lastTime);
				break;
			default:
				Setting.outShow("Tick Type = " + tickType.name());
				RuntimeException runtimeException = new RuntimeException("Tick type was not addressed : " + tickType.name());
				runtimeException.printStackTrace();
				throw runtimeException;
		}
	}
	
	@Override public void marketDataType(MktDataType marketDataType) {
		m_frozen = marketDataType == MktDataType.Frozen;
	}

	@Override
	public void tickSnapshotEnd() {
		Setting.outShow("tick Snapshot End!!!!!!!!!!");
	}

	public TickData getTickData(NewTickType tickType) {
		return tickDatum.get(tickType);
	}

}
