package org.greatfree.testing.cluster.coordinator.admin;

import java.util.HashMap;

import org.greatfree.multicast.ObjectMulticastCreatable;
import org.greatfree.testing.message.StopDNMultiNotification;
import org.greatfree.util.NullObject;
import org.greatfree.util.Tools;

/*
 * The creator is used to create the instance of StopDNMultiNotification. It works with a multicastor to do that. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNNotificationCreator implements ObjectMulticastCreatable<StopDNMultiNotification, NullObject>
{
	/*
	 * Create an instance of StopDNMultiNotification. 11/27/2014, Bing Li
	 * 
	 * For the specific notification, StopDNMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 * 
	 * The constructor needs to input the children nodes for further multicast.
	 */
	@Override
	public StopDNMultiNotification createInstanceWithChildren(NullObject message, HashMap<String, String> childrenMap)
	{
		return new StopDNMultiNotification(Tools.generateUniqueKey(), childrenMap);
	}

	/*
	 * Create an instance of StopDNMultiNotification. 11/27/2014, Bing Li
	 * 
	 * For the specific notification, StopDNMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 */
	@Override
	public StopDNMultiNotification createInstanceWithoutChildren(NullObject message)
	{
		return new StopDNMultiNotification(Tools.generateUniqueKey());
	}

}
