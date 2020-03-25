package org.greatfree.testing.coordinator.crawling;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the crawling message producer of the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CrawlServerProducerDisposer implements RunnerDisposable<MessageProducer<CrawlServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
