package org.greatfree.testing.client;

import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * This is the disposer to dispose the instance of ClientServerDispatcher. It is usually executed when the client is shutdown. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerDispatcherDisposer implements RunnerDisposable<MessageProducer<ClientServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/07/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/07/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}
}
