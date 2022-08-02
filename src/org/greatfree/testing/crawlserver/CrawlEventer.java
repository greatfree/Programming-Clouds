package org.greatfree.testing.crawlserver;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.testing.data.CrawledLink;
import org.greatfree.testing.message.CrawledLinksNotification;
import org.greatfree.testing.message.OnlineNotification;
import org.greatfree.testing.message.RegisterCrawlServerNotification;
import org.greatfree.testing.message.UnregisterCrawlServerNotification;
import org.greatfree.util.NodeID;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlEventer
{
	// The IP of the coordinator the eventer needs to notify. 11/23/2014, Bing Li
	private String ip;
	// The port of the coordinator the eventer needs to notify. 11/23/2014, Bing Li
	private int port;
	// The thread pool that starts up the asynchronous eventer. 11/23/2014, Bing Li
	private ThreadPool pool;
	// The synchronous eventer notifies the coordinator that a crawler is online. After receiving the notification, the coordinator can assign tasks and interact with the crawler for crawling. 11/23/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineNotificationEventer;
	// The synchronous eventer notifies the coordinator that a crawler needs to register. 11/23/2014, Bing Li
	private SyncRemoteEventer<RegisterCrawlServerNotification> registerCrawlServerEventer;
	// The synchronous eventer notifies the coordinator that a crawler needs to unregister. 11/23/2014, Bing Li
	private SyncRemoteEventer<UnregisterCrawlServerNotification> unregisterCrawlServerEventer;
	// The asynchronous eventer sends crawled links to the coordinator. 11/23/2014, Bing Li
	private AsyncRemoteEventer<CrawledLinksNotification> crawledLinkEventer;
	
	private CrawlEventer()
	{
	}

	/*
	 * Initialize a singleton. 11/23/2014, Bing Li
	 */
	private static CrawlEventer instance = new CrawlEventer();
	
	public static CrawlEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new CrawlEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventer. 11/23/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException, IOException
	{
		// Dispose the online eventer. 11/23/2014, Bing Li
		this.onlineNotificationEventer.dispose();
		// Dispose the registering eventer. 11/23/2014, Bing Li
		this.registerCrawlServerEventer.dispose();
		// Dispose the unregistering eventer. 11/23/2014, Bing Li
		this.unregisterCrawlServerEventer.dispose();
		// Dispose the crawled link eventer. 11/23/2014, Bing Li
		this.crawledLinkEventer.dispose();
		// Shutdown the thread pool. 11/23/2014, Bing Li
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize the eventer. The IP/port is the coordinator to be notified. 11/23/2014, Bing Li
	 */
	public void init(String ipAddress, int port)
	{
		// Assign the IP. 11/23/2014, Bing Li
		this.ip = ipAddress;
		// Assign the port. 11/23/2014, Bing Li
		this.port = port;
		// Initialize a thread pool. 11/23/2014, Bing Li
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		this.onlineNotificationEventer = new SyncRemoteEventer<OnlineNotification>(ClientPool.CRAWL().getPool());
		this.registerCrawlServerEventer = new SyncRemoteEventer<RegisterCrawlServerNotification>(ClientPool.CRAWL().getPool());
		this.unregisterCrawlServerEventer = new SyncRemoteEventer<UnregisterCrawlServerNotification>(ClientPool.CRAWL().getPool());
		
		// Initialize the crawled links eventer. 11/23/2014, Bing Li
//		this.crawledLinkEventer = new AsyncRemoteEventer<CrawledLinksNotification>(ClientPool.CRAWL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.crawledLinkEventer = new AsyncRemoteEventer<CrawledLinksNotification>(ClientPool.CRAWL().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.crawledLinkEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<CrawledLinksNotification>()
				.clientPool(ClientPool.CRAWL().getPool())
//				.threadPool(this.pool)
				.eventQueueSize(ClientConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(ClientConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(ClientConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventQueueWaitTime(ClientConfig.ASYNC_EVENTER_WAIT_TIME)
//				.waitRound(ClientConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		// Set the idle checking for the crawled links eventer. 11/23/2014, Bing Li
//		this.crawledLinkEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the crawled links eventer. 11/23/2014, Bing Li
//		this.pool.execute(this.crawledLinkEventer);
	}

	/*
	 * Notify the coordinator that the crawler is online. 11/23/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineNotificationEventer.notify(this.ip, this.port, new OnlineNotification());
	}

	/*
	 * Register the crawler. 11/23/2014, Bing Li
	 */
	public void register()
	{
		try
		{
			this.registerCrawlServerEventer.notify(this.ip, this.port, new RegisterCrawlServerNotification(NodeID.DISTRIBUTED().getKey(), CrawlScheduler.CRAWL().getURLCount()));
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
	 * Unregister the crawler. 11/23/2014, Bing Li
	 */
	public void unregister()
	{
		try
		{
			this.unregisterCrawlServerEventer.notify(this.ip, this.port, new UnregisterCrawlServerNotification(NodeID.DISTRIBUTED().getKey()));
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
	 * Send the crawled links to the coordinator concurrently. 11/23/2014, Bing Li
	 */
	public void sendCrawledLinks(Set<CrawledLink> links)
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.crawledLinkEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.crawledLinkEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.crawledLinkEventer.notify(this.ip, this.port, new CrawledLinksNotification(links));
	}
}
