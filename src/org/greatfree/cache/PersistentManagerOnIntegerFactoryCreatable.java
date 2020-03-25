package org.greatfree.cache;

import java.io.Serializable;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;

// Created: 05/23/2017, Bing Li
public interface PersistentManagerOnIntegerFactoryCreatable<Resource extends Serializable>
{
//	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, int cacheSize);
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB);
	public Cache<Integer, Resource> createCache(PersistentCacheManager manager, String cacheKey);
}
