package org.greatfree.framework.threading;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.UtilConfig;

// Created: 09/24/2019, Bing Li
public class MRStates
{
	private Map<String, String> paths;
	private Map<String, Integer> accomplishedCDs;
	
	private MRStates()
	{
		this.paths = new ConcurrentHashMap<String, String>();
		this.accomplishedCDs = new ConcurrentHashMap<String, Integer>();
	}
	
	private static MRStates instance = new MRStates();
	
	public static MRStates CONCURRENCY()
	{
		if (instance == null)
		{
			instance = new MRStates();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void incrementPath(String mrKey, String additionalPath)
	{
		if (!paths.containsKey(mrKey))
		{
			this.paths.put(mrKey, UtilConfig.EMPTY_STRING);
		}
		if (this.paths.get(mrKey).equals(UtilConfig.EMPTY_STRING))
		{
			this.paths.put(mrKey, additionalPath);
		}
		else
		{
//			this.paths.put(mrKey, this.paths.get(mrKey) + UtilConfig.BAR + additionalPath);
			this.paths.put(mrKey, this.paths.get(mrKey) + UtilConfig.NEW_LINE + additionalPath);
		}
	}
	
	public void removePath(String mrKey)
	{
		this.paths.remove(mrKey);
	}
	
	public String getPath(String mrKey)
	{
		return this.paths.get(mrKey);
	}

	public void incrementCD(String mrKey)
	{
		if (!this.accomplishedCDs.containsKey(mrKey))
		{
			this.accomplishedCDs.put(mrKey, 0);
		}
		this.accomplishedCDs.put(mrKey, this.accomplishedCDs.get(mrKey) + 1);
	}
	
	public boolean isCDFulfilled(String mrKey, int cd)
	{
		return this.accomplishedCDs.get(mrKey) >= cd;
	}
	
	public void removeCD(String mrKey)
	{
		this.accomplishedCDs.remove(mrKey);
	}
}
