package org.greatfree.dip.cps.cache.terminal;

import java.util.Map;
import java.util.Set;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalMap;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyData;
import org.greatfree.dip.cps.cache.data.MyDataFactory;
import org.greatfree.dip.cps.cache.terminal.db.MyDataDB;

// Created: 07/08/2018, Bing Li
public class MyTerminalMap
{
	private TerminalMap<MyData, MyDataFactory, MyDataDB> cache;
	
//	private AtomicInteger totalCount;
	// For testing only. 07/31/2018, Bing Li
//	private CopyOnWriteArrayList<String> totalKeys;
//	private Map<String, String> totalKeys;
	
	private MyTerminalMap()
	{
	}
	
	private static MyTerminalMap instance = new MyTerminalMap();
	
	public static MyTerminalMap BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTerminalMap();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.cache.shutdown();
	}
	
	public void init()
	{
		this.cache = new TerminalMap.TerminalMapBuilder<MyData, MyDataFactory, MyDataDB>()
				.factory(new MyDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.cacheKey(TestCacheConfig.TERMINAL_MAP_KEY)
				.cacheSize(TestCacheConfig.TERMINAL_MAP_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_MAP_DISK_SIZE_IN_MB)
				.db(new MyDataDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TERMINAL_MAP_KEY), TestCacheConfig.TERMINAL_MAP_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_MAP_EVICTED_COUNT)
				.build();
		
//		this.totalCount = new AtomicInteger(0);
//		this.totalKeys = Sets.newHashSet()
//		this.totalKeys = new CopyOnWriteArrayList<String>();
//		this.totalKeys = new ConcurrentHashMap<String, String>();
	}
	
	public void put(String key, MyData data)
	{
		System.out.println("MyTerminalCache-put(): key = " + data.getKey());
		this.cache.put(key, data);
	}

	/*
	public void addKey(String key)
	{
		if (!this.totalKeys.containsKey(key))
		{
			this.totalKeys.put(key, Tools.generateUniqueKey());
		}
//		System.out.println("MyTerminalCache-put(): the total count: " + this.totalCount.incrementAndGet());
		System.out.println("MyTerminalCache-addKey(): the total count: " + this.totalKeys.size());
	}
	*/

	/*
	 * For testing only. 07/31/2018, Bing Li
	 */
	/*
	public void addKeys(Set<String> keys)
	{
//		this.totalKeys.addAll(keys);
		for (String key : keys)
		{
			if (!this.totalKeys.containsKey(key))
			{
				this.totalKeys.put(key, Tools.generateUniqueKey());
			}
		}
		System.out.println("MyTerminalCache-addKeys(): the total count: " + this.totalKeys.size());
	}
	*/
	
	public void putAll(Map<String, MyData> data)
	{
		this.cache.putAll(data);
//		this.totalKeys.addAll(data.keySet());
//		System.out.println("MyTerminalCache-putAll(): the total count: " + this.totalCount.addAndGet(data.size()));
//		System.out.println("MyTerminalCache-put(): the total count: " + this.totalKeys.size());
	}
	
	public MyData get(String key)
	{
		System.out.println("MyTerminalCache-get(): key = " + key);
		return this.cache.get(key);
	}
	
	public Map<String, MyData> get(Set<String> keys)
	{
		System.out.println("MyTerminalCache-get(): keys = " + keys.size());
		return this.cache.getValues(keys);
	}
}
