package org.greatfree.testing.cluster.dn;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.BroadcastNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of BroadcastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class BroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, BroadcastNotificationThread>
{

	@Override
	public BroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<BroadcastNotification> binder)
	{
		return new BroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}

}
