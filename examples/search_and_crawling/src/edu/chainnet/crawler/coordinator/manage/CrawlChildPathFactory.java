package edu.chainnet.crawler.coordinator.manage;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.local.CacheMapFactorable;

// Created: 04/26/2021, Bing Li
class CrawlChildPathFactory implements CacheMapFactorable<String, CrawlChildPath>
{

	@Override
	public Cache<String, CrawlChildPath> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(cacheKey, String.class, CrawlChildPath.class);
	}

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
				.with(CacheManagerBuilder.persistence(CacheConfig.getCachePath(rootPath, cacheKey)))
				.withCache(cacheKey, CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, CrawlChildPath.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
						.heap(cacheSize, EntryUnit.ENTRIES)
						.offheap(offheapSizeInMB, MemoryUnit.MB)
						.disk(diskSizeInMB, MemoryUnit.MB, true)))
				.build(true);
	}

}
