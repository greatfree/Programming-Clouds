package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldRootIPAddressBroadcastNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of RootIPAddressBroadcastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class RootIPAddressBroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<OldRootIPAddressBroadcastNotification, MessageDisposer<OldRootIPAddressBroadcastNotification>, RootIPAddressBroadcastNotificationThread>
{

	@Override
	public RootIPAddressBroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldRootIPAddressBroadcastNotification> binder)
	{
		return new RootIPAddressBroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}

}
