package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.testing.data.CrawledLink;
import org.greatfree.testing.message.AddCrawledLinkNotification;

/*
 * This is an eventer that sends notifications to one of the distributed memory node in a synchronous or asynchronous manner. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryEventer
{
	// The thread pool that starts up the asynchronous eventer. 11/28/2014, Bing Li
	private ThreadPool pool;
	// The asynchronous eventer sends crawled links to one of the distributed memory servers. 11/28/2014, Bing Li
	private AsyncRemoteEventer<AddCrawledLinkNotification> addCrawledLinkEventer;
	
	private MemoryEventer()
	{
	}

	/*
	 * Initialize a singleton. 11/28/2014, Bing Li
	 */
	private static MemoryEventer instance = new MemoryEventer();
	
	public static MemoryEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new MemoryEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventer. 11/28/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException
	{
		this.addCrawledLinkEventer.dispose();
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the adding crawled link eventer. 11/28/2014, Bing Li
//		this.addCrawledLinkEventer = new AsyncRemoteEventer<AddCrawledLinkNotification>(MemoryServerClientPool.COORDINATE().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.addCrawledLinkEventer = new AsyncRemoteEventer<AddCrawledLinkNotification>(MemoryServerClientPool.COORDINATE().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.addCrawledLinkEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<AddCrawledLinkNotification>()
				.clientPool(MemoryServerClientPool.COORDINATE().getPool())
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

		// Set the idle checking for the adding crawled link eventer. 11/28/2014, Bing Li
//		this.addCrawledLinkEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the adding crawled link eventer. 11/28/2014, Bing Li
//		this.pool.execute(this.addCrawledLinkEventer);
	}

	/*
	 * Add the crawled links to the remote memory server identified by the key, dcKey. 11/28/2014, Bing Li
	 */
	public void addCrawledLink(String dcKey, CrawledLink link)
	{
		if (!this.addCrawledLinkEventer.isReady())
		{
			this.pool.execute(this.addCrawledLinkEventer);
		}
		this.addCrawledLinkEventer.notify(dcKey, new AddCrawledLinkNotification(dcKey, link));
	}
}
