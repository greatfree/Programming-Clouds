package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the message producer of the server. 11/23/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNMessageProducerDisposer implements RunnerDisposable<MessageProducer<DNDispatcher>>
{
	/*
	 * Dispose the message producer. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}

}
