package com.greatfree.testing.coordinator.crawling;

import com.greatfree.multicast.RootObjectMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StartCrawlMultiNotification;
import com.greatfree.util.NullObject;

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
