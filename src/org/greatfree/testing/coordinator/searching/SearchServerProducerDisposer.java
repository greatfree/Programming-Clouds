package org.greatfree.testing.coordinator.searching;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the searchers' message producer of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchServerProducerDisposer implements RunnerDisposable<MessageProducer<SearchServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
