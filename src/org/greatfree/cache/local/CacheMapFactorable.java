package org.greatfree.cache.local;

import java.io.Serializable;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;

// Created: 05/31/2017, Bing Li
public interface CacheMapFactorable<Key, Value extends Serializable>
{
//	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, int cacheSize, int offheapSizeInMB, int diskSizeInMB);
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, int offheapSizeInMB, int diskSizeInMB);
	public Cache<Key, Value> createCache(PersistentCacheManager manager, String cacheKey);
}
