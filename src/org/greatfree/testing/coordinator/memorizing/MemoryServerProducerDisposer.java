package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the memory server message producer of the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryServerProducerDisposer implements RunnerDisposable<MessageProducer<MemoryServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
