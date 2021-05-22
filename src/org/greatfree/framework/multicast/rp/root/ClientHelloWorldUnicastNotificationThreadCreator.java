package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldUnicastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread>
{

	@Override
	public ClientHelloWorldUnicastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastNotificationThread(taskSize);
	}

}
