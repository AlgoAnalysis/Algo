package com.algotrado.extract.data;

import java.util.Date;
import java.util.List;

import com.algotrado.extract.data.file.FileDataExtractor;
import com.algotrado.util.Setting;

public enum DataSource {
	FILE
	{
		@Override
        public IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Float> parameters) 
		{
            return FileDataExtractor.getSubjectDataExtractor(assetType, dataEventType, parameters, Setting.getAssetDirectoryPath());
        }
	};
	
	
	
	public abstract IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Float> parameters);
}
