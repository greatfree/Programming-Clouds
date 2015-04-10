package com.greatfree.testing.crawlserver;

import com.greatfree.multicast.ChildMessageCreatorGettable;
import com.greatfree.multicast.ChildMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StartCrawlMultiNotification;

/*
 * The sources that are needed to create an instance of ChildMulticastor are enclosed in the class. That is required by the pool to create children multicastors. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlNotificationMulticastorSource extends ChildMulticastorSource<StartCrawlMultiNotification, StartCrawlMultiNotificationCreator> implements ChildMessageCreatorGettable<StartCrawlMultiNotification>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StartCrawlNotificationMulticastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, StartCrawlMultiNotificationCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}

	/*
	 * Expose the message creator. 11/27/2014, Bing Li
	 */
	@Override
	public StartCrawlMultiNotificationCreator getMessageCreator()
	{
		return super.getMessageCreator();
	}
}
