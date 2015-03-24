package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterInternalParametersMap
{
	Map<Double,RegisterInternalParametersMap> map;
	IDataExtractorSubject subject;
	public RegisterInternalParametersMap()
	{
		map = new HashMap<Double,RegisterInternalParametersMap>();
	}
	
	IDataExtractorSubject get(List<Double> parameters)
	{
		if(parameters.size() == 0)
		{
			return subject;
		}
		
		List<Double> newParameters = parameters.subList(1,parameters.size());
		RegisterInternalParametersMap next = map.get(parameters.get(0));
		if(next == null)
		{
			return null;
		}
		return next.get(newParameters);
	}
	
	List<Double> put(List<Double> parameters,IDataExtractorSubject subject)
	{
		if(parameters.size() == 0)
		{
			this.subject = subject;
			return parameters;
		}
		
		List<Double> newParameters = parameters.subList(1,parameters.size());

		RegisterInternalParametersMap next = map.get(parameters.get(0));
		if(next == null)
		{
			next = new RegisterInternalParametersMap();
			map.put(parameters.get(0), next);
		}		
		next.put(newParameters, subject);
		return parameters;
	}
	
	
	
	List<Double> remove(List<Double> parameters)
	{
		if(parameters.size() != 0)
		{
			List<Double> newParameters = parameters.subList(1,parameters.size());
			RegisterInternalParametersMap next = map.get(parameters.get(0));
			next.remove(newParameters);
			if(next.isEmpty())
			{
				map.remove(parameters.get(0));
			}
		}
		else
		{
			subject = null;
		}
		return parameters;
	}
	
	Boolean isEmpty()
	{
		return map.isEmpty();
	}
}