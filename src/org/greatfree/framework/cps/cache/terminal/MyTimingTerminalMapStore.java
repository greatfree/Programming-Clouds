package org.greatfree.framework.cps.cache.terminal;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.SortedTerminalMapStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.TestCacheConfig;
import org.greatfree.framework.cps.cache.data.MyCacheTiming;
import org.greatfree.framework.cps.cache.data.MyCacheTimingFactory;
import org.greatfree.framework.cps.cache.terminal.db.MyCacheTimingDB;

// Created: 08/21/2018, Bing Li
public class MyTimingTerminalMapStore
{
	private SortedTerminalMapStore<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, MyCacheTimingDB> store;

	private MyTimingTerminalMapStore()
	{
	}
	
	private static MyTimingTerminalMapStore instance = new MyTimingTerminalMapStore();

	public static MyTimingTerminalMapStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MyTimingTerminalMapStore();
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
		this.store = new SortedTerminalMapStore.SortedTerminalMapStoreBuilder<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, MyCacheTimingDB>()
				.storeKey(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_KEY)
				.factory(new MyCacheTimingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.totalStoreSize(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_SIZE)
				.cacheSize(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyCacheTiming>())
				.sortSize(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.db(new MyCacheTimingDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.TIMING_TERMINAL_MAP_STORE_KEY), TestCacheConfig.TIMING_TERMINAL_MAP_STORE))
				.alertEvictedCount(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT)
				.build();
	}
	
	public void put(MyCacheTiming pointing)
	{
		this.store.put(pointing.getCacheKey(), pointing);
	}

	public void putAll(String mapKey, List<MyCacheTiming> pointings)
	{
		this.store.putAll(mapKey, pointings);
	}
	
	public boolean containsKey(String mapKey, String resourceKey)
	{
		return this.store.containsKey(mapKey, resourceKey);
	}
	
	public MyCacheTiming getMax(String mapKey)
	{
		return this.store.getMaxValueResource(mapKey);
	}
	
	public MyCacheTiming get(String mapKey, String resourceKey)
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
	
	public MyCacheTiming get(String mapKey, int index)
	{
		System.out.println("MyTimingTerminalMapStore-get(): mapKey = " + mapKey + ", index = " + index);
		return this.store.get(mapKey, index);
	}
	
	public List<MyCacheTiming> getTop(String mapKey, int endIndex)
	{
		return this.store.get(mapKey, 0, endIndex);
	}

	public List<MyCacheTiming> getRange(String mapKey, int startIndex, int endIndex)
	{
		System.out.println("MyTimingTerminalMapStore-getRange(): startIndex = " + startIndex + ", endIndex = " + endIndex);
		return this.store.get(mapKey, startIndex, endIndex);
	}
}
