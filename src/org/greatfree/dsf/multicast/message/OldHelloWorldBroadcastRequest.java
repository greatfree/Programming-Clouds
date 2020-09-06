package org.greatfree.dsf.multicast.message;

import java.util.HashMap;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.abandoned.OldMulticastRequest;
import org.greatfree.util.IPAddress;

// Created: 12/15/2018, Bing Li
public class OldHelloWorldBroadcastRequest extends OldMulticastRequest
{
	private static final long serialVersionUID = 1169721257859506709L;
	
	private HelloWorld hw;
	
	public OldHelloWorldBroadcastRequest(HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_REQUEST);
		this.hw = hw;
	}

	public OldHelloWorldBroadcastRequest(HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_REQUEST, childrenServerMap);
		this.hw = hw;
	}

	
	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
