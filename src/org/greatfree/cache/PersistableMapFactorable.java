package org.greatfree.cache;

import java.io.Serializable;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;

// Created: 05/31/2017, Bing Li
public interface PersistableMapFactorable<Key, Value extends Serializable>
{
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB);
	public Cache<Key, Value> createCache(PersistentCacheManager manager, String cacheKey);
}
