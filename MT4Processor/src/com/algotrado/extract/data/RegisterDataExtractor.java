package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.util.DebugUtil;

public class RegisterDataExtractor {
	private static Map<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>> extractorSubjectList;
	private static DataSource dataSource;
	
	static {
		extractorSubjectList = new HashMap<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>>();
		
	}
	
	public RegisterDataExtractor(/*DataSource dataSource*/)
	{
//		this.dataSource = dataSource;
	}
	
	public static void setDataSource(DataSource dataSource) {
		if (DebugUtil.debugRegisterDataExtractor && RegisterDataExtractor.dataSource != null) {
			throw new RuntimeException("The RegisterDataExtractor setDataSource dataSource is not null!!");
		}
		RegisterDataExtractor.dataSource = dataSource;
		validateDataSource();
	}

	public static void register(AssetType assetType,DataEventType dataEventType,List<Float> parameters,IDataExtractorObserver observer)
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
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> assetData =  extractorSubjectList.get(assetType);
		if(assetData == null) // new Asset
		{
			assetData = createAssetList(assetType,dataEventType,parameters);
			extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
			dataExtractorSubject = assetData.get(dataEventType).get(parameters); // TODO - need to check if this notation work
		}
		else // the Asset exist
		{
			Map<List<Float>, IDataExtractorSubject> dataEventData = assetData.get(dataEventType);
			if(dataEventData == null)
			{
				dataEventData = createDataEventData(assetType,dataEventType,parameters);
				assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
				extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed
				dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
			}
			else
			{
				dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				if(dataExtractorSubject == null)
				{
					dataExtractorSubject = createParametersData(assetType,dataEventType,parameters);
					dataEventData.put(parameters, dataExtractorSubject);
					assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
					extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
				}
			}
		}
		dataExtractorSubject.registerObserver(observer);
	}
	
	public static void removeDataExtractorSubject(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		extractorSubjectList.get(assetType).get(dataEventType).remove(parameters);  // TODO - need to check if this notation work
		if(extractorSubjectList.get(assetType).get(dataEventType).isEmpty())
		{
			extractorSubjectList.get(assetType).remove(dataEventType);
			if(extractorSubjectList.get(assetType).isEmpty())
			{
				extractorSubjectList.remove(assetType);
			}
		}
	}
	
	private static Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> createAssetList(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		Map<List<Float>, IDataExtractorSubject> dataEventData = createDataEventData(assetType,dataEventType,parameters);
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> ret = new HashMap<DataEventType, Map<List<Float>,IDataExtractorSubject>>();
		ret.put(dataEventType, dataEventData);
		return ret;
	}
	
	private static Map<List<Float>, IDataExtractorSubject> createDataEventData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = createParametersData(assetType,dataEventType,parameters);
		Map<List<Float>, IDataExtractorSubject> ret = new HashMap<List<Float>, IDataExtractorSubject>();
		ret.put(parameters, dataExtractorSubject);
		return ret;
	}
	
	private static IDataExtractorSubject createParametersData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		validateDataSource();
		IDataExtractorSubject dataExtractorSubject = dataSource.getSubjectDataExtractor(assetType, dataEventType, parameters);
		return dataExtractorSubject;
	}

	public static void validateDataSource() {
		if(DebugUtil.debugRegisterDataExtractor && (dataSource == null))
		{
			throw new RuntimeException("The RegisterDataExtractor setDataSource cannot get null in dataSource");
		}
	}
}
