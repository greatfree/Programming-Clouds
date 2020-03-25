package org.greatfree.cache.message;

import java.util.HashMap;
import java.util.Set;

import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

/*
 * The notification contains the keys to be removed from the distributed map. It is broadcast to the cluster and each node needs to remove the keys that exist in the local cache only. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class RemoveKeysNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -7874131581144026817L;
	
	private Set<String> keys;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public RemoveKeysNotification(String key, Set<String> keys)
	public RemoveKeysNotification(Set<String> keys)
	{
		super(CacheMessageType.REMOVE_KEYS_NOTIFICATION);
		this.keys = keys;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public RemoveKeysNotification(String key, HashMap<String, IPAddress> childrenServers, Set<String> keys)
	public RemoveKeysNotification(HashMap<String, IPAddress> childrenServers, Set<String> keys)
	{
		super(CacheMessageType.REMOVE_KEYS_NOTIFICATION, childrenServers);
		this.keys = keys;
	}

	public Set<String> getKeys()
	{
		return this.keys;
	}
}
