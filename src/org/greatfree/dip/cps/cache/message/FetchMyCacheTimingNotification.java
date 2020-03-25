package org.greatfree.dip.cps.cache.message;

import org.greatfree.cache.distributed.ListFetchNotification;

// Created: 08/19/2018, Bing Li
public class FetchMyCacheTimingNotification extends ListFetchNotification
{
	public FetchMyCacheTimingNotification(String cacheKey, String rscKey)
	{
		super(cacheKey, rscKey, true);
	}

	public FetchMyCacheTimingNotification(String cacheKey, int index, int currentSize, int prefetchCount)
	{
		super(cacheKey, index, currentSize, prefetchCount, true);
	}

	public FetchMyCacheTimingNotification(String cacheKey, int startIndex, int endIndex, int currentSize, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, currentSize, prefetchCount, true);
	}

}