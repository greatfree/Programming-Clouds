package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 01/12/2019, Bing Li
public class HelloWorldAnycastRequest extends Request
{
	private static final long serialVersionUID = 6234741014663080779L;

	private HelloWorld hw;

	public HelloWorldAnycastRequest(HelloWorld hw)
	{
		super(MulticastMessageType.ANYCAST_REQUEST, MulticastApplicationID.HELLO_WORLD_ANYCAST_REQUEST);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
