package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.Consumable;
import org.greatfree.concurrency.ConsumerThread;
import org.greatfree.util.NullObject;

/*
 * This class is derived from ConsumerThread. It keeps running all the time immediately after the crawler is started to perform crawling following the manner of producer/consumer. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class CrawlConsumer extends ConsumerThread<NullObject, CrawlEater>
{
	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
	public CrawlConsumer(Consumable<NullObject> consumer, long waitTime)
	{
		super(consumer, waitTime);
	}
}
