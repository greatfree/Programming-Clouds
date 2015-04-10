package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.AsyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.CrawledLink;
import com.greatfree.testing.message.AddCrawledLinkNotification;

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
	public void dispose()
	{
		this.addCrawledLinkEventer.dispose();
		this.pool.shutdown();
	}

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the adding crawled link eventer. 11/28/2014, Bing Li
		this.addCrawledLinkEventer = new AsyncRemoteEventer<AddCrawledLinkNotification>(MemoryServerClientPool.COORDINATE().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME);
		// Set the idle checking for the adding crawled link eventer. 11/28/2014, Bing Li
		this.addCrawledLinkEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the adding crawled link eventer. 11/28/2014, Bing Li
		this.pool.execute(this.addCrawledLinkEventer);
	}

	/*
	 * Add the crawled links to the remote memory server identified by the key, dcKey. 11/28/2014, Bing Li
	 */
	public void addCrawledLink(String dcKey, CrawledLink link)
	{
		this.addCrawledLinkEventer.notify(dcKey, new AddCrawledLinkNotification(dcKey, link));
	}
}
