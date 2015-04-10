package com.greatfree.testing.coordinator.admin;

import java.io.IOException;

import com.greatfree.reuse.ResourcePool;
import com.greatfree.testing.coordinator.crawling.CrawlServerClientPool;
import com.greatfree.testing.data.ServerConfig;

/*
 * This is a singleton that contains all of the multicastor pools to administer the system. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminMulticastor
{
	// The pool for the multicastor which multicasts the notification of StopCrawlMultiNotification. 11/27/2014, Bing Li
	private ResourcePool<StopCrawlMulticastorSource, StopCrawlMulticastor, StopCrawlMulticastorCreator, StopCrawlMulticastorDisposer> stopCrawlMulticastorPool;

	private AdminMulticastor()
	{
	}

	/*
	 * A singleton implementation. 11/27/2014, Bing Li
	 */
	private static AdminMulticastor instance = new AdminMulticastor();
	
	public static AdminMulticastor ADMIN()
	{
		if (instance == null)
		{
			instance = new AdminMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose all of the pools. 11/27/2014, Bing Li
	 */
	public void dispose()
	{
		this.stopCrawlMulticastorPool.shutdown();
	}

	/*
	 * Initialize the pools. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.stopCrawlMulticastorPool = new ResourcePool<StopCrawlMulticastorSource, StopCrawlMulticastor, StopCrawlMulticastorCreator, StopCrawlMulticastorDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new StopCrawlMulticastorCreator(), new StopCrawlMulticastorDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
	}

	/*
	 * Disseminate the notification of StartCrawlMultiNotification to all of the crawlers. 11/27/2014, Bing Li
	 */
	public void disseminateStopCrawl() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StartCrawlMulticastor from the pool. 11/27/2014, Bing Li
		StopCrawlMulticastor multicastor = this.stopCrawlMulticastorPool.get(new StopCrawlMulticastorSource(CrawlServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new StopCrawlNotificationCreator()));
		// Check whether the multicastor is valid. 11/27/2014, Bing Li
		if (multicastor != null)
		{
			// Disseminate the notification. The notification contains no data. Thus, it is not necessary to put any arguments here. Just place a null. 11/27/2014, Bing Li
			multicastor.disseminate(null);
			// Collect the instance of StartCrawlMulticastor. 11/27/2014, Bing Li
			this.stopCrawlMulticastorPool.collect(multicastor);
		}
	}
}
