package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/12/2019, Bing Li
public class HelloWorldUnicastNotification extends Notification
{
	private static final long serialVersionUID = -3094558159374744982L;

	private HelloWorld hl;

	public HelloWorldUnicastNotification(HelloWorld hl)
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, MulticastApplicationID.HELLO_WORLD_UNICAST_NOTIFICATION);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
