package org.greatfree.dsf.cps.cache.message;

import org.greatfree.cache.distributed.KickOffNotification;

// Created: 08/13/2018, Bing Li
public class FetchQueueNotification extends KickOffNotification
{

	public FetchQueueNotification(String cacheKey, int prefetchCount, boolean isPeeking)
	{
		super(cacheKey, prefetchCount, isPeeking, true);
	}

	public FetchQueueNotification(String cacheKey, int count, int prefetchCount, boolean isPeeking)
	{
		super(cacheKey, count, prefetchCount, isPeeking, true);
	}
	
	public FetchQueueNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, prefetchCount, true); 
	}
	
	public FetchQueueNotification(String cacheKey, int index, int prefetchCount)
	{
		super(cacheKey, index, prefetchCount, true); 
	}
}
