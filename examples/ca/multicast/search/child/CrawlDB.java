package ca.multicast.search.child;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 03/16/2020, Bing Li
class CrawlDB
{
	private Map<String, String> db;
	
	private CrawlDB()
	{
		this.db = new ConcurrentHashMap<String, String>();
	}
	
	private static CrawlDB instance = new CrawlDB();
	
	public static CrawlDB DB()
	{
		if (instance == null)
		{
			instance = new CrawlDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void save(String key, String value)
	{
		this.db.put(key, value);
	}
	
	public boolean isExisted(String key)
	{
		return this.db.containsKey(key);
	}
	
	public String getData(String key)
	{
		return this.db.get(key);
	}
}
