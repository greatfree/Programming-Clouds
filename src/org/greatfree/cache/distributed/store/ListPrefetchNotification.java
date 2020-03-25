package org.greatfree.cache.distributed.store;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

// Created: 02/08/2018, Bing Li
//public abstract class PrefetchNotification
abstract public class ListPrefetchNotification
{
	private String cacheKey;
	private int startIndex;
	private int endIndex;
	private int resourceIndex;
	private int prefetchCount;
	private int prefetchStartIndex;
	private int prefetchEndIndex;
	
//	public ListPrefetchNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
	public ListPrefetchNotification(String cacheKey, int startIndex, int endIndex, int currentCacheLastIndex, int prefetchCount)
	{
		this.cacheKey = cacheKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.prefetchCount = prefetchCount;
		this.prefetchStartIndex = currentCacheLastIndex + 1;
		this.prefetchEndIndex = this.prefetchStartIndex + this.prefetchCount - 1;
	}

	public ListPrefetchNotification(String cacheKey, int index, int currentCacheLastIndex, int prefetchCount)
	{
		this.cacheKey = cacheKey;
		this.resourceIndex = index;
		this.prefetchCount = prefetchCount;
		this.prefetchStartIndex = currentCacheLastIndex + 1;
		this.prefetchEndIndex = this.prefetchStartIndex + this.prefetchCount - 1;
	}

//	public ListPrefetchNotification(String cacheKey, int endIndex, int prefetchCount)
	/*
	public ListPrefetchNotification(String cacheKey, int endIndex, int currentCacheLastIndex, int prefetchCount)
	{
		this.cacheKey = cacheKey;
		this.startIndex = 0;
		this.endIndex = endIndex;
		this.prefetchCount = prefetchCount;
//		this.prefetchStartIndex = endIndex + 1;
		this.prefetchStartIndex = currentCacheLastIndex + 1;
		this.prefetchEndIndex = this.prefetchStartIndex + this.prefetchCount - 1;
	}
	*/

	/*
	public ListPrefetchNotification(String cacheKey, int prefetchCount)
	{
		this.cacheKey = cacheKey;
		this.startIndex = 0;
		this.endIndex = 0;
		this.prefetchCount = prefetchCount;
		this.prefetchStartIndex = 0;
		this.prefetchEndIndex = 0;
	}
	*/

	/*
	public boolean shouldPrefetch()
	{
		return this.prefetchCount > 0;
	}
	*/

	public String getCacheKey()
	{
		return this.cacheKey;
	}

	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}
	
	public int getResourceIndex()
	{
		return this.resourceIndex;
	}

	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
	
	public int getPrefetchStartIndex()
	{
		return this.prefetchStartIndex;
	}
	
	public int getPrefetchEndIndex()
	{
		return this.prefetchEndIndex;
	}
}
