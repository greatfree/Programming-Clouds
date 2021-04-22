package org.greatfree.framework.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.OldHelloWorldUnicastNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the notification that is sent to distributed nodes unicastly in a cluster. 05/19/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class HelloWorldUnicastNotificationCreator implements RootBroadcastNotificationCreatable<OldHelloWorldUnicastNotification, HelloWorld>
{
	/*
	 * The constructor is used to create notifications when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldUnicastNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld message)
	{
//		return new HelloWorldUnicastNotification(Tools.generateUniqueKey(), childrenMap, message);
		return new OldHelloWorldUnicastNotification(childrenMap, message);
	}

	/*
	 * The method is used to create notifications when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldUnicastNotification createInstanceWithoutChildren(HelloWorld message)
	{
//		return new HelloWorldUnicastNotification(Tools.generateUniqueKey(), message);
		return new OldHelloWorldUnicastNotification(message);
	}

}
