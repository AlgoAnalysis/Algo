package com.algotrado.extract.data;

import com.algotrado.util.Setting;
import com.ib.controller.NewContract;

public enum AssetType {
	USOIL("USOil",0.001) {
		@Override
		public NewContract getAssetContractDetails() {
			return Setting.getOilContractDetails();
		}
	}, 
	GOLD("Gold",0.001) {
		@Override
		public NewContract getAssetContractDetails() {
			// TODO Auto-generated method stub
			return null;
		}
	};
	
	
	private String valueString;
	private double point; // TODO - need to this not fix in the code
	
	public double getPoint() {
		return point; // TODO - need to this not fix in the code
	}

	private AssetType(String valueString,double point){
		this.valueString = valueString;
		this.point = point;
	}
	
	public static String[] getAssetsStrings()
	{
		String[] ret = new String[AssetType.values().length];
		for(int index = 0;index<AssetType.values().length;index++)
		{
			ret[index] = AssetType.values()[index].valueString;
		}
		return ret;
	}
	
	public static AssetType getAssetTypeFromString(String valueString)
	{
		for(AssetType asset:AssetType.values())
		{
			if(asset.valueString == valueString)
			{
				return asset;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return valueString;
	}
	
	public abstract NewContract getAssetContractDetails();
}
