package com.algotrado.data.event;

import java.util.Date;

import com.algotrado.extract.data.AssetType;
import com.algotrado.util.Setting;

public class SimpleUpdateDate implements NewUpdateData {

	Date time;
	AssetType asset;
	double value;
	public SimpleUpdateDate(AssetType asset,Date time,double value)
	{
		this.asset = asset;
		this.time = time;
		this.value = value;
	}
	@Override
	public Date getTime() {
		return time;
	}

	@Override
	public String getAssetName() {
		return asset.toString();
	}

	@Override
	public String getDataHeaders() {
		return Setting.getDateTimeHeader("") +",value" ;
	}
	@Override
	public String toString() {
		return Setting.getDateTimeFormat(time) +"," + value ;
	}
	public double getValue() {
		return value;
	}

}
