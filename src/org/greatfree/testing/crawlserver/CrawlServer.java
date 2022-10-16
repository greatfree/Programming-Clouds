package org.greatfree.testing.crawlserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.concurrency.Threader;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.NullObject;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

/*
 * This is an example to demonstrate the distributed patterns introduced in the tutorial. 11/11/2014, Bing Li
 * 
 *  1) The code is used to crawl a large number of URLs. In this case, the count of URLs exceeds 10,000.
 * 
 * 	2) Because of the heavy burden to crawl, it is required to distribute the crawling tasks to multiple crawlers to balance the load.
 * 
 * 	3) The crawling results must be submitted to the coordinator for distributed caching and summarizing.
 * 
 * 	4) This server is one of the components to receive and forward crawling tasks, crawl URLs and submit the results.
 * 
 */

// Created: 11/11/2014, Bing Li
public class CrawlServer
{
	// The socket that waits for connections from other crawlers and the coordinator. In this case, the crawlers and the coordinator form a cluster such that it is possible to crawl the Web in a large scale. 11/24/2014, Bing Li
	private ServerSocket serverSocket;
	// The port to wait for connections. 11/24/2014, Bing Li
	private int serverPort;

	// The listener list contains all of the listeners to wait for connections. 11/24/2014, Bing Li
//	private List<Runner<CrawlingListener, CrawlingListenerDisposer>> listeners;
	private List<Runner<CrawlingListener>> listeners;

	// The threader contains the instance of CrawlConsumer, which is a producer/consumer pattern. The consumer schedules and crawls the URLs until the threader is stopped. 11/24/2014, Bing Li
//	private Threader<CrawlConsumer, CrawlConsumerDisposer> crawlConsumerThreader;
	private Threader<CrawlConsumer> crawlConsumerThreader;

	// Declare a timer to control the crawling state checking periodically. 11/27/2014, Bing Li
	private Timer crawlCheckingTimer;
	// Declare The crawling state checker. 11/27/2014, Bing Li
	private CrawlingStateChecker stateChecker;

	private CrawlServer()
	{
	}

	/*
	 * A singleton implementation since this is the unique entry and exit of the crawler. 11/24/2014, Bing Li
	 */
	private static CrawlServer instance = new CrawlServer();
	
	public static CrawlServer CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlServer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Start up the crawler. 11/24/2014, Bing Li
	 */
	public void start(int serverPort)
	{
		// On JD7, the sorting algorithm is replaced with TimSort rather than MargeSort. To run correctly, it is necessary to use the old one. the following line sets that up. 11/23/2014, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);

		SharedThreadPool.SHARED().init(CrawlConfig.CRAWLING_TASK_LISTENER_THREAD_POOL_SIZE, CrawlConfig.CRAWLING_TASK_LISTENER_THREAD_ALIVE_TIME);
		
		// Assign the crawler port. 11/24/2014, Bing Li
		this.serverPort = serverPort;
		try
		{
			// Initialize the socket of the crawler. 11/24/2014, Bing Li
			this.serverSocket = new ServerSocket(this.serverPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a list to contain the listeners. 11/24/2014, Bing Li
//		this.listeners = new ArrayList<Runner<CrawlingListener, CrawlingListenerDisposer>>();
		this.listeners = new ArrayList<Runner<CrawlingListener>>();
		// Initialize a disposer that collects the listeners. 11/24/2014, Bing Li
//		CrawlingListenerDisposer disposer = new CrawlingListenerDisposer();
		// The runner contains the listener and listener disposer to start the listeners concurrently. 11/24/2014, Bing Li
//		Runner<CrawlingListener, CrawlingListenerDisposer> runner;
		Runner<CrawlingListener> runner;
		// Initialize and start a certain number of listeners concurrently. 11/24/2014, Bing Li
		for (int i = 0; i < ServerConfig.MAX_CLIENT_LISTEN_THREAD_COUNT; i++)
		{
			// Initialize the runner that contains the listener and its disposer. 11/24/2014, Bing Li
//			runner = new Runner<CrawlingListener, CrawlingListenerDisposer>(new CrawlingListener(this.serverSocket), disposer, true);
//			runner = new Runner<CrawlingListener, CrawlingListenerDisposer>(new CrawlingListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), disposer, true);
			runner = new Runner<CrawlingListener>(new CrawlingListener(this.serverSocket, SharedThreadPool.SHARED().getPool()), true);
			// Put the runner into a list for management. 11/24/2014, Bing Li
			this.listeners.add(runner);
			// Start up the runner. 11/24/2014, Bing Li
			runner.start();
		}

		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the crawling server IO registry. 11/24/2014, Bing Li
		CrawlIORegistry.REGISTRY().init();
		// Initialize the client pool that is used to connect the coordinator. 11/24/2014, Bing Li
		ClientPool.CRAWL().init();
		// Initialize the sub client pool that is used to connect the children of the crawler. 11/27/2014, Bing Li
		SubClientPool.CRAWL().init();
		// Initialize the crawling eventer that sends notifications to the coordinator. For example, the crawled links are sent to the coordinator asynchronously by the eventer. 11/24/2014, Bing Li
		CrawlEventer.NOTIFY().init(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_CRAWLER);

		// Initialize the message producer which dispatches received requests and notifications from the coordinator. 11/24/2014, Bing Li
		CrawlMessageProducer.CRAWL().init();
		// Initialize the crawling scheduler. 11/24/2014, Bing Li
		CrawlScheduler.CRAWL().init();

		// Initialize the threader that contains the crawling consumer and its disposer. 11/24/2014, Bing Li
//		this.crawlConsumerThreader = new Threader<CrawlConsumer, CrawlConsumerDisposer>(new CrawlConsumer(new CrawlEater(), CrawlConfig.CRAWL_SCHEDULER_WAIT_TIME), new CrawlConsumerDisposer());
		this.crawlConsumerThreader = new Threader<CrawlConsumer>(new CrawlConsumer(new CrawlEater(), CrawlConfig.CRAWL_SCHEDULER_WAIT_TIME));

		// Initialize the crawling multicastor. 12/01/2014, Bing Li
		CrawlerMulticastor.CRAWLER().init();

		try
		{
			// Notify the coordinator that the crawler is online. 11/24/2014, Bing Li
			CrawlEventer.NOTIFY().notifyOnline();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Start the crawling. It is actually invoked remotely by the coordinator when a StartCrawlMultiNotification is received. 11/24/2014, Bing Li
	 */
	public void startCrawl()
	{
		// Start the crawling consumer. 11/24/2014, Bing Li
		this.crawlConsumerThreader.start();
		// Produce a null object, which is not processed by the consumer. However, an infinite loop to schedule URLs is started for that. 11/24/2014, Bing Li
		this.crawlConsumerThreader.getFunction().produce(new NullObject());

		// Initialize the crawling state checker. 11/27/2014, Bing Li
		this.stateChecker = new CrawlingStateChecker();
		// Initialize the timer. 11/27/2014, Bing Li
		this.crawlCheckingTimer = new Timer();
		// Schedule the crawling state checker. 11/27/2014, Bing Li
		this.crawlCheckingTimer.schedule(this.stateChecker, 0, CrawlConfig.CRAWLING_STATE_CHECK_PERIOD);
	}

	/*
	 * Stop the crawler. 11/24/2014, Bing Li
	 */
	public void stop() throws InterruptedException, IOException, ClassNotFoundException
	{
		// Set the terminating signal. The long time running task, scheduling the crawling URLs, needs to be interrupted when the signal is set. 11/24/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// Stop each listener one by one. 11/24/2014, Bing Li
//		for (Runner<CrawlingListener, CrawlingListenerDisposer> runner : this.listeners)
		for (Runner<CrawlingListener> runner : this.listeners)
		{
			runner.stop(ClientConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);
		}

		// Close the socket of the crawling server. 11/24/2014, Bing Li
		this.serverSocket.close();
		
		// Stop the crawling consumer. 11/24/2014, Bing Li
		this.crawlConsumerThreader.stop(ClientConfig.TIME_TO_WAIT_FOR_THREAD_TO_DIE);

		// Check whether the crawling state checker is valid. It might not be initialized if the crawling is not started. 11/27/2014, Bing Li
		if (this.stateChecker != null)
		{
			// End the checking. 11/27/2014, Bing Li
			this.stateChecker.cancel();
			// End the timer. 11/27/2014, Bing Li
			this.crawlCheckingTimer.cancel();
		}

		// Shutdown the scheduler. 02/02/2016, Bing Li
//		Scheduler.GREATFREE().shutdown();

		// Unregister the crawling eventer. 11/24/2014, Bing Li
		CrawlEventer.NOTIFY().unregister();
		// Dispose the crawling eventer. 11/24/2014, Bing Li
		CrawlEventer.NOTIFY().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);

		// Dispose the message producer. 11/24/2014, Bing Li
		CrawlMessageProducer.CRAWL().dispose();
		// Shutdown the crawling server IOs. 11/24/2014, Bing Li
		CrawlIORegistry.REGISTRY().dispose();
		// Dispose the client pool. 11/24/2014, Bing Li
		ClientPool.CRAWL().dispose();
		// Dispose the sub client pool. 11/27/2014, Bing Li
		SubClientPool.CRAWL().dispose();
		// Dispose the crawling multicastor. 12/01/2014, Bing Li
		CrawlerMulticastor.CRAWLER().dispose();
		// Dispose the crawling scheduler. 11/24/2014, Bing Li
		CrawlScheduler.CRAWL().dispose();
		
		// Dispose the shared thread pool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose(ClientConfig.THREAD_POOL_SHUTDOWN_TIMEOUT);
	}
}
