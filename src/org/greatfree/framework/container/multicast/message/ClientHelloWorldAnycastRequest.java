package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class ClientHelloWorldAnycastRequest extends Request
{
	private static final long serialVersionUID = -9168298786666541207L;
	
	private HelloWorld hello;

	public ClientHelloWorldAnycastRequest(HelloWorld hello)
	{
		super(MulticastApplicationID.CLIENT_HELLO_WORLD_ANYCAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
