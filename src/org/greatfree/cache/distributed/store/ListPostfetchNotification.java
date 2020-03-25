package org.greatfree.cache.distributed.store;

import org.greatfree.util.Tools;

/*
 * Postfetching is performed only once at the condition when no any data exists in the cache. Once if the data is available, future accessing is converted to prefetching. Postfetching lowers the performance of the system. So it is necessary to avoid the times to perform it. 02/25/2018, Bing Li
 */

// Created: 02/25/2018, Bing Li
// public class ListPostfetchRequest extends ServerMessage
public class ListPostfetchNotification
{
	private String key;
	private String remoteServerKey;
	private String cacheKey;
	private String rscKey;
	private int postfetchCount;
//	private long timeout;

//	public ListPostfetchRequest(int type, String cacheKey, String rscKey, int postfetchCount, int timeout)
//	public ListPostfetchNotification(int type, String remoteServerKey, String cacheKey, String rscKey, int postfetchCount)
	public ListPostfetchNotification(String remoteServerKey, String cacheKey, String rscKey, int postfetchCount)
	{
//		super(type);
		this.key = Tools.generateUniqueKey();
		this.remoteServerKey = remoteServerKey;
		this.cacheKey = cacheKey;
		this.rscKey = rscKey;
		this.postfetchCount = postfetchCount;
//		this.timeout = timeout;
	}
	
//	public ListPostfetchNotification(int type, String remoteServerKey, String cacheKey, String rscKey)
	public ListPostfetchNotification(String remoteServerKey, String cacheKey, String rscKey)
	{
//		super(type);
		this.key = Tools.generateUniqueKey();
		this.remoteServerKey = remoteServerKey;
		this.cacheKey = cacheKey;
		this.rscKey = rscKey;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getRemoteServerKey()
	{
		return this.remoteServerKey;
	}
	
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	
	public String getResourceKey()
	{
		return this.rscKey;
	}
	
	public int getPostfetchCount()
	{
		return this.postfetchCount;
	}

	/*
	public long getTimeout()
	{
		return this.timeout;
	}
	*/
}
