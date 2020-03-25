package org.greatfree.abandoned.cache.distributed;

import org.greatfree.abandoned.cache.distributed.child.ChildMapRegistry;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * The interface is designed for the notification thread which needs to have the map registry to get the instance of the distributed map. 07/15/2017, Bing Li
 */

// Created: 07/15/2017, Bing Li
public interface CacheNotificationThreadCreatable<Notification extends OldMulticastMessage, NotificationThread extends NotificationQueue<Notification>, Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>>
{
	public NotificationThread createNotificationThreadInstance(int taskSize, ChildMapRegistry<Key, Value, Factory, DB> registry);
}
