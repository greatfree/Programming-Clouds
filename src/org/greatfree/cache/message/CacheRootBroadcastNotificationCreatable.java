package org.greatfree.cache.message;

import java.io.Serializable;
import java.util.HashMap;

import org.greatfree.util.IPAddress;

/*
 * The interface aims to define the methods to create notifications for caching operations. Compared with RootBroadcastNotificationCreatable, the cache key is the additional parameter for the caching notification. 11/10/2014, Bing Li
 */

// Created: 07/22/2017, Bing Li
public interface CacheRootBroadcastNotificationCreatable<Notification extends CacheNotification, NotificationData extends Serializable>
{
	// The interface to create a cache notification with children information. It denotes the node receiving the notification needs to forward the message to those children. The cache key is one additional parameter for the interface. 07/22/2017, Bing Li
	public Notification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, NotificationData message, String cacheKey);
	// The interface to create a cache notification without children information. It represents that the multicasting is ended in the node who receives the message.  The cache key is one additional parameter for the interface. 07/22/2017, Bing Li
	public Notification createInstanceWithoutChildren(NotificationData message, String cacheKey);
}
