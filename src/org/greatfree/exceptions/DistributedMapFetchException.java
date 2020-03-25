package org.greatfree.exceptions;

// Created: 07/21/2018, Bing Li
public class DistributedMapFetchException extends Exception
{
	private static final long serialVersionUID = 3838705991505912720L;
	
	private long cacheSize;
	private int fetchCount;

	public DistributedMapFetchException(long cacheSize, int fetchCount)
	{
		this.cacheSize = cacheSize;
		this.fetchCount = fetchCount;
	}
	
	public long getCacheSize()
	{
		return this.cacheSize;
	}

	public int getFetchCount()
	{
		return this.fetchCount;
	}
}
