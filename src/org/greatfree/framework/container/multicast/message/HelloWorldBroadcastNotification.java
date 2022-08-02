package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/12/2019, Bing Li
public class HelloWorldBroadcastNotification extends ClusterNotification
{
	private static final long serialVersionUID = 5470041255679461797L;

	private HelloWorld hl;

	public HelloWorldBroadcastNotification(HelloWorld hl)
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, MultiAppID.HELLO_WORLD_BROADCAST_NOTIFICATION);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
