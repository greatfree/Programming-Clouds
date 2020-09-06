package org.greatfree.dsf.cps.cache.message;

import org.greatfree.cache.distributed.KickOffNotification;

// Created: 08/07/2018, Bing Li
public class FetchStackNotification extends KickOffNotification
{
	public FetchStackNotification(String cacheKey, int prefetchCount, boolean isPeeking)
	{
		super(cacheKey, prefetchCount, isPeeking, true);
	}
	
	public FetchStackNotification(String cacheKey, int count, int prefetchCount, boolean isPeeking)
	{
		super(cacheKey, count, prefetchCount, isPeeking, true);
	}

	public FetchStackNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
	{
		super(cacheKey, startIndex, endIndex, prefetchCount, true);
	}

	public FetchStackNotification(String cacheKey, int index, int prefetchCount)
	{
		super(cacheKey, index, prefetchCount, true);
	}
}
