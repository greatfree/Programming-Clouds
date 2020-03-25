package org.greatfree.testing.coordinator.crawling;

import org.greatfree.concurrency.Consumable;
import org.greatfree.concurrency.ConsumerThread;
import org.greatfree.data.ServerConfig;

/*
 * This is the class that works in the way of producer/consumer to distribute crawling loads, URLs, to all registered crawlers. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlLoadDistributer extends ConsumerThread<CrawlLoad, CrawlLoadSender>
{
	/*
	 * Initialize the distributer, which extends the class, ConsumerThread. 11/25/2014, Bing Li
	 */
	public CrawlLoadDistributer(Consumable<CrawlLoad> consumer)
	{
		super(consumer, ServerConfig.DISTRIBUTE_DATA_WAIT_TIME);
	}
}
