package org.greatfree.framework.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.ServerMessage;

// Created: 08/26/2018, Bing Li
public class ClientHelloWorldUnicastRequest extends ServerMessage
{
	private static final long serialVersionUID = -6402051105401292270L;
	
	private HelloWorld hello;

	public ClientHelloWorldUnicastRequest(HelloWorld hello)
	{
		super(MulticastDIPMessageType.CLIENT_HELLO_WORLD_UNICAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
