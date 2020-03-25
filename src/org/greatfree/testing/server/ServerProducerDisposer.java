package org.greatfree.testing.server;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the message producer of the server. 09/20/2014, Bing Li
 */

// Created: 09/20/2014, Bing Li
public class ServerProducerDisposer implements RunnerDisposable<MessageProducer<MyServerDispatcher>>
{
	/*
	 * Dispose the message producer. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MyServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 09/20/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MyServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
