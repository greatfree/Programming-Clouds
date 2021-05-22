package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldBroadcastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastNotification, ClientHelloWorldBroadcastNotificationThread>
{

	@Override
	public ClientHelloWorldBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldBroadcastNotificationThread(taskSize);
	}

}
