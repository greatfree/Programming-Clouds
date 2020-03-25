package org.greatfree.testing.coordinator.admin;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the administration message producer of the coordinator. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminServerProducerDisposer implements RunnerDisposable<MessageProducer<AdminServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
