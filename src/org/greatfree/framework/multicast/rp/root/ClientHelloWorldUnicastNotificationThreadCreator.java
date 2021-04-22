package org.greatfree.framework.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldUnicastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread>
{

	@Override
	public ClientHelloWorldUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastNotificationThread(taskSize);
	}

}
