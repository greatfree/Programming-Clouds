package org.greatfree.cache.factory;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.PersistableMapFactorable;

// Created: 06/23/2017, Bing Li
class TimingListIndexesFactory implements PersistableMapFactorable<String, TimingListIndexes>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(rootPath))
//				.with(CacheManagerBuilder.persistence(CacheConfig.getMapCachePath(rootPath, cacheKey)))
				.withCache(cacheKey, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, TimingListIndexes.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
				.build(true);
	}

	@Override
	public Cache<String, TimingListIndexes> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(cacheKey, String.class, TimingListIndexes.class);
	}

}
