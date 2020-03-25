package org.greatfree.cache;

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
import org.greatfree.util.Pointing;

// Created: 05/25/2017, Bing Li
//public abstract class PointingListPersistentManagerFactory<Resource extends Serializable>
public abstract class PointingListPersistentManagerFactory<Resource extends Pointing>
{
	public abstract PersistentCacheManager createListManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB);
	public abstract Cache<String, Resource> getListCache(PersistentCacheManager manager, String cacheKey);
//	public abstract PointingListPersistentManagerFactory<Resource> getFactory();

	public StringKeyDB getListKeyDB(String listRootPath, String cacheKey)
	{
		return StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(listRootPath, cacheKey));
	}

	public PersistentCacheManager createPointsManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getPointingListPointsCachePath(listRootPath, cacheKey)))
				.withCache(CacheConfig.getPointingListPointsCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Double.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
				.build(true);
	}
	
	public Cache<String, Double> getPointsCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(CacheConfig.getPointingListPointsCacheKey(cacheKey), String.class, Double.class);
	}
	
	public StringKeyDB getListPointsKeyDB(String listRootPath, String cacheKey)
	{
		return StringKeyDBPool.SHARED().getDB(CacheConfig.getPointingListPointsKeyDBPath(listRootPath, cacheKey));
	}
	
	public PersistentCacheManager createKeysManagerInstance(String listRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getListKeysCachePath(listRootPath, cacheKey)))
				.withCache(CacheConfig.getListKeysCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, String.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
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
