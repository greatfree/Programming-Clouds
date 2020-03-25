package org.greatfree.dip.multicast.message;

import java.util.HashMap;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldUnicastNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -5176975369232434522L;

	private HelloWorld hl;
	
	public OldHelloWorldUnicastNotification(HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION);
		this.hl = hl;
	}

	public OldHelloWorldUnicastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
