package org.greatfree.dip.multicast.message;

import java.util.HashMap;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldUnicastRequest extends OldMulticastRequest
{
	private static final long serialVersionUID = 1374124266987157174L;

	private HelloWorld hw;

	public OldHelloWorldUnicastRequest(HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_REQUEST);
		this.hw = hw;
	}

	public OldHelloWorldUnicastRequest(HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_REQUEST, childrenServerMap);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
