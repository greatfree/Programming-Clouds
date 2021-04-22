package org.greatfree.framework.multicast.bound.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.framework.multicast.message.MessageDisposer;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;

// Created: 08/26/2018, Bing Li
public class BroadcastHelloWorldRequestThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>, BroadcastHelloWorldRequestThread>
{

	@Override
	public BroadcastHelloWorldRequestThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> binder)
	{
		return new BroadcastHelloWorldRequestThread(taskSize, dispatcherKey, binder);
	}

}
