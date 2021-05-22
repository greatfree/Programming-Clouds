package org.greatfree.server;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.MessageProducer;
import org.greatfree.concurrency.Runner;
import org.greatfree.message.ServerMessage;

/*
 * This is an instantiating-enabled ServerMessageProducer. For application developers, they just need to work on it. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class ServerMessageProducer<Dispatcher extends ServerDispatcher<ServerMessage>>
{
	// The Threader aims to associate with the message producer to guarantee the producer can work concurrently. 09/20/2014, Bing Li
//	private Runner<MessageProducer<Dispatcher>, ServerProducerDisposer<Dispatcher>> producerThreader;
//	private Runner<MessageProducer<Dispatcher>, ServerProducerDisposer<Dispatcher>> producerThreader;
	private Runner<MessageProducer<Dispatcher>> producerThreader;

	/*
	 * Dispose the producers when the process of the server is shutdown. 08/22/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException
	{
		this.producerThreader.stop(timeout);
	}
	
	/*
	 * Initialize the message producers. It is invoked when the connection modules of the server is started since clients can send requests or notifications only after it is started. 08/22/2014, Bing Li
	 */
//	public void init(Dispatcher dispatcher, long timeout)
	public void init(Dispatcher dispatcher)
	{
		// Initialize the message producer. A threader is associated with the message producer such that the producer is able to work in a concurrent way. 09/20/2014, Bing Li
//		this.producerThreader = new Runner<MessageProducer<Dispatcher>, ServerProducerDisposer<Dispatcher>>(new MessageProducer<Dispatcher>(dispatcher), new ServerProducerDisposer<Dispatcher>());
//		this.producerThreader = new Runner<MessageProducer<Dispatcher>>(new MessageProducer<Dispatcher>(dispatcher, timeout));
//		System.out.println("ServerMessageProducer-init(): dispatcher hashCode = " + dispatcher.hashCode());
		this.producerThreader = new Runner<MessageProducer<Dispatcher>>(new MessageProducer<Dispatcher>(dispatcher));
		// Start the associated thread for the message producer. 09/20/2014, Bing Li
		this.producerThreader.start();
	}
	
	/*
	 * Assign messages, requests or notifications, to the bound message dispatcher such that they can be responded or dealt with. 09/20/2014, Bing Li
	 */
	public void produceMessage(MessageStream<ServerMessage> message)
	{
		this.producerThreader.getFunction().produce(message);
	}
}
