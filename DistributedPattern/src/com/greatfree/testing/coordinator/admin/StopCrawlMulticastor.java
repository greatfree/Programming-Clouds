package com.greatfree.testing.coordinator.admin;

import com.greatfree.multicast.RootObjectMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopCrawlMultiNotification;
import com.greatfree.util.NullObject;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of StopCrawlServerMultiNotification to all of the crawlers. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMulticastor extends RootObjectMulticastor<NullObject, StopCrawlMultiNotification, StopCrawlNotificationCreator>
{
	public StopCrawlMulticastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StopCrawlNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}
}
