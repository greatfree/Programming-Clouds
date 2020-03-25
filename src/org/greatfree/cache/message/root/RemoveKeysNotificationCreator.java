package org.greatfree.cache.message.root;

import java.util.HashMap;
import java.util.HashSet;

import org.greatfree.cache.message.RemoveKeysNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the key removal notification that is sent to distributed nodes broadcastly in a cluster. 07/12/2017, Bing Li
 */

// Created: 07/12/2017, Bing Li
public class RemoveKeysNotificationCreator implements RootBroadcastNotificationCreatable<RemoveKeysNotification, HashSet<String>>
{

	/*
	 * The constructor is used to create keys removal notifications when the scale of the cluster is even larger than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public RemoveKeysNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HashSet<String> message)
	{
		return new RemoveKeysNotification(childrenMap, message);
	}

	/*
	 * The method is used to create keys removal notifications when the scale of the cluster is even smaller than the value of the root branch count. 07/12/2017, Bing Li
	 */
	@Override
	public RemoveKeysNotification createInstanceWithoutChildren(HashSet<String> message)
	{
		return new RemoveKeysNotification(message);
	}
}
