package org.greatfree.dsf.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldBroadcastRequest extends ServerMessage
{
	private static final long serialVersionUID = 5365111335239240745L;
	
	private HelloWorld hello;

	public ClientHelloWorldBroadcastRequest(HelloWorld hello)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_BROADCAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
