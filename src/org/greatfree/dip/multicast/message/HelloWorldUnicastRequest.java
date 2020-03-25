package org.greatfree.dip.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastRequest;

/*
 * The request is unicast to one particular one of the distributed nodes in a cluster and then the node that receives the request needs to respond to the root individually. The responses are merged together as the final response. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
public class HelloWorldUnicastRequest extends MulticastRequest
{
	private static final long serialVersionUID = -2317366858101531395L;
	
	private HelloWorld hw;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldUnicastRequest(String key, String collaboratorKey, HelloWorld hw)
	public HelloWorldUnicastRequest(HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_UNICAST_REQUEST);
		this.hw = hw;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldUnicastRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	/*
	public HelloWorldUnicastRequest(HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastMessageType.HELLO_WORLD_UNICAST_REQUEST, childrenServerMap);
		this.hw = hw;
	}
	*/

	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
