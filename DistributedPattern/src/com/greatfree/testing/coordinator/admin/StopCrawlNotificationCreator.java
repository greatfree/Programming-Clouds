package com.greatfree.testing.coordinator.admin;

import java.util.HashMap;

import com.greatfree.multicast.ObjectMulticastCreatable;
import com.greatfree.testing.message.StopCrawlMultiNotification;
import com.greatfree.util.NullObject;
import com.greatfree.util.Tools;

/*
 * The creator is used to create the instance of StopCrawlServerMultiNotification. It works with a multicastor to do that. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlNotificationCreator implements ObjectMulticastCreatable<StopCrawlMultiNotification, NullObject>
{
	/*
	 * Create an instance of StopCrawlServerMultiNotification. 11/27/2014, Bing Li
	 * 
	 * For the specific notification, StopCrawlServerMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 * 
	 * The constructor needs to input the children nodes for further multicast.
	 */
	@Override
	public StopCrawlMultiNotification createInstanceWithChildren(NullObject message, HashMap<String, String> childrenMap)
	{
		return new StopCrawlMultiNotification(Tools.generateUniqueKey(), childrenMap);
	}

	/*
	 * Create an instance of StartCrawlMultiNotification. 11/27/2014, Bing Li
	 * 
	 * For the specific notification, StartCrawlMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 */
	@Override
	public StopCrawlMultiNotification createInstanceWithoutChildren(NullObject message)
	{
		return new StopCrawlMultiNotification(Tools.generateUniqueKey());
	}
}
