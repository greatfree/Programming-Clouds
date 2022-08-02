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
public class HelloWorldUnicastResponse extends MulticastResponse
{
	private static final long serialVersionUID = -4738842871001132593L;
	
	private HelloWorld hl;

	public HelloWorldUnicastResponse(HelloWorld hl, String collaboratorKey)
	{
		super(MultiAppID.HELLO_WORLD_UNICAST_RESPONSE, collaboratorKey);
		this.hl = hl;
	}
	
	public HelloWorld getHelloWorld()
	{
		return this.hl;
	}
}
