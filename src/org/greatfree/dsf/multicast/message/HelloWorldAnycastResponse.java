package org.greatfree.dsf.multicast.message;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of HelloWorldAnycastRequest. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
public class HelloWorldAnycastResponse extends MulticastResponse
{
	private static final long serialVersionUID = -4984663742943784120L;
	
	private HelloWorld hw;

//	public HelloWorldAnycastResponse(HelloWorld hw, String key, String collaboratorKey)
	public HelloWorldAnycastResponse(HelloWorld hw, String collaboratorKey)
	{
//		super(MulticastMessageType.HELLO_WORLD_ANYCAST_RESPONSE, Tools.generateUniqueKey(), collaboratorKey);
		super(MulticastDIPMessageType.HELLO_WORLD_ANYCAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
