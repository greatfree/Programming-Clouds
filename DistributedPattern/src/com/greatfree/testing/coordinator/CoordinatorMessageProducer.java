package com.greatfree.testing.coordinator;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.concurrency.Threader;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.coordinator.admin.AdminServerDispatcher;
import com.greatfree.testing.coordinator.admin.AdminServerProducerDisposer;
import com.greatfree.testing.coordinator.crawling.CrawlServerDispatcher;
import com.greatfree.testing.coordinator.crawling.CrawlServerProducerDisposer;
import com.greatfree.testing.coordinator.memorizing.MemoryServerDispatcher;
import com.greatfree.testing.coordinator.memorizing.MemoryServerProducerDisposer;
import com.greatfree.testing.coordinator.searching.SearchServerDispatcher;
import com.greatfree.testing.coordinator.searching.SearchServerProducerDisposer;
import com.greatfree.testing.data.ServerConfig;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client, such as the crawler server, that connects to a respective port on the coordinator. Usually, each port aims to provide one particular service. 11/24/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 08/22/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CoordinatorMessageProducer
{
	// The Threader aims to associate with the crawler message producer to guarantee the producer can work concurrently. 11/24/2014, Bing Li
	private Threader<MessageProducer<CrawlServerDispatcher>, CrawlServerProducerDisposer> crawlProducerThreader;
	// The Threader aims to associate with the memory server message producer to guarantee the producer can work concurrently. 11/28/2014, Bing Li
	private Threader<MessageProducer<MemoryServerDispatcher>, MemoryServerProducerDisposer> memoryProducerThreader;
	// The Threader aims to associate with the administration message producer to guarantee the producer can work concurrently. 11/27/2014, Bing Li
	private Threader<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer> adminProducerThreader;
	// The Threader aims to associate with the search message producer to guarantee the producer can work concurrently. 11/29/2014, Bing Li
	private Threader<MessageProducer<SearchServerDispatcher>, SearchServerProducerDisposer> searchProducerThreader;
	
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
		this.crawlProducerThreader.stop();
		this.memoryProducerThreader.stop();
		this.adminProducerThreader.stop();
		this.searchProducerThreader.stop();
	}
	
	/*
	 * Initialize the message producers. It is invoked when the connection modules of the coordinator is started since clients can send requests or notifications only after it is started. 11/24/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the crawling message producer. A threader is associated with the crawling message producer such that the producer is able to work in a concurrent way. 11/24/2014, Bing Li
		this.crawlProducerThreader = new Threader<MessageProducer<CrawlServerDispatcher>, CrawlServerProducerDisposer>(new MessageProducer<CrawlServerDispatcher>(new CrawlServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new CrawlServerProducerDisposer());
		// Start the associated thread for the crawling message producer. 11/24/2014, Bing Li
		this.crawlProducerThreader.start();

		// Initialize the memory server message producer. A threader is associated with the memory server message producer such that the producer is able to work in a concurrent way. 11/28/2014, Bing Li
		this.memoryProducerThreader = new Threader<MessageProducer<MemoryServerDispatcher>, MemoryServerProducerDisposer>(new MessageProducer<MemoryServerDispatcher>(new MemoryServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new MemoryServerProducerDisposer());
		// Start the associated thread for the memory server message producer. 11/28/2014, Bing Li
		this.memoryProducerThreader.start();
		
		// Initialize the administration message producer. A threader is associated with the administration message producer such that the producer is able to work in a concurrent way. 11/27/2014, Bing Li
		this.adminProducerThreader = new Threader<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new AdminServerProducerDisposer());
		// Start the associated thread for the administration message producer. 11/27/2014, Bing Li
		this.adminProducerThreader.start();
		
		// Initialize the search message producer. A threader is associated with the search message producer such that the producer is able to work in a concurrent way. 11/29/2014, Bing Li
		this.searchProducerThreader = new Threader<MessageProducer<SearchServerDispatcher>, SearchServerProducerDisposer>(new MessageProducer<SearchServerDispatcher>(new SearchServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new SearchServerProducerDisposer());
		// Start the associated thread for the search message producer. 11/29/2014, Bing Li
		this.searchProducerThreader.start();
	}
	
	/*
	 * Assign crawling messages, requests or notifications, to the bound crawling message dispatcher such that they can be responded or dealt with. 11/24/2014, Bing Li
	 */
	public void produceCrawlingMessage(OutMessageStream<ServerMessage> message)
	{
		this.crawlProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign memory server messages, requests or notifications, to the bound memory server message dispatcher such that they can be responded or dealt with. 11/28/2014, Bing Li
	 */
	public void produceMemoryMessage(OutMessageStream<ServerMessage> message)
	{
		this.memoryProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign administration messages, requests or notifications, to the bound administration message dispatcher such that they can be responded or dealt with. 11/27/2014, Bing Li
	 */
	public void produceAdminMessage(OutMessageStream<ServerMessage> message)
	{
		this.adminProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign search messages, requests or notifications, to the bound search message dispatcher such that they can be responded or dealt with. 11/29/2014, Bing Li
	 */
	public void produceSearchMessage(OutMessageStream<ServerMessage> message)
	{
		this.searchProducerThreader.getFunction().produce(message);
	}
}
