package org.greatfree.dsf.old.multicast.message.root;

import java.util.HashMap;

import org.greatfree.dsf.multicast.HelloWorld;
import org.greatfree.dsf.multicast.message.OldShutdownChildrenBroadcastNotification;
import org.greatfree.message.RootBroadcastNotificationCreatable;
import org.greatfree.util.IPAddress;

// Created: 05/19/2017, Bing Li
public class ShutdownChildrenBroadcastNotificationCreator implements RootBroadcastNotificationCreatable<OldShutdownChildrenBroadcastNotification, HelloWorld>
{
	/*
	 * The constructor is used to create notifications when the scale of the cluster is even larger than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldShutdownChildrenBroadcastNotification createInstanceWithChildren(HashMap<String, IPAddress> childrenMap, HelloWorld message)
	{
//		return new ShutdownChildrenBroadcastNotification(Tools.generateUniqueKey(), childrenMap);
		return new OldShutdownChildrenBroadcastNotification(childrenMap);
	}

	/*
	 * The method is used to create notifications when the scale of the cluster is even smaller than the value of the root branch count. 06/14/2017, Bing Li
	 */
	@Override
	public OldShutdownChildrenBroadcastNotification createInstanceWithoutChildren(HelloWorld message)
	{
//		return new ShutdownChildrenBroadcastNotification(Tools.generateUniqueKey());
		return new OldShutdownChildrenBroadcastNotification();
	}

}
