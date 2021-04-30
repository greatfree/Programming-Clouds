package edu.chainnet.crawler.child.crawl;

import org.greatfree.concurrency.Consumable;
import org.greatfree.concurrency.ConsumerThread;
import org.greatfree.util.NullObject;

// Created: 02/20/2014, Bing Li
class HubCrawlConsumer extends ConsumerThread<NullObject, CrawlEater>
{
	public HubCrawlConsumer(Consumable<NullObject> consumer, long waitTime)
	{
		super(consumer, waitTime);
	}
}

