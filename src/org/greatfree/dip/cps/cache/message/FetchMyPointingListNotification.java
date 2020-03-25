package org.greatfree.dip.cps.cache.message;

import org.greatfree.cache.distributed.ListFetchNotification;

// Created: 07/11/2018, Bing Li
public class FetchMyPointingListNotification extends ListFetchNotification
{

//	public FetchMyPointingNotification(String cacheKey, int index, int currentCacheSize, int prefetchCount, int postfetchCount)
//	public FetchMyPointingListNotification(String cacheKey, int index, int prefetchCount)
//	public FetchMyPointingListNotification(String cacheKey, int index, int currentCacheSize, int prefetchCount)
	public FetchMyPointingListNotification(String cacheKey, int index, long currentCacheSize, int prefetchCount)
	{
		super(cacheKey, index, currentCacheSize, prefetchCount, true);
//		super(cacheKey, index, currentCacheSize, prefetchCount, postfetchCount);
//		super(cacheKey, index, prefetchCount);
	}
	
//	public FetchMyPointingNotification(String cacheKey, int startIndex, int endIndex, int currentCacheSize, int prefetchCount, int postfetchCount)
//	public FetchMyPointingListNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
//	public FetchMyPointingListNotification(String cacheKey, int startIndex, int endIndex, int currentCacheSize, int prefetchCount)
	public FetchMyPointingListNotification(String cacheKey, int startIndex, int endIndex, long currentCacheSize, int prefetchCount)
	{
//		super(cacheKey, startIndex, endIndex, currentCacheSize, prefetchCount, postfetchCount);
		super(cacheKey, startIndex, endIndex, currentCacheSize, prefetchCount, true);
//		super(cacheKey, startIndex, endIndex, prefetchCount);
	}

}
