package org.greatfree.dsf.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.dsf.multicast.message.MessageDisposer;
import org.greatfree.dsf.multicast.message.OldShutdownChildrenBroadcastNotification;

/*
 * The creator aims to generate the instance of BroadcastShutdownNotificationThread for the BoundNotificationDispatcher so as to schedule the notification as tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class BroadcastShutdownNotificationThreadCreator implements BoundNotificationThreadCreatable<OldShutdownChildrenBroadcastNotification, MessageDisposer<OldShutdownChildrenBroadcastNotification>, BroadcastShutdownNotificationThread>
{

	@Override
	public BroadcastShutdownNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldShutdownChildrenBroadcastNotification> binder)
	{
		return new BroadcastShutdownNotificationThread(taskSize, dispatcherKey, binder);
	}

}
