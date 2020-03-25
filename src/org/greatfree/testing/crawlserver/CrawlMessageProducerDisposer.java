package org.greatfree.testing.crawlserver;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the message producer of the server. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlMessageProducerDisposer implements RunnerDisposable<MessageProducer<CrawlDispatcher>>
{
	/*
	 * Dispose the message producer. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<CrawlDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
