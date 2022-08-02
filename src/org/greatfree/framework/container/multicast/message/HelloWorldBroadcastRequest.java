package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/12/2019, Bing Li
public class HelloWorldBroadcastRequest extends ClusterRequest
{
	private static final long serialVersionUID = -3883215146504654020L;
	
	private HelloWorld hl;

	public HelloWorldBroadcastRequest(HelloWorld hl)
	{
		super(MulticastMessageType.BROADCAST_REQUEST, MultiAppID.HELLO_WORLD_BROADCAST_REQUEST);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
