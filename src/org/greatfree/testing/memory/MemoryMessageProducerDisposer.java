package org.greatfree.testing.memory;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the message producer of the server. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryMessageProducerDisposer implements RunnerDisposable<MessageProducer<MemoryDispatcher>>
{
	/*
	 * Dispose the message producer. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
