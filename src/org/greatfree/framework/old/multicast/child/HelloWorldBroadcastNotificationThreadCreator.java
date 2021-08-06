package org.greatfree.framework.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.MessageDisposer;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of BroadcastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 05/15/2017, Bing Li
class HelloWorldBroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}

}
