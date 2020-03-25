package org.greatfree.testing.cache.local;

import java.io.IOException;

import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.distributed.terminal.SortedTerminalMapStore;
import org.greatfree.cache.factory.StoreKeyCreator;
import org.greatfree.data.DescendantListPointingComparator;
import org.greatfree.dip.cps.cache.TestCacheConfig;
import org.greatfree.dip.cps.cache.data.MyCacheTiming;
import org.greatfree.dip.cps.cache.data.MyCacheTimingFactory;
import org.greatfree.dip.cps.cache.terminal.db.MyCacheTimingDB;
import org.greatfree.util.Time;
import org.greatfree.util.Tools;

// Created: 02/19/2019, Bing Li
class SortedTerminalMapStoreTester
{

	public static void main(String[] args) throws InterruptedException, IOException
	{
		String cacheRoot = "/Users/libing/Temp/";
		SortedTerminalMapStore<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, MyCacheTimingDB> store = new SortedTerminalMapStore.SortedTerminalMapStoreBuilder<MyCacheTiming, MyCacheTimingFactory, StoreKeyCreator, DescendantListPointingComparator<MyCacheTiming>, MyCacheTimingDB>()
				.storeKey(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_KEY)
				.factory(new MyCacheTimingFactory())
				.rootPath(cacheRoot)
				.totalStoreSize(100)
				.cacheSize(15)
				.offheapSizeInMB(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_OFFHEAP_SIZE_IN_MB)
				.diskSizeInMB(TestCacheConfig.TIMING_TERMINAL_MAP_STORE_DISK_SIZE_IN_MB)
				.comp(new DescendantListPointingComparator<MyCacheTiming>())
				.sortSize(10)
				.keyCreator(new StoreKeyCreator())
				.db(new MyCacheTimingDB(CacheConfig.getTerminalDBPath(cacheRoot, TestCacheConfig.TIMING_TERMINAL_MAP_STORE_KEY), TestCacheConfig.TIMING_TERMINAL_MAP_STORE))
				.alertEvictedCount(TestCacheConfig.SORTED_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT)
				.build();

		MyCacheTiming p;
		String cacheKey = "c1";
		for (int i = 0; i < 20; i++)
		{
			p = new MyCacheTiming(cacheKey, Tools.generateUniqueKey(), Time.getRandomTime());
			store.put(cacheKey, p);
		}

		System.out.println("------------------------------");

		for (int i = 0; i < 20; i++)
		{
			p = store.get(cacheKey, i);
			System.out.println(p.getCacheKey() + ", " + p.getKey() + ", " + Time.getTime((long)p.getPoints()));
		}

		store.dispose();
	}

}
