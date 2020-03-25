package org.greatfree.testing.crawlserver;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.StopCrawlMultiNotification;
import org.greatfree.util.Tools;

/*
 * The creator generates the notifications of StopCrawlMultiNotification that should be multicast to the local crawler's children. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMultiNotificationCreator implements ChildMulticastMessageCreatable<StopCrawlMultiNotification>
{
	@Override
	public StopCrawlMultiNotification createInstanceWithChildren(StopCrawlMultiNotification msg, HashMap<String, String> children)
	{
		return new StopCrawlMultiNotification(Tools.generateUniqueKey(), children);
	}
}
