package org.greatfree.testing.coordinator.admin;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootMulticastorSource;
import org.greatfree.testing.message.StopCrawlMultiNotification;
import org.greatfree.util.NullObject;

/*
 * The class provides the pool with the initial values to create a StopCrawlMulticastor. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMulticastorSource extends RootMulticastorSource<NullObject, StopCrawlMultiNotification, StopCrawlNotificationCreator>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StopCrawlMulticastorSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StopCrawlNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}
