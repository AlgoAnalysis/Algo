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
			// TODO - exaptation/Error.
		}
		this.dataSource = dataSource;
	}
	
	public void register(AssetType assetType,DataEventType dataEventType,List<Float> parameters,IDataExtractorObserver observer)
	{
		if(DebugUtil.debugRegisterDataExtractor)
		{
			switch(dataEventType)
			{
			case JAPANESE:
				if(parameters.size() != 2)
				{
					throw new RuntimeException("The ");
				}
				else if( !TimeFrameType.isIntervalValid(parameters.get(0)))
				{
					// TODO - exaptation/Error.
					return;
				}
				else if(parameters.get(1).intValue() != parameters.get(1))
				{
					// TODO - exaptation/Error.
					return;
				}
				break;
			case NEW_QUOTE:
				if(parameters.size() != 0)
				{
					// TODO - exaptation/Error.
					return;
				}
				break;
			default:
					// TODO - exaptation/Error.
				return;
			}
		}
		
		IDataExtractorSubject dataExtractorSubject = null;
		Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> assetData =  extractorSubjectList.get(assetType);
		if(assetData == null) // new Asset
		{
			assetData = createAssetList(assetType,dataEventType,parameters);
			if(assetData !=null)
			{
				extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
				dataExtractorSubject = assetData.get(dataEventType).get(parameters); // TODO - need to check if this notation work
			}
		}
		else // the Asset exist
		{
			Map<List<Float>, IDataExtractorSubject> dataEventData = assetData.get(dataEventType);
			if(dataEventData == null)
			{
				dataEventData = craeteDataEventData(assetType,dataEventType,parameters);
				if(dataEventData != null)
				{
					assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
					extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed
					dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				}
			}
			else
			{
				dataExtractorSubject = dataEventData.get(parameters); // TODO - need to check if this notation work
				if(dataExtractorSubject == null)
				{
					dataExtractorSubject = craeteParametersData(assetType,dataEventType,parameters);
					if(dataExtractorSubject != null)
					{
						dataEventData.put(parameters, dataExtractorSubject);
						assetData.put(dataEventType, dataEventData); 	// TODO - need to check if needed 
						extractorSubjectList.put(assetType, assetData); // TODO - need to check if needed 
					}
				}
			}
		}
		
		if(dataExtractorSubject != null)
		{
			dataExtractorSubject.registerObserver(observer);
		}
	}
	
	private Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> createAssetList(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		Map<List<Float>, IDataExtractorSubject> dataEventData = craeteDataEventData(assetType,dataEventType,parameters);
		if(dataEventData != null)
		{
			Map<DataEventType, Map<List<Float>, IDataExtractorSubject>> ret = new HashMap<DataEventType, Map<List<Float>,IDataExtractorSubject>>();
			ret.put(dataEventType, dataEventData);
			return ret;
		}
		return null;
	}
	
	private Map<List<Float>, IDataExtractorSubject> craeteDataEventData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = craeteParametersData(assetType,dataEventType,parameters);
		if( dataExtractorSubject != null)
		{
			Map<List<Float>, IDataExtractorSubject> ret = new HashMap<List<Float>, IDataExtractorSubject>();
			ret.put(parameters, dataExtractorSubject);
			return ret;
		}
		return null;
	}
	
	private IDataExtractorSubject craeteParametersData(AssetType assetType,DataEventType dataEventType,List<Float> parameters)
	{
		IDataExtractorSubject dataExtractorSubject = null;
		switch(this.dataSource)
		{
			case FILE:
				dataExtractorSubject = new FileDataExtractor(assetType,dataEventType,parameters,""); // TODO - add path
				break;
			default:
				// TODO - exaptation/Error.
				break;
		}
		return dataExtractorSubject;
	}
}
