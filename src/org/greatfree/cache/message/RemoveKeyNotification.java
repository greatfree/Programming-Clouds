package org.greatfree.cache.message;

import java.util.HashMap;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * The notification is sent to a single node in the cluster, i.e., through the way of unicasting such that the value with the key is removed from the cache. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class RemoveKeyNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -7188687210458080297L;
	
	private String valueKey;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 07/12/2017, Bing Li
	 */
//	public RemoveKeyNotification(String key, String valueKey)
	public RemoveKeyNotification(String valueKey)
	{
		super(CacheMessageType.REMOVE_KEY_NOTIFICATION);
		this.valueKey = valueKey;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 07/12/2017, Bing Li
	 */
//	public RemoveKeyNotification(String key, HashMap<String, IPAddress> childrenServers, String valueKey)
	public RemoveKeyNotification(HashMap<String, IPAddress> childrenServers, String valueKey)
	{
		super(CacheMessageType.REMOVE_KEY_NOTIFICATION, childrenServers);
		this.valueKey = valueKey;
	}

	public String getValueKey()
	{
		return this.valueKey;
	}
}
