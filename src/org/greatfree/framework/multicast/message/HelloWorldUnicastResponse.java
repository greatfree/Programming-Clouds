package org.greatfree.framework.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of HelloWorldUnicastRequest. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
public class HelloWorldUnicastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 7583663294458313886L;
	
	private HelloWorld hw;

//	public HelloWorldUnicastResponse(HelloWorld hw, String key, String collaboratorKey)
	public HelloWorldUnicastResponse(HelloWorld hw, String collaboratorKey)
	{
//		super(MulticastMessageType.HELLO_WORLD_UNICAST_RESPONSE, Tools.generateUniqueKey(), collaboratorKey);
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
