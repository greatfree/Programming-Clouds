package org.greatfree.testing.cluster.coordinator.client;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is responsible for disposing the client message producer of the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class ClientServerProducerDisposer implements RunnerDisposable<MessageProducer<ClientServerDispatcher>>
{

	/*
	 * Dispose the message producer. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}

}
