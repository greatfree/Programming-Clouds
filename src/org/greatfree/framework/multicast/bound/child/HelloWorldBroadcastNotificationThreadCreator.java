package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.MessageDisposer;

// Created: 08/26/2018, Bing Li
public class HelloWorldBroadcastNotificationThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, HelloWorldBroadcastNotificationThread>
{

	@Override
	public HelloWorldBroadcastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		return new HelloWorldBroadcastNotificationThread(taskSize, dispatcherKey, binder);
	}

}
