package com.greatfree.testing.server;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the message producer of the server. 09/20/2014, Bing Li
 */

// Created: 09/20/2014, Bing Li
public class MyServerProducerDisposer implements ThreadDisposable<MessageProducer<MyServerDispatcher>>
{
	/*
	 * Dispose the message producer. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MyServerDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MyServerDispatcher> r, long time)
	{
		r.dispose();
	}
}
