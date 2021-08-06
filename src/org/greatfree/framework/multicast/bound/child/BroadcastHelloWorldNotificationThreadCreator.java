package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.MessageDisposer;

// Created: 08/26/2018, Bing Li
public class BroadcastHelloWorldNotificationThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastNotification, MessageDisposer<OldHelloWorldBroadcastNotification>, BroadcastHelloWorldNotificationThread>
{

	@Override
	public BroadcastHelloWorldNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastNotification> binder)
	{
		return new BroadcastHelloWorldNotificationThread(taskSize, dispatcherKey, binder);
	}

}
