package com.algotrado.extract.data;

public enum AssetType {
	USOIL("USOil"),
	GOLD("Gold");
	
	
	private String valueString;
	
	private AssetType(String valueString){
		this.valueString = valueString;
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
}
