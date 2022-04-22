package org.greatfree.server.nio;

import org.greatfree.concurrency.Runner;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/02/2022, Bing Li
 * 
 * The class replaced ServerMessageProducer since its kernel is SocketChannel instead of ObjectOutputStream. 02/02/2022, Bing Li
 *
 */
class ServerMessageProducer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	private Runner<MessageProducer<Dispatcher>> producerThreader;

	public void dispose(long timeout) throws InterruptedException
	{
		this.producerThreader.stop(timeout);
	}

	public void init(Dispatcher dispatcher)
	{
		this.producerThreader = new Runner<MessageProducer<Dispatcher>>(new MessageProducer<Dispatcher>(dispatcher));
		this.producerThreader.start();
	}

	public void produceMessage(MessageStream<ServerMessage> message)
	{
		this.producerThreader.getFunction().produce(message);
	}
}
