package org.greatfree.dip.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastResponse;

/*
 * The response is the counterpart of HelloWorldBroadcastRequest. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class HelloWorldBroadcastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3885107291746728630L;
	
	private HelloWorld hw;

//	public HelloWorldBroadcastResponse(HelloWorld hw, String key, String collaboratorKey)
	public HelloWorldBroadcastResponse(HelloWorld hw, String collaboratorKey)
	{
//		super(MulticastMessageType.HELLO_WORLD_BROADCAST_RESPONSE, Tools.generateUniqueKey(), collaboratorKey);
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
