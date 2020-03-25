package org.greatfree.testing.cluster.coordinator.dn;

import java.util.HashMap;

import org.greatfree.multicast.ObjectMulticastCreatable;
import org.greatfree.testing.message.AnycastNotification;
import org.greatfree.util.Tools;

/*
 * The class provides the pool with the initial values to create an AnycastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create anycastors. 11/25/2016, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotificationCreator implements ObjectMulticastCreatable<AnycastNotification, String>
{
	/*
	 * Create an instance of AnycastNotification. 11/26/2014, Bing Li
	 * 
	 * The constructor needs to input the children nodes for further multicasting.
	 */

	@Override
	public AnycastNotification createInstanceWithChildren(String message, HashMap<String, String> childrenMap)
	{
		return new AnycastNotification(Tools.generateUniqueKey(), childrenMap, message);
	}

	/*
	 * Create an instance of AnycastNotification. 11/26/2014, Bing Li
	 */
	@Override
	public AnycastNotification createInstanceWithoutChildren(String message)
	{
		return new AnycastNotification(Tools.generateUniqueKey(), message);
	}

}
