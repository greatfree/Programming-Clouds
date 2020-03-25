package org.greatfree.dip.container.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 01/12/2019, Bing Li
public class HelloWorldUnicastRequest extends Request
{
	private static final long serialVersionUID = -2269284378133182851L;

	private HelloWorld hw;

	public HelloWorldUnicastRequest(HelloWorld hw)
	{
		super(MulticastMessageType.UNICAST_REQUEST, MulticastApplicationID.HELLO_WORLD_UNICAST_REQUEST);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
