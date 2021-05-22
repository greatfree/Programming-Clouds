package org.greatfree.testing.cluster.coordinator;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.Runner;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.abandoned.MessageProducer;
import org.greatfree.testing.cluster.coordinator.admin.AdminServerDispatcher;
import org.greatfree.testing.cluster.coordinator.client.ClientServerDispatcher;
import org.greatfree.testing.cluster.coordinator.dn.DNServerDispatcher;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client, that connects to a respective port on the coordinator. Usually, each port aims to provide one particular service. 11/24/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 08/22/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class CoordinatorMessageProducer
{
	// The Threader aims to associate with the client message producer to guarantee the producer can work concurrently. 11/24/2014, Bing Li
//	private Runner<MessageProducer<ClientServerDispatcher>, ClientServerProducerDisposer> clientProducerThreader;
	private Runner<MessageProducer<ClientServerDispatcher>> clientProducerThreader;
	// The Threader aims to associate with the DN server message producer to guarantee the producer can work concurrently. 11/28/2014, Bing Li
//	private Runner<MessageProducer<DNServerDispatcher>, DNServerProducerDisposer> dnProducerThreader;
	private Runner<MessageProducer<DNServerDispatcher>> dnProducerThreader;
	// The Threader aims to associate with the administration message producer to guarantee the producer can work concurrently. 11/27/2014, Bing Li
//	private Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer> adminProducerThreader;
	private Runner<MessageProducer<AdminServerDispatcher>> adminProducerThreader;

	private CoordinatorMessageProducer()
	{
	}

	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 11/24/2014, Bing Li
	 */
	private static CoordinatorMessageProducer instance = new CoordinatorMessageProducer();
	
	public static CoordinatorMessageProducer SERVER()
	{
		if (instance == null)
		{
			instance = new CoordinatorMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the coordinator is shutdown. 11/24/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.clientProducerThreader.stop();
		this.dnProducerThreader.stop();
		this.adminProducerThreader.stop();
	}
	
	/*
	 * Initialize the message producers. It is invoked when the connection modules of the coordinator is started since clients can send requests or notifications only after it is started. 11/24/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client message producer. A threader is associated with the crawling message producer such that the producer is able to work in a concurrent way. 11/24/2014, Bing Li
//		this.clientProducerThreader = new Runner<MessageProducer<ClientServerDispatcher>, ClientServerProducerDisposer>(new MessageProducer<ClientServerDispatcher>(new ClientServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new ClientServerProducerDisposer());
		this.clientProducerThreader = new Runner<MessageProducer<ClientServerDispatcher>>(new MessageProducer<ClientServerDispatcher>(new ClientServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
//		this.clientProducerThreader = new Runner<MessageProducer<ClientServerDispatcher>>(new MessageProducer<ClientServerDispatcher>(new ClientServerDispatcher(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the client message producer. 11/24/2014, Bing Li
		this.clientProducerThreader.start();

		// Initialize the client message producer. A threader is associated with the crawling message producer such that the producer is able to work in a concurrent way. 11/24/2014, Bing Li
//		this.dnProducerThreader = new Runner<MessageProducer<DNServerDispatcher>, DNServerProducerDisposer>(new MessageProducer<DNServerDispatcher>(new DNServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new DNServerProducerDisposer());
		this.dnProducerThreader = new Runner<MessageProducer<DNServerDispatcher>>(new MessageProducer<DNServerDispatcher>(new DNServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
//		this.dnProducerThreader = new Runner<MessageProducer<DNServerDispatcher>>(new MessageProducer<DNServerDispatcher>(new DNServerDispatcher(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the client message producer. 11/24/2014, Bing Li
		this.dnProducerThreader.start();

		// Initialize the administration message producer. A threader is associated with the administration message producer such that the producer is able to work in a concurrent way. 11/27/2014, Bing Li
//		this.adminProducerThreader = new Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new AdminServerProducerDisposer());
		this.adminProducerThreader = new Runner<MessageProducer<AdminServerDispatcher>>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the administration message producer. 11/27/2014, Bing Li
		this.adminProducerThreader.start();
	}

	/*
	 * Assign client messages, requests or notifications, to the bound client message dispatcher such that they can be responded or dealt with. 11/24/2014, Bing Li
	 */
	public void produceClientMessage(MessageStream<ServerMessage> message)
	{
		this.clientProducerThreader.getFunction().produce(message);
	}

	/*
	 * Assign memory server messages, requests or notifications, to the bound DN server message dispatcher such that they can be responded or dealt with. 11/28/2014, Bing Li
	 */
	public void produceDNMessage(MessageStream<ServerMessage> message)
	{
		this.dnProducerThreader.getFunction().produce(message);
	}

	/*
	 * Assign administration messages, requests or notifications, to the bound administration message dispatcher such that they can be responded or dealt with. 11/27/2014, Bing Li
	 */
	public void produceAdminMessage(MessageStream<ServerMessage> message)
	{
		this.adminProducerThreader.getFunction().produce(message);
	}
}
