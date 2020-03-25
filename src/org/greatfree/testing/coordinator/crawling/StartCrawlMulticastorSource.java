package org.greatfree.testing.coordinator.crawling;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootMulticastorSource;
import org.greatfree.testing.message.StartCrawlMultiNotification;
import org.greatfree.util.NullObject;

/*
 * The class provides the pool with the initial values to create a StartCrawlMulticastor. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMulticastorSource extends RootMulticastorSource<NullObject, StartCrawlMultiNotification, StartCrawlNotificationCreator>
{
	/*
	 * Initialize the source. 11/26/2014, Bing Li
	 */
	public StartCrawlMulticastorSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StartCrawlNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}
