package com.algotrado.extract.data;

import java.util.Date;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.extract.data.file.FileDataExtractor;
import com.algotrado.util.Setting;

public enum DataSource {
	FILE("File")
	{
		@Override
        public IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Float> parameters) 
		{
            return FileDataExtractor.getSubjectDataExtractor(assetType, dataEventType, parameters, Setting.getAssetDirectoryPath());
        }
	};

	private String valueString;
	
	private DataSource(String valueString){
		this.valueString = valueString;
	}
	
	public String getValueString() {
		return valueString;
	}

	public static String[] getDataSourceStrings()
	{
		String[] ret = new String[DataSource.values().length];
		for(int index = 0;index<DataSource.values().length;index++)
		{
			ret[index] = DataSource.values()[index].valueString;
		}
		return ret;
	}
	
	public static DataSource getDataSourceFromString(String valueString)
	{
		for(DataSource source:DataSource.values())
		{
			if(source.valueString == valueString)
			{
				return source;
			}
		}
		return null;
	}
	
	
	public abstract IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Float> parameters);
}
