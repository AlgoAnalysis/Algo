package com.algotrado.extract.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterInternalParametersMap
{
	Map<Double,RegisterInternalParametersMap> map;
	IDataExtractorSubject subject;
	int size;
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
		if(get(parameters) == null)
		{
			size++;
		}
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
		if(get(parameters) == null)
		{
			return parameters;
		}
		size--;
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
	
	boolean isEmpty()
	{
		return map.isEmpty();
	}
	
	boolean containsKey(List<Double> parameters)
	{
		return (get(parameters) == null) ? false : true;
	}
	
	int size()
	{
		return size;
	}
}