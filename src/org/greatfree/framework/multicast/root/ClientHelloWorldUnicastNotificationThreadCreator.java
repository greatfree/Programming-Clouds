package org.greatfree.framework.multicast.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 08/26/2018, Bing Li
class ClientHelloWorldUnicastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread>
{

	@Override
	public ClientHelloWorldUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastNotificationThread(taskSize);
	}

}
