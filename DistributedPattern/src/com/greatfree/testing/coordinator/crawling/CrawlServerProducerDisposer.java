package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the crawling message producer of the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CrawlServerProducerDisposer implements ThreadDisposable<MessageProducer<CrawlServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlServerDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlServerDispatcher> r, long time)
	{
		r.dispose();
	}
}
