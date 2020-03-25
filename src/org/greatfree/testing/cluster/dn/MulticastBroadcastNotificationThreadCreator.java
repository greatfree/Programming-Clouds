package org.greatfree.testing.cluster.dn;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.BroadcastNotification;

/*
 * The creator aims to generate the instance of MulticastBroadcastNotificationThread for the BoundNotificationDispatcher so as to schedule the notification as tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/24/2016, Bing Li
public class MulticastBroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, MulticastBroadcastNotificationThread>
{

	@Override
	public MulticastBroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<BroadcastNotification> binder)
	{
		return new MulticastBroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}

}
