package org.greatfree.dsf.cps.cache.terminal;

import java.io.IOException;
import java.util.List;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.SortedTerminalListStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCachePointingFactory;
import org.greatfree.dsf.cps.cache.terminal.db.MyCachePointingDB;

// Created: 09/04/2018, Bing Li
public class MySortedTerminalListStore
{
	private SortedTerminalListStore<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, MyCachePointingDB> store;
	
	private MySortedTerminalListStore()
	{
	}
	
	private static MySortedTerminalListStore instance = new MySortedTerminalListStore();

	public static MySortedTerminalListStore BACKEND()
	{
		if (instance == null)
		{
			instance = new MySortedTerminalListStore();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws InterruptedException, IOException
	{
		this.store.close();
	}

	public void init()
	{
		this.store = new SortedTerminalListStore.SortedTerminalListStoreBuilder<MyCachePointing, MyCachePointingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCachePointing>, MyCachePointingDB>()
				.storeKey(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_KEY)
				.factory(new MyCachePointingFactory())
				.rootPath(ServerConfig.CACHE_HOME)
				.totalStoreSize(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_SIZE)
				.cacheSize(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_CACHE_SIZE)
				.offheapSizeInMB(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyCachePointing>())
				.sortSize(TestCacheConfig.SORTED_TERMINAL_LIST_STORE_SORT_SIZE)
				.keyCreator(new StoreKeyCreator())
				.db(new MyCachePointingDB(CacheConfig.getTerminalDBPath(ServerConfig.CACHE_HOME, TestCacheConfig.SORTED_TERMINAL_LIST_STORE_KEY), TestCacheConfig.SORTED_TERMINAL_LIST_DB_STORE))
				.alertEvictedCount(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT)
				.build();
	}

	public void add(MyCachePointing pointing)
	{
		this.store.add(pointing.getCacheKey(), pointing);
	}

	public void addAll(String mapKey, List<MyCachePointing> pointings)
	{
		this.store.addAll(mapKey, pointings);
	}
	
	public MyCachePointing getMax(String mapKey)
	{
		return this.store.getMaxValueResource(mapKey);
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
		return this.store.get(mapKey, startIndex, endIndex);
	}
	

}
