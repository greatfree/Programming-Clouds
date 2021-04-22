package org.greatfree.framework.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.OldHelloWorldBroadcastRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the request that is sent to distributed nodes broadcastly in a cluster. 05/20/2017, Bing Li
 */

// Created: 05/20/2017, Bing Li
public class HelloWorldBroadcastRequestCreator implements RootBroadcastRequestCreatable<OldHelloWorldBroadcastRequest, HelloWorld>
{
	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldBroadcastRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, HelloWorld t)
	public OldHelloWorldBroadcastRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld t)
	{
		return new OldHelloWorldBroadcastRequest(childrenMap, t);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldBroadcastRequest createInstanceWithoutChildren(String collaboratorKey, HelloWorld t)
	public OldHelloWorldBroadcastRequest createInstanceWithoutChildren(HelloWorld t)
	{
		return new OldHelloWorldBroadcastRequest(t);
	}

}
