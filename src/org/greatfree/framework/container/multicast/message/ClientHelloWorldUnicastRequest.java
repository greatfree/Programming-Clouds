package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.container.Request;

// Created: 01/12/2019, Bing Li
public class ClientHelloWorldUnicastRequest extends Request
{
	private static final long serialVersionUID = 3873485249239830703L;

	private HelloWorld hello;

	public ClientHelloWorldUnicastRequest(HelloWorld hello)
	{
		super(MultiAppID.CLIENT_HELLO_WORLD_UNICAST_REQUEST);
		this.hello = hello;
	}

	public HelloWorld getHello()
	{
		return this.hello;
	}
}
