package org.greatfree.framework.cps.cache.message;

import org.greatfree.cache.distributed.ListFetchNotification;

// Created: 07/22/2018, Bing Li
public class FetchMyCachePointingNotification extends ListFetchNotification
{
	public FetchMyCachePointingNotification(String cacheKey, String resourceKey)
	{
		super(cacheKey, resourceKey, true);
	}

	public FetchMyCachePointingNotification(String cacheKey, int index, int currentSize, int prefetchCount)
	{
		super(cacheKey, index, currentSize, prefetchCount, true);
	}

	public FetchMyCachePointingNotification(String cacheKey, int startIndex, int endIndex, int currentSize, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, currentSize, prefetchCount, true);
	}
}
