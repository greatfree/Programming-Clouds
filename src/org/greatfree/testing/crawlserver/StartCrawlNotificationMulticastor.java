package org.greatfree.testing.crawlserver;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastor;
import org.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The multicastor is derived from ChildMulticastor to transfer the notification, StartCrawlMultiNotification, to the children nodes. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlNotificationMulticastor extends ChildMulticastor<StartCrawlMultiNotification, StartCrawlMultiNotificationCreator>
{
	public StartCrawlNotificationMulticastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, StartCrawlMultiNotificationCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}
}
