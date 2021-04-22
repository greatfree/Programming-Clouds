package org.greatfree.framework.multicast.message;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldAnycastNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -7602607511493547481L;

	private HelloWorld hl;
	
	public OldHelloWorldAnycastNotification(HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION);
		this.hl = hl;
	}

	public OldHelloWorldAnycastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
