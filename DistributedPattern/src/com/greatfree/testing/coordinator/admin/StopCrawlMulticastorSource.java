package com.greatfree.testing.coordinator.admin;

import com.greatfree.multicast.RootMessageCreatorGettable;
import com.greatfree.multicast.RootMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopCrawlMultiNotification;
import com.greatfree.util.NullObject;

/*
 * The class provides the pool with the initial values to create a StopCrawlMulticastor. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMulticastorSource extends RootMulticastorSource<NullObject, StopCrawlMultiNotification, StopCrawlNotificationCreator> implements RootMessageCreatorGettable<StopCrawlMultiNotification, NullObject>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StopCrawlMulticastorSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StopCrawlNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}

	/*
	 * Expose the message creator. 11/26/2014, Bing Li
	 */
	@Override
	public StopCrawlNotificationCreator getMessageCreator()
	{
		return super.getCreator();
	}
}
