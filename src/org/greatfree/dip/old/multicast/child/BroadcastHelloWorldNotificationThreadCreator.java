package org.greatfree.dip.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastNotification;

/*
 * The creator aims to generate the instance of BroadcastHelloWorldNotificationThread for the BoundNotificationDispatcher so as to schedule the notification as tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 05/16/2017, Bing Li
class BroadcastHelloWorldNotificationThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, BroadcastHelloWorldNotificationThread>
{

	@Override
	public BroadcastHelloWorldNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		return new BroadcastHelloWorldNotificationThread(taskSize, dispatcherKey, binder);
	}

}
