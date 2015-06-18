package com.algotrado.data.event.indicator.IND_0003;

import java.util.Date;

import com.algotrado.data.event.SimpleUpdateData;
import com.algotrado.extract.data.AssetType;

public class ZigZagUpdateData extends SimpleUpdateData {

	public ZigZagUpdateData(AssetType asset, Date time, double value,
			double volume) {
		super(asset, time, value, volume);
	}

}
