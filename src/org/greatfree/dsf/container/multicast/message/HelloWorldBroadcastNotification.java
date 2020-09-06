package org.greatfree.dsf.container.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 01/12/2019, Bing Li
public class HelloWorldBroadcastNotification extends Notification
{
	private static final long serialVersionUID = 5470041255679461797L;

	private HelloWorld hl;

	public HelloWorldBroadcastNotification(HelloWorld hl)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, MulticastApplicationID.HELLO_WORLD_BROADCAST_NOTIFICATION);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
