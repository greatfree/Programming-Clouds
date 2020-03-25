package org.greatfree.cache.local;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.CacheConfig;

/*
 * The class is not used at this moment at least. Just keep it temporarily. 02/16/2019, Bing Li
 * 
 * The class is useful. It should be retained. 06/02/2018, Bing Li
 * 
 * The class is abandoned. 05/28/2018, Bing Li
 */

// Created: 05/31/2017, Bing Li
class PointingListPointsFactory implements CacheMapFactorable<String, Double>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB)
	{
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
