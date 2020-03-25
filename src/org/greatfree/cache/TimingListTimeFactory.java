package org.greatfree.cache;

import java.util.Date;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

// Created: 06/01/2017, Bing Li
public class TimingListTimeFactory implements PersistableMapFactorable<String, Date>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getTimingListTimesCachePath(rootPath, cacheKey)))
				.withCache(CacheConfig.getTimingListTimesCacheKey(cacheKey), CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, Date.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
				.build(true);
	}

	@Override
	public Cache<String, Date> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(CacheConfig.getTimingListTimesCacheKey(cacheKey), String.class, Date.class);
	}

}
