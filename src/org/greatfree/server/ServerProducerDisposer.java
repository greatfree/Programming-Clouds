package org.greatfree.server;

import org.greatfree.concurrency.MessageProducer;
import org.greatfree.message.ServerMessage;
import org.greatfree.reuse.RunnerDisposable;

/*
 * The disposer is designed for ServerMessageProducerInstance rather than ServerMessageProducer. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class ServerProducerDisposer<Dispatcher extends ServerDispatcher<ServerMessage>> implements  RunnerDisposable<MessageProducer<Dispatcher>>
{

	@Override
	public void dispose(MessageProducer<Dispatcher> r) throws InterruptedException
	{
		r.dispose(0);
	}

	@Override
	public void dispose(MessageProducer<Dispatcher> r, long time) throws InterruptedException
	{
		r.dispose(time);
	}

}
