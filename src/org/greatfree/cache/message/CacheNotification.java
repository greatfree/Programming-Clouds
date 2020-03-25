package org.greatfree.cache.message;

import java.util.Map;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * To enclose the cache key in the message, a new notification is designed for caching. With the cache key, the registered cache can be retrieved from the registry such that distributed caching operations be be accomplished. 07/22/2017, Bing Li
 */

// Created: 07/22/2017, Bing Li
public abstract class CacheNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = 2745122095263337376L;

	// The cache key the notification serves for. 07/22/2017, Bing Li
	private String cacheKey;

	/*
	 * Initialize the cache notification. The cache key is one additional parameter. 07/22/2017, Bing Li
	 */
//	public CacheNotification(int type, String key, String cacheKey)
	public CacheNotification(int type, String cacheKey)
	{
		super(type);
		this.cacheKey = cacheKey;
	}

	/*
	 * Initialize the cache notification. The cache key is one additional parameter. 07/22/2017, Bing Li
	 */
//	public CacheNotification(int type, String key, Map<String, IPAddress> children, String cacheKey)
	public CacheNotification(int type, Map<String, IPAddress> children, String cacheKey)
	{
		super(type, children);
		this.cacheKey = cacheKey;
	}

	/*
	 * Expose the cache key. 07/22/2017, Bing Li
	 */
	public String getCacheKey()
	{
		return this.cacheKey;
	}

	/*
	 * Set the cache key. 07/22/2017, Bing Li
	 */
	public void setCacheKey(String cacheKey)
	{
		this.cacheKey = cacheKey;
	}
}
