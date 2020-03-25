package org.greatfree.dip.multicast.message;

import org.greatfree.dip.multicast.HelloWorld;
import org.greatfree.message.multicast.MulticastRequest;

/*
 * The request is broadcast to each of the distributed node in a cluster and then each of the node needs to respond to the root individually. The responses are merged together as the final response. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class HelloWorldBroadcastRequest extends MulticastRequest
{
	private static final long serialVersionUID = 7467117351318715616L;
	
	private HelloWorld hw;

	/*
	 * The constructor is used when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldBroadcastRequest(String key, String collaboratorKey, HelloWorld hw)
	public HelloWorldBroadcastRequest(HelloWorld hw)
	{
		super(MulticastDIPMessageType.HELLO_WORLD_BROADCAST_REQUEST);
		this.hw = hw;
	}

	/*
	 * The constructor is used when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
//	public HelloWorldBroadcastRequest(String key, String collaboratorKey, HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	/*
	public HelloWorldBroadcastRequest(HashMap<String, IPAddress> childrenServerMap, HelloWorld hw)
	{
		super(MulticastMessageType.HELLO_WORLD_BROADCAST_REQUEST, childrenServerMap);
		this.hw = hw;
	}
	*/
	
	public HelloWorld getHelloWorld()
	{
		return this.hw;
	}
}
