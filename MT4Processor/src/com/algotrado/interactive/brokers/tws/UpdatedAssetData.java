package com.algotrado.interactive.brokers.tws;

import static com.ib.controller.Formats.fmtPct;

import java.util.Date;

import com.ib.controller.NewTickType;
import com.ib.controller.ApiController.TopMktDataAdapter;
import com.ib.controller.Types.MktDataType;

public class UpdatedAssetData extends TopMktDataAdapter {
	String m_description;
	double m_bid;
	double m_ask;
	double m_last;
	long m_lastTime;
	int m_bidSize;
	int m_askSize;
	double m_close;
	int m_volume;
	boolean m_frozen;
	
	UpdatedAssetData(String description) {
		m_description = description;
	}
	
	@Override
	public String toString() {
		return "last Time of trade=" + new Date(m_lastTime) + ", Last Price=" + m_last + 
				"\nClose=" + m_close +
				"\nBid=" + m_bid + " Size=" + m_bidSize + 
				"\nAsk=" + m_ask + " Size=" + m_askSize + 
				"\nVolume=" + m_volume;
	}

	public String change() {
		return m_close == 0	? null : fmtPct( (m_last - m_close) / m_close);
	}

	@Override public void tickPrice( NewTickType tickType, double price, int canAutoExecute) {
		switch( tickType) {
			case BID:
				m_bid = price;
//				System.out.println("Bid="+ m_bid );
				break;
			case ASK:
				m_ask = price;
//				System.out.println("Ask="+ m_ask);
				break;
			case LAST:
				m_last = price;
//				System.out.println("Last"+ m_last);
				break;
			case CLOSE:
				m_close = price;
//				System.out.println("Close="+ m_close);
				break;
		}
		
//		m_model.fireTableDataChanged(); // should use a timer to be more efficient
	}

	@Override public void tickSize( NewTickType tickType, int size) {
		switch( tickType) {
			case BID_SIZE:
				m_bidSize = size;
//				System.out.println("Bid Size="+ m_bidSize);
				break;
			case ASK_SIZE:
				m_askSize = size;
//				System.out.println("Ask size="+ m_askSize);
				break;
			case VOLUME:
				m_volume = size;
//				System.out.println("Volume="+ m_volume);
				System.out.println(this);
				break;
		}
//		m_model.fireTableDataChanged();
	}
	
	@Override public void tickString(NewTickType tickType, String value) {
		switch( tickType) {
			case LAST_TIMESTAMP:
				m_lastTime = Long.parseLong( value) * 1000;
//				System.out.println("Last Time"+ m_lastTime /*+ " Time=" + m_lastTime*/);
				break;
		}
	}
	
	@Override public void marketDataType(MktDataType marketDataType) {
		m_frozen = marketDataType == MktDataType.Frozen;
//		m_model.fireTableDataChanged();
	}
}
