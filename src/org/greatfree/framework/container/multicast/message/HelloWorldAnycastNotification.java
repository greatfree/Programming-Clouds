package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterNotification;

// Created: 01/12/2019, Bing Li
public class HelloWorldAnycastNotification extends ClusterNotification
{
	private static final long serialVersionUID = -4037208811160850382L;
	
	private HelloWorld hl;

	public HelloWorldAnycastNotification(HelloWorld hl)
	{
		super(MulticastMessageType.ANYCAST_NOTIFICATION, MultiAppID.HELLO_WORLD_ANYCAST_NOTIFICATION);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
