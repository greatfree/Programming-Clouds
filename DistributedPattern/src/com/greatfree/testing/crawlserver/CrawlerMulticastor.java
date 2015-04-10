package com.greatfree.testing.crawlserver;

import java.io.IOException;

import com.greatfree.reuse.ResourcePool;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.StartCrawlMultiNotification;
import com.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * This is the multicasting mechanism to manage all of the multicastors to transfer notifications to its children crawlers. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class CrawlerMulticastor
{
	// The pool for the children multicastor which multicasts the notification of StartCrawlMultiNotification. 11/27/2014, Bing Li
	private ResourcePool<StartCrawlNotificationMulticastorSource, StartCrawlNotificationMulticastor, StartCrawlNotificationMulticastorCreator, StartCrawlNotificationMulticastorDisposer> startCrawlMulticastorPool;
	// The pool for the children multicastor which multicasts the notification of StopCrawlMultiNotification. 11/27/2014, Bing Li
	private ResourcePool<StopCrawlNotificationMulticastorSource, StopCrawlNotificationMulticastor, StopCrawlNotificationMulticastorCreator, StopCrawlNotificationMulticastorDisposer> stopCrawlMulticastorPool;

	private CrawlerMulticastor()
	{
	}
	
	/*
	 * A singleton implementation. 11/27/2014, Bing Li
	 */
	private static CrawlerMulticastor instance = new CrawlerMulticastor();
	
	public static CrawlerMulticastor CRAWLER()
	{
		if (instance == null)
		{
			instance = new CrawlerMulticastor();
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
		this.startCrawlMulticastorPool.shutdown();
		this.stopCrawlMulticastorPool.shutdown();
	}
	
	/*
	 * Initialize the pools. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.startCrawlMulticastorPool = new ResourcePool<StartCrawlNotificationMulticastorSource, StartCrawlNotificationMulticastor, StartCrawlNotificationMulticastorCreator, StartCrawlNotificationMulticastorDisposer>(ClientConfig.MULTICASTOR_POOL_SIZE, new StartCrawlNotificationMulticastorCreator(), new StartCrawlNotificationMulticastorDisposer(), ClientConfig.MULTICASTOR_WAIT_TIME);
		this.stopCrawlMulticastorPool = new ResourcePool<StopCrawlNotificationMulticastorSource, StopCrawlNotificationMulticastor, StopCrawlNotificationMulticastorCreator, StopCrawlNotificationMulticastorDisposer>(ClientConfig.MULTICASTOR_POOL_SIZE, new StopCrawlNotificationMulticastorCreator(), new StopCrawlNotificationMulticastorDisposer(), ClientConfig.MULTICASTOR_WAIT_TIME);
	}
	
	/*
	 * Disseminate the notification of StartCrawlMultiNotification to the children crawlers of the local one. 11/27/2014, Bing Li
	 */
	public void disseminateStartCrawlNotificationAmongSubCrawlServers(StartCrawlMultiNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StartCrawlNotificationMulticastor from the pool. 11/27/2014, Bing Li
		StartCrawlNotificationMulticastor notifier = this.startCrawlMulticastorPool.get(new StartCrawlNotificationMulticastorSource(SubClientPool.CRAWL().getPool(), ClientConfig.MULTICAST_BRANCH_COUNT, ServerConfig.CRAWL_SERVER_PORT, new StartCrawlMultiNotificationCreator()));
		// Check whether the notifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the notification. 11/27/2014, Bing Li
			notifier.disseminate(notification);
			// Collect the instance of StartCrawlNotificationMulticastor. 11/27/2014, Bing Li
			this.startCrawlMulticastorPool.collect(notifier);
		}
	}
	
	/*
	 * Disseminate the notification of StartCrawlMultiNotification to the children crawlers of the local one. 11/27/2014, Bing Li
	 */
	public void disseminateStopCrawlNotificationAmongSubCrawlServers(StopCrawlMultiNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StartCrawlNotificationMulticastor from the pool. 11/27/2014, Bing Li
		StopCrawlNotificationMulticastor notifier = this.stopCrawlMulticastorPool.get(new StopCrawlNotificationMulticastorSource(SubClientPool.CRAWL().getPool(), ClientConfig.MULTICAST_BRANCH_COUNT, ServerConfig.CRAWL_SERVER_PORT, new StopCrawlMultiNotificationCreator()));
		// Check whether the notifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the notification. 11/27/2014, Bing Li
			notifier.disseminate(notification);
			// Collect the instance of Stop`CrawlNotificationMulticastor. 11/27/2014, Bing Li
			this.stopCrawlMulticastorPool.collect(notifier);
		}
	}
}
