package org.greatfree.testing.server;

import org.greatfree.concurrency.Runner;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client that connects to a respective port on the server. Usually, each port aims to provide one particular service. 09/20/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 08/22/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
class ServerMessageProducer
{
	// The Threader aims to associate with the message producer to guarantee the producer can work concurrently. 09/20/2014, Bing Li
//	private Runner<MessageProducer<MyServerDispatcher>, ServerProducerDisposer> producerThreader;
	private Runner<MessageProducer<MyServerDispatcher>> producerThreader;
	
	private ServerMessageProducer()
	{
	}

	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 08/22/2014, Bing Li
	 */
	private static ServerMessageProducer instance = new ServerMessageProducer();
	
	public static ServerMessageProducer SERVER()
	{
		if (instance == null)
		{
			instance = new ServerMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the server is shutdown. 08/22/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.producerThreader.stop();
	}

	/*
	 * Initialize the message producers. It is invoked when the connection modules of the server is started since clients can send requests or notifications only after it is started. 08/22/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the message producer. A threader is associated with the message producer such that the producer is able to work in a concurrent way. 09/20/2014, Bing Li
//		this.producerThreader = new Runner<MessageProducer<MyServerDispatcher>, ServerProducerDisposer>(new MessageProducer<MyServerDispatcher>(new MyServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new ServerProducerDisposer());
		this.producerThreader = new Runner<MessageProducer<MyServerDispatcher>>(new MessageProducer<MyServerDispatcher>(new MyServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
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
