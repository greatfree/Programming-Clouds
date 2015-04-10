package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the memory server message producer of the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryServerProducerDisposer implements ThreadDisposable<MessageProducer<MemoryServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r, long time)
	{
		r.dispose();
	}
}
