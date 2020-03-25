package org.greatfree.testing.memory;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.Runner;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.abandoned.MessageProducer;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client that connects to a respective port on the memory server. Usually, each port aims to provide one particular service. 11/27/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryMessageProducer
{
//	private Runner<MessageProducer<MemoryDispatcher>, MemoryMessageProducerDisposer> producerThreader;
	private Runner<MessageProducer<MemoryDispatcher>> producerThreader;
	
	private MemoryMessageProducer()
	{
	}
	
	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 11/27/2014, Bing Li
	 */
	private static MemoryMessageProducer instance = new MemoryMessageProducer();
	
	public static MemoryMessageProducer STORE()
	{
		if (instance == null)
		{
			instance = new MemoryMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the server is shutdown. 11/27/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.producerThreader.stop();
	}

	/*
	 * Initialize the message producers. It is invoked when the connection modules of the server is started since clients can send requests or notifications only after it is started. 11/27/2014, Bing Li
	 */
	public void Init()
	{
		// Initialize the memory message producer. A threader is associated with the message producer such that the producer is able to work in a concurrent way. 11/27/2014, Bing Li
//		this.producerThreader = new Runner<MessageProducer<MemoryDispatcher>, MemoryMessageProducerDisposer>(new MessageProducer<MemoryDispatcher>(new MemoryDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new MemoryMessageProducerDisposer());
		this.producerThreader = new Runner<MessageProducer<MemoryDispatcher>>(new MessageProducer<MemoryDispatcher>(new MemoryDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the crawling message producer. 11/27/2014, Bing Li
		this.producerThreader.start();
	}
	
	/*
	 * Assign messages, requests or notifications, to the bound crawling message dispatcher such that they can be responded or dealt with. 11/27/2014, Bing Li
	 */
	public void produceMessage(OutMessageStream<ServerMessage> message)
	{
		this.producerThreader.getFunction().produce(message);
	}
}
