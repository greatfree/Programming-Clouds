package org.greatfree.framework.container.multicast.message;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastResponse;

/**
 * 
 * @author libing
 * 
 * 05/10/2022
 *
 */
public class HelloWorldBroadcastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 1717189169106155750L;
	
	private HelloWorld hw;

	public HelloWorldBroadcastResponse(HelloWorld hw, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORDL_BROADCAST_RESPONSE, collaboratorKey);
		this.hw = hw;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
