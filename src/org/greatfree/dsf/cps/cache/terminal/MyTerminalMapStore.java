package org.greatfree.dsf.cps.cache.terminal;

import java.util.Map;
import java.util.Set;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.TerminalMapStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.data.MyStoreDataFactory;
import org.greatfree.dsf.cps.cache.terminal.db.MyStoreDataDB;

// Created: 08/25/2018, Bing Li
public class MyTerminalMapStore
{
	private TerminalMapStore<MyStoreData, MyStoreDataFactory, StoreKeyCreator, MyStoreDataDB> store;
	
	private MyTerminalMapStore()
	{
	}
	
	private static MyTerminalMapStore instance = new MyTerminalMapStore();
	
	public static MyTerminalMapStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTerminalMapStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.store.close();
	}

	public void init()
	{
		this.store = new TerminalMapStore.TerminalMapStoreBuilder<MyStoreData, MyStoreDataFactory, StoreKeyCreator, MyStoreDataDB>()
				.factory(new MyStoreDataFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.storeKey(TestCacheConfig.TERMINAL_MAP_STORE_KEY)
				.totalStoreSize(TestCacheConfig.TERMINAL_MAP_STORE_TOTAL_SIZE)
				.cacheSize(TestCacheConfig.TERMINAL_MAP_STORE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TERMINAL_MAP_DISK_SIZE_IN_MB)
				.keyCreator(new StoreKeyCreator())
				.db(new MyStoreDataDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TERMINAL_MAP_STORE_KEY), TestCacheConfig.TERMINAL_MAP_DB_STORE))
				.alertEvictedCount(TestCacheConfig.TERMINAL_MAP_STORE_EVICTED_COUNT)
				.build();
	}

	public Set<String> getMapKeys()
	{
		return this.store.getCacheKeys();
	}
	
	public boolean isEmpty()
	{
		return this.store.isEmpty();
	}
	
	public void put(String mapKey, String key, MyStoreData value)
	{
		this.store.put(mapKey, key, value);
	}
	
	public void putAll(String mapKey, Map<String, MyStoreData> values)
	{
		this.store.putAll(mapKey, values);
	}
	
	public MyStoreData get(String mapKey, String key)
	{
		return this.store.get(mapKey, key);
	}
	
	public Map<String, MyStoreData> get(String mapKey, Set<String> keys)
	{
		return this.store.get(mapKey, keys);
	}

	/*
	public Set<String> getKeys(String mapKey)
	{
		return this.store.getKeys(mapKey);
	}
	*/
	
	public Map<String, MyStoreData> getValues(String mapKey, int size)
	{
		return this.store.getValues(mapKey, size);
	}

}
