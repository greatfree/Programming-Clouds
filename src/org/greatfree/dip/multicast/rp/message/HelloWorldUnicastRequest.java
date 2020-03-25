package org.greatfree.dip.multicast.rp.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.RPMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 10/21/2018, Bing Li
public class HelloWorldUnicastRequest extends RPMulticastRequest
{
	private static final long serialVersionUID = -4655151260890247128L;

	private HelloWorld hw;

	public HelloWorldUnicastRequest(IPAddress rpAddress, HelloWorld hw)
	{
		super(MulticastDIPRPMessageType.HELLO_WORLD_UNICAST_REQUEST, rpAddress);
		this.hw = hw;
	}
	
	/*
	public HelloWorldUnicastRequest(IPAddress rpAddress, HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastMessageType.HELLO_WORLD_UNICAST_REQUEST, rpAddress, childrenServerMap);
		this.hw = hw;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
