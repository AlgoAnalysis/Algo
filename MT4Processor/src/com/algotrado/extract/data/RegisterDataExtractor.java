package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.algotrado.util.DebugUtil;

public class RegisterDataExtractor {
	private Map<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>> extractorSubjectList;
	DataSource dataSource;
	public RegisterDataExtractor(DataSource dataSource)
	{
		extractorSubjectList = new HashMap<AssetType,Map<DataEventType,Map<List<Float>,IDataExtractorSubject>>>();
		if(DebugUtil.debugRegisterDataExtractor && (dataSource == null))
		{
			throw new RuntimeException("The RegisterDataExtractor contractor canot get null in dataSource");
		}
		this.dataSource = dataSource;
	}
	
	public void register(AssetType assetType,DataEventType dataEventType,List<Float> parameters,IDataExtractorObserver observer)
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
				dataEventData = craeteDataEventData(assetType,dataEventType,parameters);
				assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
				extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed
				dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
			}
			else
			{
				dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				if(dataExtractorSubject == null)
				{
					dataExtractorSubject = craeteParametersData(assetType,dataEventType,parameters);
					dataEventData.put(parameters, dataExtractorSubject);
					assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
					extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
				}
			}
		}
		dataExtractorSubject.registerObserver(observer);
	}
	
	public void removeDataExtractorSubject(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
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
	
	private Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> createAssetList(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		Map<List<Float>, IDataExtractorSubject> dataEventData = craeteDataEventData(assetType,dataEventType,parameters);
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> ret = new HashMap<DataEventType, Map<List<Float>,IDataExtractorSubject>>();
		ret.put(dataEventType, dataEventData);
		return ret;
	}
	
	private Map<List<Float>, IDataExtractorSubject> craeteDataEventData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = craeteParametersData(assetType,dataEventType,parameters);
		Map<List<Float>, IDataExtractorSubject> ret = new HashMap<List<Float>, IDataExtractorSubject>();
		ret.put(parameters, dataExtractorSubject);
		return ret;

	}
	
	private IDataExtractorSubject craeteParametersData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = this.dataSource.getSubjectDataExtractor(assetType, dataEventType, parameters);
		return dataExtractorSubject;
	}
}
