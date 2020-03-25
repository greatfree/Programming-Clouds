package org.greatfree.cache.distributed;

import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * Kill-off prefetching notification is used for stack/queue since accessing those structures result in data' being removed from the structured. That is different from List/Map, in which data is retained even after being accessed. The prefetching for List/Map prefetching is named, list notification. 02/17/2018, Bing Li 
 */

// Created: 02/17/2018, Bing Li
abstract public class KickOffNotification
{
	private String key;
	private String cacheKey;

	// To implement the feature, it takes much effort to update stacks and queues. And it is not reasonable to do that in stacks and queues. 03/21/2019, Bing Li
	// The value key is used to peek data by key from stacks and queues. That is not a standard case in stacks and queues. But now I am replacing sorted caches in the N3W. Some caches have the requirement. 03/21/2019, Bing Li
//	private String valueKey;
	
	private int kickOffCount;
	private int prefetchCount;
	private int postfetchCount;
	
	private int startIndex;
	private int endIndex;
	
	private int fetchStartIndex;
	private int fetchEndIndex;
//	private int pRangeSize;

	private boolean isPeeking;
	
	private boolean isBlocking;
	
	/*
	 * To implement the feature, it takes much effort to update stacks and queues. And it is not reasonable to do that in stacks and queues. 03/21/2019, Bing Li
	 * The constructor is not standard ones for the stack. It gets data from the cache by key like a map. 03/20/2019, Bing Li
	 */
	/*
	public KickOffNotification(String cacheKey, String valueKey)
	{
		this.key = Tools.generateUniqueKey();
		this.cacheKey = cacheKey;
		this.valueKey = valueKey;
		this.kickOffCount = UtilConfig.NO_COUNT;
		this.prefetchCount = UtilConfig.NO_COUNT;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
		this.isPeeking = true;
	}
	*/
	
	public KickOffNotification(String cacheKey, int kickOffCount, int prefetchCount, boolean isPeeking, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.cacheKey = cacheKey;
//		this.valueKey = UtilConfig.EMPTY_STRING;
		this.kickOffCount = kickOffCount;
		this.prefetchCount = prefetchCount;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
		/*
		if (isPeeking)
		{
			this.pRangeSize = kickOffCount;
		}
		*/
		this.isPeeking = isPeeking;
		this.isBlocking = isBlocking;
	}
	
	public KickOffNotification(String cacheKey, int prefetchCount, boolean isPeeking, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.cacheKey = cacheKey;
//		this.valueKey = UtilConfig.EMPTY_STRING;
		this.kickOffCount = UtilConfig.ONE;
		this.prefetchCount = prefetchCount;
		this.startIndex = UtilConfig.NO_INDEX;
		this.endIndex = UtilConfig.NO_INDEX;
		/*
		if (isPeeking)
		{
			this.pRangeSize = UtilConfig.ONE;
		}
		*/
		this.isPeeking = isPeeking;
		this.isBlocking = isBlocking;
	}
	
	public KickOffNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.cacheKey = cacheKey;
//		this.valueKey = UtilConfig.EMPTY_STRING;
//		this.kickOffCount = UtilConfig.ZERO_SIZE;
		this.kickOffCount = endIndex - startIndex + 1;
		this.prefetchCount = prefetchCount;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
//		this.pRangeSize = endIndex - startIndex + 1;
		this.fetchStartIndex = this.endIndex;
		this.fetchEndIndex = this.fetchStartIndex + this.prefetchCount;
		this.isPeeking = true;
		this.isBlocking = isBlocking;
	}
	
	public KickOffNotification(String cacheKey, int index, int prefetchCount, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.cacheKey = cacheKey;
//		this.valueKey = UtilConfig.EMPTY_STRING;
//		this.kickOffCount = UtilConfig.ZERO_SIZE;
		this.kickOffCount = UtilConfig.ONE;
		this.prefetchCount = prefetchCount;
		this.startIndex = index;
		this.endIndex = index;
//		this.pRangeSize = endIndex - startIndex + 1;
		this.fetchStartIndex = this.endIndex;
		this.fetchEndIndex = this.fetchStartIndex + this.prefetchCount;
		this.isPeeking = true;
		this.isBlocking = isBlocking;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}

	/*
	public String getValueKey()
	{
		return this.valueKey;
	}
	*/
	
	public int getKickOffCount()
	{
		return this.kickOffCount;
	}
	
	public int getPrefetchCount()
	{
		return this.prefetchCount;
	}
	
	public void setPostfetchCount(int postfetchCount)
	{
		this.postfetchCount = postfetchCount;
	}
	
	public void setPostfetchIndex(int obtainedCount)
	{
		this.fetchStartIndex = this.fetchStartIndex + obtainedCount - 1;
		this.fetchEndIndex = this.fetchEndIndex + this.prefetchCount;
		this.postfetchCount = this.fetchEndIndex - this.fetchStartIndex;
	}
	
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}

	/*
	public int getPeekRangeSize()
	{
		return this.pRangeSize;
	}
	*/
	
	public boolean isPeeking()
	{
		return this.isPeeking;
	}
	
	public int getFetchStartIndex()
	{
		return this.fetchStartIndex;
	}
	
	public int getFetchEndIndex()
	{
		return this.fetchEndIndex;
	}
	
	public boolean isBlocking()
	{
		return this.isBlocking;
	}
}
