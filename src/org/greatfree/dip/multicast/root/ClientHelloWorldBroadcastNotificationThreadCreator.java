package org.greatfree.dip.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastNotification;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldBroadcastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldBroadcastNotification, ClientHelloWorldBroadcastNotificationThread>
{

	@Override
	public ClientHelloWorldBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientHelloWorldBroadcastNotificationThread(taskSize);
	}

}
