package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread>
{

	@Override
	public ClientHelloWorldUnicastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastNotificationThread(taskSize);
	}

}
