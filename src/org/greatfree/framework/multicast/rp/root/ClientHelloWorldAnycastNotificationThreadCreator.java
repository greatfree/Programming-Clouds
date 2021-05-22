package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldAnycastNotificationThreadCreator implements NotificationQueueCreator<HelloWorldAnycastNotification, ClientHelloWorldAnycastNotificationThread>
{

	@Override
	public ClientHelloWorldAnycastNotificationThread createInstance(int taskSize)
	{
		return new ClientHelloWorldAnycastNotificationThread(taskSize);
	}

}
