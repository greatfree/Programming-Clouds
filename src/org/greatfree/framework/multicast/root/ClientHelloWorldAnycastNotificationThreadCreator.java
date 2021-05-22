package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldAnycastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldAnycastNotification, ClientHelloWorldAnycastNotificationThread>
{

	@Override
	public ClientHelloWorldAnycastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldAnycastNotificationThread(taskSize);
	}

}
