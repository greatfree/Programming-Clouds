package org.greatfree.dip.multicast.rp.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;
import org.greatfree.dip.multicast.message.HelloWorldUnicastNotification;

// Created: 10/21/2018, Bing Li
public class ClientHelloWorldUnicastNotificationThreadCreator implements NotificationThreadCreatable<HelloWorldUnicastNotification, ClientHelloWorldUnicastNotificationThread>
{

	@Override
	public ClientHelloWorldUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientHelloWorldUnicastNotificationThread(taskSize);
	}

}
