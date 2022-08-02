package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 01/12/2019, Bing Li
public class HelloWorldAnycastRequest extends ClusterRequest
{
	private static final long serialVersionUID = 6234741014663080779L;

	private HelloWorld hw;

	public HelloWorldAnycastRequest(HelloWorld hw)
	{
		super(MulticastMessageType.ANYCAST_REQUEST, MultiAppID.HELLO_WORLD_ANYCAST_REQUEST);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
