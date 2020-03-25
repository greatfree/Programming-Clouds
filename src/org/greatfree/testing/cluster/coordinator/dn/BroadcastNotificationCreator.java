package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.ObjectMulticastCreatable;
import org.greatfree.testing.message.BroadcastNotification;
import org.greatfree.util.Tools;

/*
 * The class provides the pool with the initial values to create a BroadcastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotificationCreator implements ObjectMulticastCreatable<BroadcastNotification, String>
{
	/*
	 * Create an instance of BroadcastNotification. 11/26/2014, Bing Li
	 * 
	 * The constructor needs to input the children nodes for further multicasting.
	 */
	@Override
	public BroadcastNotification createInstanceWithChildren(String message, HashMap<String, String> childrenMap)
	{
		return new BroadcastNotification(Tools.generateUniqueKey(), childrenMap, message);
	}

	/*
	 * Create an instance of BroadcastNotification. 11/26/2014, Bing Li
	 */
	@Override
	public BroadcastNotification createInstanceWithoutChildren(String message)
	{
		return new BroadcastNotification(Tools.generateUniqueKey(), message);
	}
}
