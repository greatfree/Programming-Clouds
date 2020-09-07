package org.greatfree.dsf.cluster.replication.child.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.UtilConfig;

// Created: 09/07/2020, Bing Li
public class ChildDB
{
	private Map<String, String> data;
	
	private ChildDB()
	{
		this.data = new ConcurrentHashMap<String, String>();
	}
	
	private static ChildDB instance = new ChildDB();
	
	public static ChildDB DB()
	{
		if (instance == null)
		{
			instance = new ChildDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void add(String key, String data)
	{
		this.data.put(key, data);
	}

	public boolean isExisted(String key)
	{
		return this.data.containsKey(key);
	}
	
	public String getData(String key)
	{
		if (this.data.containsKey(key))
		{
			return this.data.get(key);
		}
		return UtilConfig.EMPTY_STRING;
	}
}
