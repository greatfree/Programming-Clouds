package org.greatfree.cache.message.root.update;

import java.util.HashMap;

import org.greatfree.abandoned.cache.distributed.CacheValue;
import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

// Created: 07/17/2017, Bing Li
public class PutNotificationCreator implements RootBroadcastNotificationCreatable<PutNotification, CacheValue>
{

	@Override
	public PutNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, CacheValue message)
	{
		return new PutNotification(childrenMap, message);
	}

	@Override
	public PutNotification createInstanceWithoutChildren(CacheValue message)
	{
		return new PutNotification(message);
	}

}
