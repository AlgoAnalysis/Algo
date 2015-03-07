package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.data.event.DataEventType;
import com.algotrado.util.DebugUtil;

public class RegisterDataExtractor {
	private static Map<DataSource , Map<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>>> extractorSubjectList;
	
	static {
		extractorSubjectList = new HashMap<DataSource , Map<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>>>();
		
	}
	
	public RegisterDataExtractor()
	{
	}

	public static void register(DataSource dataSource,AssetType assetType,DataEventType dataEventType,List<Float> parameters,IDataExtractorObserver observer)
	{
		if(DebugUtil.debugRegisterDataExtractor)
		{
			if((assetType == null) || (dataEventType == null) || (parameters == null) || (observer == null))
			{
				throw new RuntimeException("The RegisterDataExtractor.register contractor canot get null");
			}
			dataEventType.checkIfTheParametersValid(parameters, true);
		}
		
		IDataExtractorSubject dataExtractorSubject;
		Map<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>> dataSourceMap = extractorSubjectList.get(dataSource);
		if(dataSourceMap == null)
		{
			// Create data source.
			dataSourceMap = createDataSourceList(dataSource, assetType, dataEventType, parameters);
			extractorSubjectList.put(dataSource, dataSourceMap);
			dataExtractorSubject = dataSourceMap.get(assetType).get(dataEventType).get(parameters);
		}
		else
		{
			Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> assetData =  dataSourceMap.get(assetType);
			if(assetData == null) // new Asset
			{
				assetData = createAssetList(dataSource,assetType,dataEventType,parameters);
	//			extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
				dataExtractorSubject = assetData.get(dataEventType).get(parameters); // TODO - need to check if this notation work
			}
			else // the Asset exist
			{
				Map<List<Float>, IDataExtractorSubject> dataEventData = assetData.get(dataEventType);
				if(dataEventData == null)
				{
					dataEventData = createDataEventData(dataSource,assetType,dataEventType,parameters);
	//				assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
	//				extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				}
				else
				{
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
					if(dataExtractorSubject == null)
					{
						dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters);
						dataEventData.put(parameters, dataExtractorSubject);
	//					assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
	//					extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
					}
				}
			}
		}
		dataExtractorSubject.registerObserver(observer);
	}
	
	public static void removeDataExtractorSubject(DataSource dataSource ,AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		extractorSubjectList.get(dataSource).get(assetType).get(dataEventType).remove(parameters);  // TODO - need to check if this notation work
		if(extractorSubjectList.get(dataSource).get(assetType).get(dataEventType).isEmpty())
		{
			extractorSubjectList.get(dataSource).get(assetType).remove(dataEventType);
			if(extractorSubjectList.get(dataSource).get(assetType).isEmpty())
			{
				extractorSubjectList.get(dataSource).remove(assetType);
				if (extractorSubjectList.get(dataSource).isEmpty()) {
					extractorSubjectList.remove(dataSource);
				}
			}
		}
	}
	
	private static Map<AssetType, Map<DataEventType, Map<List<Float>, IDataExtractorSubject>>> createDataSourceList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> assetMap = createAssetList(dataSource, assetType,dataEventType,parameters);
		Map<AssetType, Map<DataEventType, Map<List<Float>, IDataExtractorSubject>>> ret = new HashMap<AssetType, Map<DataEventType, Map<List<Float>, IDataExtractorSubject>>>();
		ret.put(assetType, assetMap);
		return ret;
	}
	
	private static Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> createAssetList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		Map<List<Float>, IDataExtractorSubject> dataEventData = createDataEventData(dataSource, assetType,dataEventType,parameters);
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> ret = new HashMap<DataEventType, Map<List<Float>,IDataExtractorSubject>>();
		ret.put(dataEventType, dataEventData);
		return ret;
	}
	
	private static Map<List<Float>, IDataExtractorSubject> createDataEventData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters);
		Map<List<Float>, IDataExtractorSubject> ret = new HashMap<List<Float>, IDataExtractorSubject>();
		ret.put(parameters, dataExtractorSubject);
		return ret;
	}
	
	private static IDataExtractorSubject createParametersData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		validateDataSource();
		IDataExtractorSubject dataExtractorSubject = dataSource.getSubjectDataExtractor(assetType, dataEventType, parameters);
		return dataExtractorSubject;
	}

	public static void validateDataSource() {
//		if(DebugUtil.debugRegisterDataExtractor && (dataSource == null))
//		{
//			throw new RuntimeException("The RegisterDataExtractor setDataSource cannot get null in dataSource");
//		}
	}
}
