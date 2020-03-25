package org.greatfree.dip.old.multicast.child;

import org.greatfree.concurrency.reactive.BoundNotificationThreadCreatable;
import org.greatfree.dip.multicast.message.MessageDisposer;
import org.greatfree.dip.multicast.message.OldHelloWorldBroadcastRequest;

/*
 * The creator initialize the instance of the thread, TransmitBroadcastRequestThread. The BoundNotificationDispatcher can schedule tasks to the thread. 11/29/2014, Bing Li
 */

// Created: 05/20/2017, Bing Li
class BroadcastHelloWorldRequestThreadCreator implements BoundNotificationThreadCreatable<OldHelloWorldBroadcastRequest, MessageDisposer<OldHelloWorldBroadcastRequest>, BroadcastHelloWorldRequestThread>
{

	@Override
	public BroadcastHelloWorldRequestThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MessageDisposer<OldHelloWorldBroadcastRequest> binder)
	{
		return new BroadcastHelloWorldRequestThread(taskSize, dispatcherKey, binder);
	}

}
