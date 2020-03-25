package org.greatfree.testing.coordinator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.reuse.ResourcePool;
import org.greatfree.testing.coordinator.crawling.CrawlServerClientPool;
import org.greatfree.testing.coordinator.crawling.StartCrawlMulticastor;
import org.greatfree.testing.coordinator.crawling.StartCrawlMulticastorCreator;
import org.greatfree.testing.coordinator.crawling.StartCrawlMulticastorDisposer;
import org.greatfree.testing.coordinator.crawling.StartCrawlMulticastorSource;
import org.greatfree.testing.coordinator.crawling.StartCrawlNotificationCreator;

/*
 * This is a singleton that contains all of the multicastor pools. Those multicastors are critical to compose a cluster for all of the crawlers. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class CoordinatorMulticastor
{
	// The pool for the multicastor which multicasts the notification of StartCrawlMultiNotification. 11/26/2014, Bing Li
	private ResourcePool<StartCrawlMulticastorSource, StartCrawlMulticastor, StartCrawlMulticastorCreator, StartCrawlMulticastorDisposer> startCrawlMulticastorPool;

	private CoordinatorMulticastor()
	{
	}

	/*
	 * A singleton implementation. 11/26/2014, Bing Li
	 */
	private static CoordinatorMulticastor instance = new CoordinatorMulticastor();
	
	public static CoordinatorMulticastor COORDINATE()
	{
		if (instance == null)
		{
			instance = new CoordinatorMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose all of the pools. 11/26/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.startCrawlMulticastorPool.shutdown();
	}

	/*
	 * Initialize the pools. 11/26/2014, Bing Li
	 */
	public void init()
	{
		this.startCrawlMulticastorPool = new ResourcePool<StartCrawlMulticastorSource, StartCrawlMulticastor, StartCrawlMulticastorCreator, StartCrawlMulticastorDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new StartCrawlMulticastorCreator(), new StartCrawlMulticastorDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
	}

	/*
	 * Disseminate the notification of StartCrawlMultiNotification to all of the crawlers. 11/26/2014, Bing Li
	 */
	public void disseminateStartCrawl() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StartCrawlMulticastor from the pool. 11/26/2014, Bing Li
		StartCrawlMulticastor multicastor = this.startCrawlMulticastorPool.get(new StartCrawlMulticastorSource(CrawlServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new StartCrawlNotificationCreator()));
		// Check whether the multicastor is valid. 11/26/2014, Bing Li
		if (multicastor != null)
		{
			// Disseminate the notification. The notification contains no data. Thus, it is not necessary to put any arguments here. Just place a null. 11/26/2014, Bing Li
			multicastor.disseminate(null);
			// Collect the instance of StartCrawlMulticastor. 11/26/2014, Bing Li
			this.startCrawlMulticastorPool.collect(multicastor);
		}
	}
}
