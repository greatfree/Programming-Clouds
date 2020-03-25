package org.greatfree.dip.cps.cache.message;

import org.greatfree.cache.distributed.store.ListPrefetchNotification;

// Created: 08/03/2018, Bing Li
public class PrefetchMyCachePointingListStoreNotification extends ListPrefetchNotification
{

	public PrefetchMyCachePointingListStoreNotification(String cacheKey, int startIndex, int endIndex, int currentCacheLastIndex, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, currentCacheLastIndex, prefetchCount);
	}

	public PrefetchMyCachePointingListStoreNotification(String cacheKey, int index, int currentCacheLastIndex, int prefetchCount)
	{
		super(cacheKey, index, currentCacheLastIndex, prefetchCount);
	}

}
