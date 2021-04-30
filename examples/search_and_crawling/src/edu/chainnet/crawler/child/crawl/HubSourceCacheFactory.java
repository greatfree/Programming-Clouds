package edu.chainnet.crawler.child.crawl;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.local.CacheMapFactorable;

import edu.chainnet.crawler.HubSource;

// Created: 04/24/2021, Bing Li
class HubSourceCacheFactory implements CacheMapFactorable<String, HubSource>
{

	@Override
	public Cache<String, HubSource> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(cacheKey, String.class, HubSource.class);
	}

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
		.with(CacheManagerBuilder.persistence(CacheConfig.getCachePath(rootPath, cacheKey)))
		.withCache(cacheKey, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, HubSource.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
				.heap(cacheSize, EntryUnit.ENTRIES)
				.offheap(offheapSizeInMB, MemoryUnit.MB)
				.disk(diskSizeInMB, MemoryUnit.MB, true)))
		.build(true);
	}

}
