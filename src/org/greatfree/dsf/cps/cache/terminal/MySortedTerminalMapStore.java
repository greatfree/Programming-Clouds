package org.greatfree.dsf.cps.cache.terminal;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.SortedTerminalMapStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCachePointingFactory;
import org.greatfree.dsf.cps.cache.terminal.db.MyCachePointingDB;

// Created: 07/23/2018, Bing Li
public class MySortedTerminalMapStore
{
	private SortedTerminalMapStore<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, MyCachePointingDB> store;
	
	private MySortedTerminalMapStore()
	{
	}
	
	private static MySortedTerminalMapStore instance = new MySortedTerminalMapStore();

	public static MySortedTerminalMapStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MySortedTerminalMapStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.store.dispose();
	}
	
	public void init()
	{
		this.store = new SortedTerminalMapStore.SortedTerminalMapStoreBuilder<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, MyCachePointingDB>()
				.storeKey(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_KEY)
				.factory(new MyCachePointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.totalStoreSize(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_SIZE)
				.cacheSize(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyCachePointing>())
				.sortSize(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.db(new MyCachePointingDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.SORTED_TERMINAL_MAP_STORE_KEY), TestCacheConfig.SORTED_TERMINAL_MAP_STORE))
				.alertEvictedCount(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT)
				.build();
	}
	
	public void put(MyCachePointing pointing)
	{
		this.store.put(pointing.getCacheKey(), pointing);
	}

	public void putAll(String mapKey, List<MyCachePointing> pointings)
	{
		this.store.putAll(mapKey, pointings);
	}
	
	public boolean containsKey(String mapKey, String resourceKey)
	{
		return this.store.containsKey(mapKey, resourceKey);
	}
	
	public MyCachePointing getMax(String mapKey)
	{
		return this.store.getMaxValueResource(mapKey);
	}
	
	public MyCachePointing get(String mapKey, String resourceKey)
	{
		return this.store.get(mapKey, resourceKey);
	}
	
	public boolean isCacheExisted(String mapKey)
	{
		return this.store.isExisted(mapKey);
	}
	
	public boolean isCacheEmpty(String mapKey)
	{
		return this.store.isEmpty(mapKey);
	}
	
	public MyCachePointing get(String mapKey, int index)
	{
		return this.store.get(mapKey, index);
	}
	
	public List<MyCachePointing> getTop(String mapKey, int endIndex)
	{
		return this.store.get(mapKey, 0, endIndex);
	}

	public List<MyCachePointing> getRange(String mapKey, int startIndex, int endIndex)
	{
		System.out.println("MyPointingTerminalMapStore-getRange(): startIndex = " + startIndex + ", endIndex = " + endIndex);
		return this.store.get(mapKey, startIndex, endIndex);
	}
}
