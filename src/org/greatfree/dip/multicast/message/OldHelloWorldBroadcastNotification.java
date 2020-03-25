package org.greatfree.dip.multicast.message;

import java.util.HashMap;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.abandoned.OldMulticastMessage;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldBroadcastNotification extends OldMulticastMessage
{
	private static final long serialVersionUID = -4517833259853985501L;

	private HelloWorld hl;

	public OldHelloWorldBroadcastNotification(HelloWorld hl)
	{
//		super(MulticastMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION, Tools.generateUniqueKey());
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION);
		this.hl = hl;
	}

	public OldHelloWorldBroadcastNotification(HashMap<String, IPAddress> childrenServers, HelloWorld hl)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION, childrenServers);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
