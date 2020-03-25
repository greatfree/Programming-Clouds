package org.greatfree.testing.crawlserver;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastorSource;
import org.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The sources that are needed to create an instance of ChildMulticastor are enclosed in the class. That is required by the pool to create children multicastors. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlNotificationMulticastorSource extends ChildMulticastorSource<StartCrawlMultiNotification, StartCrawlMultiNotificationCreator>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StartCrawlNotificationMulticastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, StartCrawlMultiNotificationCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}
}
