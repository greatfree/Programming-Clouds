package org.greatfree.dip.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldAnycastRequest extends ServerMessage
{
	private static final long serialVersionUID = -5996906193351629295L;
	
	private HelloWorld hello;

	public ClientHelloWorldAnycastRequest(HelloWorld hello)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_ANYCAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
