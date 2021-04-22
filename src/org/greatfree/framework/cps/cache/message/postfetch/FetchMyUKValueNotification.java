package org.greatfree.framework.cps.cache.message.postfetch;

import org.greatfree.cache.distributed.ListFetchNotification;

// Created: 02/25/2019, Bing Li
public class FetchMyUKValueNotification extends ListFetchNotification
{

//	public FetchMyUKValueNotification(String cacheKey, int index, int currentCacheSize, int prefetchCount)
	public FetchMyUKValueNotification(String cacheKey, int index, long currentCacheSize, int prefetchCount)
	{
		super(cacheKey, index, currentCacheSize, prefetchCount, true);
	}
	
//	public FetchMyUKValueNotification(String cacheKey, int startIndex, int endIndex, int currentCacheSize, int prefetchCount)
	public FetchMyUKValueNotification(String cacheKey, int startIndex, int endIndex, long currentCacheSize, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, currentCacheSize, prefetchCount, true);
	}

}
