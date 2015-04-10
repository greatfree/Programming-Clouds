package com.greatfree.testing.coordinator.admin;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.ThreadDisposable;

/*
 * The class is responsible for disposing the administration message producer of the coordinator. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminServerProducerDisposer implements ThreadDisposable<MessageProducer<AdminServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r)
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r, long time)
	{
		r.dispose();
	}
}
