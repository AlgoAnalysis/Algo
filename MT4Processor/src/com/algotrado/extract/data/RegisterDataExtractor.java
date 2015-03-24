package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.data.event.DataEventType;
import com.algotrado.util.DebugUtil;

public class RegisterDataExtractor {
	private static Map<DataSource , Map<AssetType,Map<DataEventType,RegisterInternalParametersMap>>> extractorSubjectList;
	
	static {
		extractorSubjectList = new HashMap<DataSource , Map<AssetType,Map<DataEventType,RegisterInternalParametersMap>>>();
		
	}
	
	public RegisterDataExtractor()
	{
	}

	public static void register(DataSource dataSource,AssetType assetType,DataEventType dataEventType,List<Double> parameters,int historyLength,IDataExtractorObserver observer)
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
		Map<AssetType,Map<DataEventType,RegisterInternalParametersMap>> dataSourceMap = extractorSubjectList.get(dataSource);
		if(dataSourceMap == null)
		{
			// Create data source.
			dataExtractorSubject = createDataSourceList(dataSource, assetType, dataEventType, parameters);
		}
		else
		{
			Map<DataEventType, RegisterInternalParametersMap> assetData =  dataSourceMap.get(assetType);
			if(assetData == null) // new Asset
			{
				dataExtractorSubject = createAssetList(dataSource,assetType,dataEventType,parameters,dataSourceMap);
			}
			else // the Asset exist
			{
				RegisterInternalParametersMap dataEventData = assetData.get(dataEventType);
				if(dataEventData == null)
				{
					dataExtractorSubject = createDataEventData(dataSource,assetType,dataEventType,parameters,assetData);
				}
				else
				{
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
					if(dataExtractorSubject == null)
					{
						dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters,dataEventData);
					}
				}
			}
		}
		dataExtractorSubject.registerObserver(observer);
		// TODO - history!!!
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
	
	private static IDataExtractorSubject createDataSourceList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters)
	{
		Map<AssetType, Map<DataEventType, RegisterInternalParametersMap>> assetTypeMap = new HashMap<AssetType, Map<DataEventType, RegisterInternalParametersMap>>();
		extractorSubjectList.put(dataSource, assetTypeMap);
		return createAssetList(dataSource, assetType,dataEventType,parameters,assetTypeMap);
	}
	
	private static IDataExtractorSubject createAssetList(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters,Map<AssetType, Map<DataEventType, RegisterInternalParametersMap>> assetTypeMap)
	{
		Map<DataEventType, RegisterInternalParametersMap> dataEventMap = new HashMap<DataEventType, RegisterInternalParametersMap>();
		assetTypeMap.put(assetType, dataEventMap);
		return createDataEventData(dataSource, assetType,dataEventType,parameters,dataEventMap);
	}
	
	private static IDataExtractorSubject createDataEventData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters,Map<DataEventType, RegisterInternalParametersMap> dataEventMap)
	{
		RegisterInternalParametersMap parametersMap = new RegisterInternalParametersMap();
		dataEventMap.put(dataEventType, parametersMap);
		IDataExtractorSubject dataExtractorSubject = createParametersData(dataSource,assetType,dataEventType,parameters,parametersMap);
		return dataExtractorSubject;
	}
	
	private static IDataExtractorSubject createParametersData(DataSource dataSource, AssetType assetType,DataEventType dataEventType,List<Double> parameters,RegisterInternalParametersMap parametersMap)
	{
		IDataExtractorSubject dataExtractorSubject = dataEventType.getSubjectDataExtractor(dataSource,assetType, dataEventType, parameters);
		parametersMap.put(parameters, dataExtractorSubject);
		return dataExtractorSubject;
	}
}
