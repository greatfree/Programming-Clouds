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
public class HelloWorldAnycastResponse extends MulticastResponse
{
	private static final long serialVersionUID = 4553707257431079743L;
	
	private HelloWorld hl;

	public HelloWorldAnycastResponse(HelloWorld hl, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORLD_ANYCAST_RESPONSE, collaboratorKey);
		this.hl = hl;
	}

	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
