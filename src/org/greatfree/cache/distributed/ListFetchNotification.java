package org.greatfree.cache.distributed;

import org.greatfree.util.Tools;

/*
 * The version is tested in the Clouds project. 08/22/2018, Bing Li
 */

/*
 * The notification is a little bit complicated. I hope the prefetch and postfetch can be separated. 07/04/2018, Bing Li
 */

// Created: 02/25/2018, Bing Li
abstract public class ListFetchNotification
{
	private String key;
	private int type;
	private String cacheKey;
	private String resourceKey;
	private int resourceIndex;
	private int startIndex;
	private int endIndex;
//	private int currentCacheSize;
	private int prefetchCount;
	private int prefetchStartIndex;
	private int prefetchEndIndex;
//	private int postfetchCount;
	// Usually, the postfetching is performed when the retrieved data is null. If so, the value is false. Sometimes, if the retrieved data size is less than one particular size, it is required to postfetch. If so, the value is true.
	private boolean isPostfetchForNullOnly;
	
	// The field is used to determine whether the postfetching is required. Since the operation is a blocking one, it must lower the responsive speed to the client. When the client UI is affected, it is not good. So in the case, it is highly suggested to ignore the postfetching. 07/05/2019, Bing Li
	private boolean isBlocking;
	
//	public ListPrePostfetchNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
//	public ListPrePostfetchNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount, int postfetchCount)
//	public ListFetchNotification(String cacheKey, int startIndex, int endIndex, int currentCacheSize, int prefetchCount, int postfetchCount)
//	public ListFetchNotification(String cacheKey, int startIndex, int endIndex, int prefetchCount)
//	public ListFetchNotification(String cacheKey, int startIndex, int endIndex, int currentCacheSize, int prefetchCount)
	public ListFetchNotification(String cacheKey, int startIndex, int endIndex, long currentCacheSize, int prefetchCount, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		// FETCH = PREFETCH + POSTFETCH. It is possible to perform either of them. 07/14/2018, Bing Li
		this.type = FetchConfig.FETCH_RESOURCES_BY_RANGE;
		this.cacheKey = cacheKey;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
//		this.currentCacheSize = currentCacheSize;
		this.prefetchCount = prefetchCount;
//		this.prefetchStartIndex = endIndex + 1;
		this.prefetchStartIndex = (int)currentCacheSize;
		this.prefetchEndIndex = this.prefetchStartIndex + this.prefetchCount - 1;
//		this.postfetchCount = postfetchCount;
		this.isPostfetchForNullOnly = false;
		this.isBlocking = isBlocking;
	}
	
	/*
	 * To avoid confusions, retrieving by top is merged with retrieving by range. 07/16/2018, Bing Li
	 */
	/*
	 * The option has two usages. 1) Getting top; 2) Getting the one at the endIndex. It depends on the method where the notification is created. 07/13/2018, Bing Li
	 */
//	public ListPrePostfetchNotification(String cacheKey, int endIndex, int prefetchCount)
//	public ListPrePostfetchNotification(String cacheKey, int endIndex, int prefetchCount, int postfetchCount)
//	public ListFetchNotification(String cacheKey, int endIndex, int currentCacheSize, int prefetchCount, int postfetchCount)
//	public ListFetchNotification(String cacheKey, int index, int currentCacheSize, int prefetchCount, int postfetchCount)
//	public ListFetchNotification(String cacheKey, int index, int prefetchCount)
//	public ListFetchNotification(String cacheKey, int index, int currentCacheSize, int prefetchCount)
	public ListFetchNotification(String cacheKey, int index, long currentCacheSize, int prefetchCount, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		// FETCH = PREFETCH + POSTFETCH. It is possible to perform either of them. 07/14/2018, Bing Li
//		this.type = FetchConfig.FETCH_RESOURCES_BY_TOP;
		this.type = FetchConfig.FETCH_RESOURCE_BY_INDEX;
		this.cacheKey = cacheKey;
		this.startIndex = 0;
//		this.endIndex = index;
		this.resourceIndex = index;
//		this.currentCacheSize = currentCacheSize;
		this.prefetchCount = prefetchCount;
//		this.prefetchStartIndex = endIndex + 1;
//		this.prefetchStartIndex = index + 1;
		this.prefetchStartIndex = (int)currentCacheSize;
		this.prefetchEndIndex = this.prefetchStartIndex + this.prefetchCount - 1;
//		this.postfetchCount = postfetchCount;
		this.isPostfetchForNullOnly = false;
		this.isBlocking = isBlocking;
	}
	
	public ListFetchNotification(String cacheKey, String rscKey, boolean isBlocking)
	{
		this.key = Tools.generateUniqueKey();
		this.type = FetchConfig.POSTFETCH_RESOURCE_BY_KEY;
		this.cacheKey = cacheKey;
		this.resourceKey = rscKey;
		this.isPostfetchForNullOnly = false;
		this.isBlocking = isBlocking;
	}
	
//	public ListPrePostfetchNotification(String cacheKey)
	/*
	 * The option is not a proper design. Usually, it is not reasonable to get all of the data from a cache. 07/13/2018, Bing Li
	 */
	/*
	public ListFetchNotification(String cacheKey, int postfetchCount, boolean isForNullOnly)
	{
		this.key = Tools.generateUniqueKey();
		this.type = FetchConfig.POSTFETCH_ALL_RESOURCES_BY_CACHE_KEY;
		this.cacheKey = cacheKey;
		this.postfetchCount = postfetchCount;
		this.isPostfetchForNullOnly = isForNullOnly;
	}
	*/
	
//	public ListPrePostfetchNotification(String cacheKey, int index)
	/*
	 * The option is replaced by the one for getting the top data. 07/13/2018, Bing Li
	 */
	/*
	public ListPrePostfetchNotification(String cacheKey, int index, int postfetchCount)
	{
		this.key = Tools.generateUniqueKey();
		this.type = FetchConfig.FETCH_RESOURCE_BY_INDEX;
		this.cacheKey = cacheKey;
		this.resourceIndex = index;
		this.postfetchCount = postfetchCount;
		this.isPostfetchForNullOnly = false;
	}
	*/

	public String getKey()
	{
		return this.key;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public String getResourceKey()
	{
		return this.resourceKey;
	}
	
	public int getResourceIndex()
	{
		return this.resourceIndex;
	}

	/*
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}
	
	public void setPostfetchCount(int count)
	{
		this.postfetchCount = count;
	}
	*/
	
	/*
	public void setStartIndex(int index)
	{
		this.startIndex = index;
	}
	*/
	
	public int getStartIndex()
	{
		return this.startIndex;
	}
	
	public int getEndIndex()
	{
		return this.endIndex;
	}

	/*
	public int getCurrentCacheSize()
	{
		return this.currentCacheSize;
	}
	*/

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

	public boolean isPostfetchForNullOnly()
	{
		return this.isPostfetchForNullOnly;
	}
	
	public boolean isBlocking()
	{
		return this.isBlocking;
	}
}
