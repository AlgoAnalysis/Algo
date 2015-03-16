package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.data.event.DataEventType;
import com.algotrado.util.DebugUtil;

public class RegisterDataExtractor {
	private static Map<DataSource , Map<AssetType,Map<DataEventType,Map<List<Double>,IDataExtractorSubject>>>> extractorSubjectList;
	
	static {
		extractorSubjectList = new HashMap<DataSource , Map<AssetType,Map<DataEventType,Map<List<Double>,IDataExtractorSubject>>>>();
		
	}
	
	public RegisterDataExtractor()
	{
	}

	public static void register(DataSource dataSource,AssetType assetType,DataEventType dataEventType,List<Double> parameters,IDataExtractorObserver observer)
	{
		if(DebugUtil.debugRegisterDataExtractor)
		{
			if((dataSource == null) || (assetType == null) || (dataEventType == null) || (parameters == null) || (observer == null))
			{
				throw new RuntimeException("The RegisterDataExtractor.register contractor canot get null");
			}
			dataEventType.checkIfTheParametersValid(parameters, true);
		}
		
		IDataExtractorSubject dataExtractorSubject;
		Map<AssetType,Map<DataEventType,Map<List<Double>,IDataExtractorSubject>>> dataSourceMap = extractorSubjectList.get(dataSource);
		if(dataSourceMap == null)
		{
			// Create data source.
			dataSourceMap = createDataSourceList(dataSource, assetType, dataEventType, parameters);
			extractorSubjectList.put(dataSource, dataSourceMap);
			dataExtractorSubject = dataSourceMap.get(assetType).get(dataEventType).get(parameters);
		}
		else
		{
			Map<DataEventType, Map<List<Double>, IDataExtractorSubject>> assetData =  dataSourceMap.get(assetType);
			if(assetData == null) // new Asset
			{
				assetData = createAssetList(dataSource,assetType,dataEventType,parameters);
				dataExtractorSubject = assetData.get(dataEventType).get(parameters); // TODO - need to check if this notation work
			}
			else // the Asset exist
			{
				Map<List<Double>, IDataExtractorSubject> dataEventData = assetData.get(dataEventType);
				if(dataEventData == null)
				{
					dataEventData = createDataEventData(dataSource,assetType,dataEventType,parameters);
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				}
				else
				{
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
					if(dataExtractorSubject == null)
					{
						dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters);
						dataEventData.put(parameters, dataExtractorSubject);
					}
				}
			}
		}
		dataExtractorSubject.registerObserver(observer);
	}
	
	public static void removeDataExtractorSubject(DataSource dataSource ,AssetType assetType,DataEventType dataEventType,List<Double> parameters)
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
	
	private static Map<AssetType, Map<DataEventType, Map<List<Double>, IDataExtractorSubject>>> createDataSourceList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters)
	{
		Map<DataEventType, Map<List<Double>, IDataExtractorSubject>> assetMap = createAssetList(dataSource, assetType,dataEventType,parameters);
		Map<AssetType, Map<DataEventType, Map<List<Double>, IDataExtractorSubject>>> ret = new HashMap<AssetType, Map<DataEventType, Map<List<Double>, IDataExtractorSubject>>>();
		ret.put(assetType, assetMap);
		return ret;
	}
	
	private static Map<DataEventType, Map<List<Double>, IDataExtractorSubject>> createAssetList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters)
	{
		Map<List<Double>, IDataExtractorSubject> dataEventData = createDataEventData(dataSource, assetType,dataEventType,parameters);
		Map<DataEventType, Map<List<Double>, IDataExtractorSubject>> ret = new HashMap<DataEventType, Map<List<Double>,IDataExtractorSubject>>();
		ret.put(dataEventType, dataEventData);
		return ret;
	}
	
	private static Map<List<Double>, IDataExtractorSubject> createDataEventData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters);
		Map<List<Double>, IDataExtractorSubject> ret = new HashMap<List<Double>, IDataExtractorSubject>();
		ret.put(parameters, dataExtractorSubject);
		return ret;
	}
	
	private static IDataExtractorSubject createParametersData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = dataEventType.getSubjectDataExtractor(dataSource,assetType, dataEventType, parameters);
		return dataExtractorSubject;
	}
}
