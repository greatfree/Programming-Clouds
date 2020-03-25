package org.greatfree.testing.coordinator.crawling;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootObjectMulticastor;
import org.greatfree.testing.message.StartCrawlMultiNotification;
import org.greatfree.util.NullObject;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of StartCrawlMultiNotification to all of the crawlers. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMulticastor extends RootObjectMulticastor<NullObject, StartCrawlMultiNotification, StartCrawlNotificationCreator>
{
	/*
	 * Initialize the multicastor. 11/26/2014, Bing Li
	 */
	public StartCrawlMulticastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StartCrawlNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}
}
