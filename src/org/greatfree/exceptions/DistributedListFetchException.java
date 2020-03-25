package org.greatfree.exceptions;

// Created: 07/19/2018, Bing Li
public class DistributedListFetchException extends Exception
{
	private static final long serialVersionUID = -8157515320470952680L;
	
	private long cacheSize;
	private int fetchCount;
	
	public DistributedListFetchException(long cacheSize, int fetchCount)
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
