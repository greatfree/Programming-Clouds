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

import edu.chainnet.crawler.AuthoritySolrValue;

// Created: 04/24/2021, Bing Li
class AuthoritySolrQueueFactory implements CacheMapFactorable<Integer, AuthoritySolrValue>
{

	@Override
	public Cache<Integer, AuthoritySolrValue> createCache(PersistentCacheManager manager, String cacheKey)
	{
		return manager.getCache(cacheKey, Integer.class, AuthoritySolrValue.class);
	}

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB)
	{
		return CacheManagerBuilder.newCacheManagerBuilder()
		.with(CacheManagerBuilder.persistence(CacheConfig.getCachePath(rootPath, cacheKey)))
		.withCache(cacheKey, CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, AuthoritySolrValue.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
				.heap(cacheSize, EntryUnit.ENTRIES)
				.offheap(offheapSizeInMB, MemoryUnit.MB)
				.disk(diskSizeInMB, MemoryUnit.MB, true)))
		.build(true);
	}

}
