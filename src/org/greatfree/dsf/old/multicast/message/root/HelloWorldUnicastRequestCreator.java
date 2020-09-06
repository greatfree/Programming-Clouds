package org.greatfree.dsf.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.dsf.multicast.message.OldHelloWorldUnicastRequest;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the request that is sent to distributed nodes unicastly in a cluster. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
public class HelloWorldUnicastRequestCreator implements RootBroadcastRequestCreatable<OldHelloWorldUnicastRequest, HelloWorld>
{
	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldUnicastRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, HelloWorld t)
	public OldHelloWorldUnicastRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld t)
	{
//		return new HelloWorldUnicastRequest(Tools.generateUniqueKey(), collaboratorKey, childrenMap, t);
		return new OldHelloWorldUnicastRequest(childrenMap, t);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldUnicastRequest createInstanceWithoutChildren(String collaboratorKey, HelloWorld t)
	public OldHelloWorldUnicastRequest createInstanceWithoutChildren(HelloWorld t)
	{
//		return new HelloWorldUnicastRequest(Tools.generateUniqueKey(), collaboratorKey, t);
		return new OldHelloWorldUnicastRequest(t);
	}

}
