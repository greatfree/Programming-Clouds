package org.greatfree.dsf.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.dsf.multicast.message.OldHelloWorldBroadcastNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the notification that is sent to distributed nodes broadcastly in a cluster. 05/08/2017, Bing Li
 */

// Created: 05/08/2017, Bing Li
public class HelloWorldBroadcastNotificationCreator implements RootBroadcastNotificationCreatable<OldHelloWorldBroadcastNotification, HelloWorld>
{
	/*
	 * The constructor is used to create notifications when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld message)
	{
//		return new HelloWorldBroadcastNotification(Tools.generateUniqueKey(), childrenMap, message);
		return new OldHelloWorldBroadcastNotification(childrenMap, message);
	}

	/*
	 * The method is used to create notifications when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldBroadcastNotification createInstanceWithoutChildren(HelloWorld message)
	{
//		return new HelloWorldBroadcastNotification(Tools.generateUniqueKey(), message);
		return new OldHelloWorldBroadcastNotification(message);
	}
}
