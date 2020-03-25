package org.greatfree.dip.multicast.rp.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.dip.multicast.message.MulticastDIPMessageType;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 10/21/2018, Bing Li
public class HelloWorldAnycastRequest extends RPMulticastRequest
{
	private static final long serialVersionUID = -1964341586443084292L;

	private HelloWorld hw;

	public HelloWorldAnycastRequest(IPAddress rpAddress, HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_REQUEST, rpAddress);
		this.hw = hw;
	}

	/*
	public HelloWorldAnycastRequest(IPAddress rpAddress, HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastMessageType.HELLO_WORLD_ANYCAST_REQUEST, rpAddress, childrenServerMap);
		this.hw = hw;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
