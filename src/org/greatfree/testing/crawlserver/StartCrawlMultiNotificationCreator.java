package org.greatfree.testing.crawlserver;

import java.util.HashMap;

import org.greatfree.multicast.ChildMulticastMessageCreatable;
import org.greatfree.testing.message.StartCrawlMultiNotification;
import org.greatfree.util.Tools;

/*
 * The creator generates the notifications of StartCrawlMultiNotification that should be multicast to the local crawler's children. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlMultiNotificationCreator implements ChildMulticastMessageCreatable<StartCrawlMultiNotification>
{
	@Override
	public StartCrawlMultiNotification createInstanceWithChildren(StartCrawlMultiNotification msg, HashMap<String, String> children)
	{
		return new StartCrawlMultiNotification(Tools.generateUniqueKey(), children);
	}
}
