package com.algotrado.data.event;

import java.util.Date;

import com.algotrado.extract.data.AssetType;
import com.algotrado.util.Setting;

public class SimpleUpdateData implements NewUpdateData {

	Date time;
	AssetType asset;
	double value;
	double volume;
	public SimpleUpdateData(AssetType asset,Date time,double value,double volume)
	{
		this.asset = asset;
		this.time = time;
		this.value = value;
		this.volume = volume;
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

	public AssetType getAsset() {
		return asset;
	}

	public double getVolume() {
		return volume;
	}
	

}
