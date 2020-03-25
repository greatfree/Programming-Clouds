package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.ObjectMulticastCreatable;
import org.greatfree.testing.message.UnicastNotification;
import org.greatfree.util.Tools;

/*
 * The class provides the pool with the initial values to create a UnicastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create unicastors. 11/25/2016, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotificationCreator implements ObjectMulticastCreatable<UnicastNotification, String>
{
	/*
	 * Create an instance of UnicastNotification. 11/26/2014, Bing Li
	 * 
	 * The constructor needs to input the children nodes for further multicasting.
	 */

	@Override
	public UnicastNotification createInstanceWithChildren(String message, HashMap<String, String> childrenMap)
	{
		return new UnicastNotification(Tools.generateUniqueKey(), childrenMap, message);
	}

	/*
	 * Create an instance of UnicastNotification. 11/26/2014, Bing Li
	 */
	@Override
	public UnicastNotification createInstanceWithoutChildren(String message)
	{
		return new UnicastNotification(Tools.generateUniqueKey(), message);
	}

}
