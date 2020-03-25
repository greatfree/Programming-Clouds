package org.greatfree.cache;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.util.Timing;

// Created: 05/28/2017, Bing Li
public abstract class TimingMapPersistentManagerFactory<Resource extends Timing>
{
	public abstract PersistentCacheManager createMapManagerInstance(String mapRootPath, String cacheKey, int cacheSize, long offheapSizeInMB, long diskSizeInMB);
	public abstract Cache<String, Resource> getMapCache(PersistentCacheManager manager, String cacheKey);

	public StringKeyDB getMapKeyDB(String mapRootPath, String cacheKey)
	{
		return StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(mapRootPath, cacheKey));
	}
}
