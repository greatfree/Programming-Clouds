package org.greatfree.cache;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

// Created: 05/31/2017, Bing Li
public class PointingListPointsFactory implements PersistableMapFactorable<String, Double>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
//		System.out.println("PointingListPointsFactory - rootPath = " + rootPath);
//		System.out.println("PointingListPointsFactory - cacheKey = " + cacheKey);

		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getPointingListPointsCachePath(rootPath, cacheKey)))
				.withCache(CacheConfig.getPointingListPointsCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Double.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
				.build(true);
	}

	@Override
	public Cache<String, Double> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(CacheConfig.getPointingListPointsCacheKey(cacheKey), String.class, Double.class);
	}

}
