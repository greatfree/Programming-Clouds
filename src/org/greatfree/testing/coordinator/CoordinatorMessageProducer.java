package org.greatfree.testing.coordinator;

import org.greatfree.concurrency.Runner;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.MessageProducer;
import org.greatfree.testing.coordinator.admin.AdminServerDispatcher;
import org.greatfree.testing.coordinator.crawling.CrawlServerDispatcher;
import org.greatfree.testing.coordinator.memorizing.MemoryServerDispatcher;
import org.greatfree.testing.coordinator.searching.SearchServerDispatcher;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client, such as the crawler server, that connects to a respective port on the coordinator. Usually, each port aims to provide one particular service. 11/24/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 08/22/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class CoordinatorMessageProducer
{
	// The Threader aims to associate with the crawler message producer to guarantee the producer can work concurrently. 11/24/2014, Bing Li
//	private Runner<MessageProducer<CrawlServerDispatcher>, CrawlServerProducerDisposer> crawlProducerThreader;
	private Runner<MessageProducer<CrawlServerDispatcher>> crawlProducerThreader;
	// The Threader aims to associate with the memory server message producer to guarantee the producer can work concurrently. 11/28/2014, Bing Li
//	private Runner<MessageProducer<MemoryServerDispatcher>, MemoryServerProducerDisposer> memoryProducerThreader;
	private Runner<MessageProducer<MemoryServerDispatcher>> memoryProducerThreader;
	// The Threader aims to associate with the administration message producer to guarantee the producer can work concurrently. 11/27/2014, Bing Li
//	private Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer> adminProducerThreader;
	private Runner<MessageProducer<AdminServerDispatcher>> adminProducerThreader;
	// The Threader aims to associate with the search message producer to guarantee the producer can work concurrently. 11/29/2014, Bing Li
//	private Runner<MessageProducer<SearchServerDispatcher>, SearchServerProducerDisposer> searchProducerThreader;
	private Runner<MessageProducer<SearchServerDispatcher>> searchProducerThreader;
	
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
//		this.crawlProducerThreader = new Runner<MessageProducer<CrawlServerDispatcher>, CrawlServerProducerDisposer>(new MessageProducer<CrawlServerDispatcher>(new CrawlServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new CrawlServerProducerDisposer());
		this.crawlProducerThreader = new Runner<MessageProducer<CrawlServerDispatcher>>(new MessageProducer<CrawlServerDispatcher>(new CrawlServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
//		this.crawlProducerThreader = new Runner<MessageProducer<CrawlServerDispatcher>>(new MessageProducer<CrawlServerDispatcher>(new CrawlServerDispatcher(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the crawling message producer. 11/24/2014, Bing Li
		this.crawlProducerThreader.start();

		// Initialize the memory server message producer. A threader is associated with the memory server message producer such that the producer is able to work in a concurrent way. 11/28/2014, Bing Li
//		this.memoryProducerThreader = new Runner<MessageProducer<MemoryServerDispatcher>, MemoryServerProducerDisposer>(new MessageProducer<MemoryServerDispatcher>(new MemoryServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new MemoryServerProducerDisposer());
		this.memoryProducerThreader = new Runner<MessageProducer<MemoryServerDispatcher>>(new MessageProducer<MemoryServerDispatcher>(new MemoryServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the memory server message producer. 11/28/2014, Bing Li
		this.memoryProducerThreader.start();
		
		// Initialize the administration message producer. A threader is associated with the administration message producer such that the producer is able to work in a concurrent way. 11/27/2014, Bing Li
//		this.adminProducerThreader = new Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new AdminServerProducerDisposer());
		this.adminProducerThreader = new Runner<MessageProducer<AdminServerDispatcher>>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the administration message producer. 11/27/2014, Bing Li
		this.adminProducerThreader.start();
		
		// Initialize the search message producer. A threader is associated with the search message producer such that the producer is able to work in a concurrent way. 11/29/2014, Bing Li
//		this.searchProducerThreader = new Runner<MessageProducer<SearchServerDispatcher>, SearchServerProducerDisposer>(new MessageProducer<SearchServerDispatcher>(new SearchServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)), new SearchServerProducerDisposer());
		this.searchProducerThreader = new Runner<MessageProducer<SearchServerDispatcher>>(new MessageProducer<SearchServerDispatcher>(new SearchServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME, ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME)));
		// Start the associated thread for the search message producer. 11/29/2014, Bing Li
		this.searchProducerThreader.start();
	}
	
	/*
	 * Assign crawling messages, requests or notifications, to the bound crawling message dispatcher such that they can be responded or dealt with. 11/24/2014, Bing Li
	 */
	public void produceCrawlingMessage(MessageStream<ServerMessage> message)
	{
		this.crawlProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign memory server messages, requests or notifications, to the bound memory server message dispatcher such that they can be responded or dealt with. 11/28/2014, Bing Li
	 */
	public void produceMemoryMessage(MessageStream<ServerMessage> message)
	{
		this.memoryProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign administration messages, requests or notifications, to the bound administration message dispatcher such that they can be responded or dealt with. 11/27/2014, Bing Li
	 */
	public void produceAdminMessage(MessageStream<ServerMessage> message)
	{
		this.adminProducerThreader.getFunction().produce(message);
	}
	
	/*
	 * Assign search messages, requests or notifications, to the bound search message dispatcher such that they can be responded or dealt with. 11/29/2014, Bing Li
	 */
	public void produceSearchMessage(MessageStream<ServerMessage> message)
	{
		this.searchProducerThreader.getFunction().produce(message);
	}
}
