package com.greatfree.testing.coordinator.crawling;

import com.greatfree.multicast.RootMessageCreatorGettable;
import com.greatfree.multicast.RootMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StartCrawlMultiNotification;
import com.greatfree.util.NullObject;

/*
 * The class provides the pool with the initial values to create a StartCrawlMulticastor. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMulticastorSource extends RootMulticastorSource<NullObject, StartCrawlMultiNotification, StartCrawlNotificationCreator> implements RootMessageCreatorGettable<StartCrawlMultiNotification, NullObject>
{
	/*
	 * Initialize the source. 11/26/2014, Bing Li
	 */
	public StartCrawlMulticastorSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StartCrawlNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}

	/*
	 * Expose the message creator. 11/26/2014, Bing Li
	 */
	@Override
	public StartCrawlNotificationCreator getMessageCreator()
	{
		return super.getCreator();
	}
}
