package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldAnycastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldAnycastNotification, ClientHelloWorldAnycastNotificationThread>
{

	@Override
	public ClientHelloWorldAnycastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientHelloWorldAnycastNotificationThread(taskSize);
	}

}
