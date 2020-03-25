package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the DN server message producer of the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNServerProducerDisposer implements RunnerDisposable<MessageProducer<DNServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}

}
