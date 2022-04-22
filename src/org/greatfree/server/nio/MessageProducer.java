package org.greatfree.server.nio;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/02/2022, Bing Li
 *
 */
final class MessageProducer<Consumer extends ServerDispatcher<ServerMessage>> extends RunnerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.nio");
	
	private Consumer consumer;
	private Queue<MessageStream<ServerMessage>> queue;
	private Sync collaborator;
	
	public MessageProducer(Consumer consumer)
	{
		this.consumer = consumer;
		this.queue = new LinkedBlockingQueue<MessageStream<ServerMessage>>();
		this.collaborator = new Sync();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		this.collaborator.shutdown();
		this.queue.clear();
		this.consumer.dispose(timeout);
	}
	
	public synchronized void produce(MessageStream<ServerMessage> message)
	{
		log.info("message produced ...");
		this.queue.add(message);
		this.collaborator.signal();
	}

	@Override
	public void run()
	{
		MessageStream<ServerMessage> message;
		while (!this.collaborator.isShutdown())
		{
			while (!this.queue.isEmpty())
			{
				message = this.queue.poll();
				log.info("message received ...");
				if (!this.consumer.isDown())
				{
					// Process the message by the consumer, which is defined outside the class. 08/22/2014, Bing Li
					this.consumer.consume(message);
					this.consumer.process(message);
				}
			}
			// If the message queue is empty, the thread needs to wait until messages are received. 08/22/2014, Bing Li
			this.collaborator.holdOn(ServerConfig.CHECK_MESSAGE_TIMEOUT);
		}
	}

	@Override
	public int getWorkload()
	{
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
	}

}
