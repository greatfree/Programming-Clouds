package org.greatfree.testing.crawlserver;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastor;
import org.greatfree.testing.message.StopCrawlMultiNotification;

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
