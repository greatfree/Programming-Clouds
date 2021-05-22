package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldBroadcastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldBroadcastNotification, ClientHelloWorldBroadcastNotificationThread>
{

	@Override
	public ClientHelloWorldBroadcastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldBroadcastNotificationThread(taskSize);
	}

}
