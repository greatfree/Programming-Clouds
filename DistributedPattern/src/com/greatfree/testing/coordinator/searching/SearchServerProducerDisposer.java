package com.greatfree.testing.coordinator.searching;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the searchers' message producer of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchServerProducerDisposer implements ThreadDisposable<MessageProducer<SearchServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r, long time)
	{
		r.dispose();
	}
}
