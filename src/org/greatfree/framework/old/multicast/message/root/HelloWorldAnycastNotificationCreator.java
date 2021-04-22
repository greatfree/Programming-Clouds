package org.greatfree.framework.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.framework.multicast.HelloWorld;
import org.greatfree.framework.multicast.message.OldHelloWorldAnycastNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

/*
 * The creator that generates the notification that is sent to distributed nodes anycastly in a cluster. 05/08/2017, Bing Li
 */

// Created: 05/19/2017, Bing Li
public class HelloWorldAnycastNotificationCreator implements RootBroadcastNotificationCreatable<OldHelloWorldAnycastNotification, HelloWorld>
{
	/*
	 * The constructor is used to create notifications when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldAnycastNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld message)
	{
//		return new HelloWorldAnycastNotification(Tools.generateUniqueKey(), childrenMap, message);
		return new OldHelloWorldAnycastNotification(childrenMap, message);
	}

	/*
	 * The method is used to create notifications when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldHelloWorldAnycastNotification createInstanceWithoutChildren(HelloWorld message)
	{
//		return new HelloWorldAnycastNotification(Tools.generateUniqueKey(), message);
		return new OldHelloWorldAnycastNotification(message);
	}

}
