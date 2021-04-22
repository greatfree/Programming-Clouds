package org.greatfree.framework.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.message.RootBroadcastRequestCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the request that is sent to distributed nodes anycastly in a cluster. 05/20/2017, Bing Li
 */

// Created: 05/21/2017, Bing Li
public class HelloWorldAnycastRequestCreator implements RootBroadcastRequestCreatable<OldHelloWorldAnycastRequest, HelloWorld>
{
	/*
	 * The constructor is used to create requests when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldAnycastRequest createInstanceWithChildren(String collaboratorKey, HashMap<String, IPAddress> childrenMap, HelloWorld t)
	public OldHelloWorldAnycastRequest createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld t)
	{
//		return new HelloWorldAnycastRequest(Tools.generateUniqueKey(), collaboratorKey, childrenMap, t);
		return new OldHelloWorldAnycastRequest(childrenMap, t);
	}

	/*
	 * The method is used to create request when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
//	public HelloWorldAnycastRequest createInstanceWithoutChildren(String collaboratorKey, HelloWorld t)
	public OldHelloWorldAnycastRequest createInstanceWithoutChildren(HelloWorld t)
	{
//		return new HelloWorldAnycastRequest(Tools.generateUniqueKey(), collaboratorKey, t);
		return new OldHelloWorldAnycastRequest(t);
	}

}
