package com.algotrado.data.event.indicator.IND_0001;

import java.util.Date;

import com.algotrado.data.event.NewUpdateData;
import com.algotrado.extract.data.AssetType;
import com.algotrado.util.Setting;

public class IND_0001_NewUpdateDate implements NewUpdateData {
	Date time;
	AssetType asset;
	double value;
	public IND_0001_NewUpdateDate(AssetType asset,Date time,double value)
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
