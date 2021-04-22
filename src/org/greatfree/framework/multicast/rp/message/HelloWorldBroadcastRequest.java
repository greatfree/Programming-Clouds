package org.greatfree.framework.multicast.rp.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 10/21/2018, Bing Li
public class HelloWorldBroadcastRequest extends RPMulticastRequest
{
	private static final long serialVersionUID = 7595079707655585642L;
	
	private HelloWorld hw;

	public HelloWorldBroadcastRequest(IPAddress rpAddress, HelloWorld hw)
	{
		super(MulticastDIPRPMessageType.HELLO_WORLD_BROADCAST_REQUEST, rpAddress);
		this.hw = hw;
	}

	/*
	public HelloWorldBroadcastRequest(IPAddress rpAddress, HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastMessageType.HELLO_WORLD_BROADCAST_REQUEST, rpAddress, childrenServerMap);
		this.hw = hw;
	}
	*/
	
	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
