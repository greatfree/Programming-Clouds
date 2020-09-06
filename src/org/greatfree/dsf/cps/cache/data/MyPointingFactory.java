package org.greatfree.dsf.cps.cache.data;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.local.CacheMapFactorable;

// Created: 07/11/2018, Bing Li
public class MyPointingFactory implements CacheMapFactorable<String, MyPointing>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
		.with(CacheManagerBuilder.persistence(CacheConfig.getCachePath(rootPath, cacheKey)))
		.withCache(cacheKey, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, MyPointing.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
				.heap(cacheSize, EntryUnit.ENTRIES)
				.offheap(offheapSizeInMB, MemoryUnit.MB)
				.disk(diskSizeInMB, MemoryUnit.MB, true)))
		.build(true);
	}

	@Override
	public Cache<String, MyPointing> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(cacheKey, String.class, MyPointing.class);
	}

}
