package com.greatfree.testing.crawlserver;

import com.greatfree.multicast.ChildMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * The multicastor is derived from ChildMulticastor to transfer the notification, StopCrawlMultiNotification, to the children nodes. 11/27/2014, Bing Li
 */

// Created; 11/27/2014, Bing Li
public class StopCrawlNotificationMulticastor extends ChildMulticastor<StopCrawlMultiNotification, StopCrawlMultiNotificationCreator>
{
	public StopCrawlNotificationMulticastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, StopCrawlMultiNotificationCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}
}
