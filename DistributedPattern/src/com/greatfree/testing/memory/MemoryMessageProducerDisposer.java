package com.greatfree.testing.memory;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the message producer of the server. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryMessageProducerDisposer implements ThreadDisposable<MessageProducer<MemoryDispatcher>>
{
	/*
	 * Dispose the message producer. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryDispatcher> r, long time)
	{
		r.dispose();
	}
}
