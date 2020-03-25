package org.greatfree.testing.cache.distributed;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.greatfree.cache.PersistableMapFactorable;

// Created: 07/04/2017, Bing Li
public class MyFactory implements PersistableMapFactorable<String, TestPointing>
{

	@Override
	public PersistentCacheManager createManagerInstance(String rootPath, String cacheKey, long cacheSize, long offheapSizeInMB, long diskSizeInMB)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cache<String, TestPointing> createCache(PersistentCacheManager manager, String cacheKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
