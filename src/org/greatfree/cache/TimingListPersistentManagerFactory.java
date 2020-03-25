package org.greatfree.cache;

import java.util.Date;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.db.IntegerKeyDB;
import org.greatfree.cache.db.IntegerKeyDBPool;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.util.Timing;

// Created: 05/26/2017, Bing Li
public abstract class TimingListPersistentManagerFactory<Resource extends Timing>
{
	public abstract PersistentCacheManager createListManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB);
	public abstract Cache<String, Resource> getListCache(PersistentCacheManager manager, String cacheKey);

	public StringKeyDB getListKeyDB(String listRootPath, String cacheKey)
	{
		return StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(listRootPath, cacheKey));
	}

	public PersistentCacheManager createTimesManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getTimingListTimesCachePath(listRootPath, cacheKey)))
				.withCache(CacheConfig.getTimingListTimesCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Date.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(1L, MemoryUnit.MB)
						.disk(20, MemoryUnit.MB, true)))
				.build(true);
	}
	
	public Cache<String, Date> getTimesCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(CacheConfig.getTimingListTimesCacheKey(cacheKey), String.class, Date.class);
	}
	
	public StringKeyDB getListTimesKeyDB(String listRootPath, String cacheKey)
	{
		return StringKeyDBPool.SHARED().getDB(CacheConfig.getTimingListTimesKeyDBPath(listRootPath, cacheKey));
	}
	
	public PersistentCacheManager createKeysManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getListKeysCachePath(listRootPath, cacheKey)))
				.withCache(CacheConfig.getListKeysCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(1L, MemoryUnit.MB)
						.disk(20, MemoryUnit.MB, true)))
				.build(true);
	}
	
	public Cache<Integer, String> getKeysCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(CacheConfig.getListKeysCacheKey(cacheKey), Integer.class, String.class);
	}
	
	public IntegerKeyDB getListKeysKeyDB(String listRootPath, String cacheKey)
	{
		return IntegerKeyDBPool.SHARED().getDB(CacheConfig.getListKeysKeyDBPath(listRootPath, cacheKey));
	}
}
