package org.greatfree.framework.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldAnycastRequest extends OldMulticastRequest
{
	private static final long serialVersionUID = -7533336498760108451L;

	private HelloWorld hw;

	public OldHelloWorldAnycastRequest(HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_REQUEST);
		this.hw = hw;
	}

	public OldHelloWorldAnycastRequest(HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_REQUEST, childrenServerMap);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
