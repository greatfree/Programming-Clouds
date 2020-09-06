package org.greatfree.dsf.container.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class ClientHelloWorldBroadcastRequest extends Request
{
	private static final long serialVersionUID = -7986558368644129729L;

	private HelloWorld hello;

	public ClientHelloWorldBroadcastRequest(HelloWorld hello)
	{
		super(MulticastApplicationID.CLIENT_HELLO_WORLD_BROADCAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
