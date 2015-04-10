package com.greatfree.testing.coordinator.crawling;

import java.util.HashMap;

import com.greatfree.multicast.ObjectMulticastCreatable;
import com.greatfree.testing.message.StartCrawlMultiNotification;
import com.greatfree.util.NullObject;
import com.greatfree.util.Tools;

/*
 * The creator is used to create the instance of StartCrawlMultiNotification. It works with a multicastor to do that. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlNotificationCreator implements ObjectMulticastCreatable<StartCrawlMultiNotification, NullObject>
{
	/*
	 * Create an instance of StartCrawlMultiNotification. 11/26/2014, Bing Li
	 * 
	 * For the specific notification, StartCrawlMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 * 
	 * The constructor needs to input the children nodes for further multicast.
	 */
	@Override
	public StartCrawlMultiNotification createInstanceWithChildren(NullObject message, HashMap<String, String> childrenMap)
	{
		return new StartCrawlMultiNotification(Tools.generateUniqueKey(), childrenMap);
	}

	/*
	 * Create an instance of StartCrawlMultiNotification. 11/26/2014, Bing Li
	 * 
	 * For the specific notification, StartCrawlMultiNotification, no arguments are needed to send. Therefore, the NullObject is put here. For other notification, an object that contains all of the arguments must be enclosed in the object.
	 */
	@Override
	public StartCrawlMultiNotification createInstanceWithoutChildren(NullObject message)
	{
		return new StartCrawlMultiNotification(Tools.generateUniqueKey());
	}
}
