package org.greatfree.cache.message;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * This notification is transmitted within a cluster, which is the physical substrate for the distributed cache. In the cache, the notification encloses the data to be put into the distributed map. 07/01/2017, Bing Li
 */

// Created: 07/01/2017, Bing Li
//public class PutNotification<Key extends Serializable, Value extends SerializedKey<Key>> extends MulticastMessage
//public class PutNotification<Value extends CacheKey<String>> extends CacheNotification
public class PutNotification<Value extends CacheKey<String>> extends OldMulticastMessage
{
	private static final long serialVersionUID = 5205607356873182323L;
	
//	private String cacheKey;
	
	private Value v;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 07/01/2017, Bing Li
	 */
//	public PutNotification(String key, String cacheKey, Value v)
//	public PutNotification(String key, Value v)
	public PutNotification(Value v)
//	public PutNotification(String key, Value v, String cacheKey)
	{
//		super(CacheMessageType.PUT_NOTIFICATION, key, cacheKey);
		super(CacheMessageType.PUT_NOTIFICATION);
//		this.cacheKey = cacheKey;
		this.v = v;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public PutNotification(String key, HashMap<String, IPAddress> childrenServers, String cacheKey, Value v)
//	public PutNotification(String key, HashMap<String, IPAddress> childrenServers, Value v, String cacheKey)
//	public PutNotification(String key, HashMap<String, IPAddress> childrenServers, Value v)
	public PutNotification(HashMap<String, IPAddress> childrenServers, Value v)
	{
//		super(CacheMessageType.PUT_NOTIFICATION, key, childrenServers, cacheKey);
		super(CacheMessageType.PUT_NOTIFICATION, childrenServers);
//		this.cacheKey = cacheKey;
		this.v = v;
	}

	/*
	 * Expose the value to be put into the distributed map. 07/01/2017, Bing Li
	 */
	public Value getValue()
	{
		return this.v;
	}

	/*
	public String getCacheKey()
	{
		return this.cacheKey;
	}
	*/
}
