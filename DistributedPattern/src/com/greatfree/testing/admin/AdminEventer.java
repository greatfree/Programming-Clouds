package com.greatfree.testing.admin;

import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.AsyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ShutdownCoordinatorServerNotification;
import com.greatfree.testing.message.ShutdownCrawlServerNotification;
import com.greatfree.testing.message.ShutdownMemoryServerNotification;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminEventer
{
	// The notification of ShutdownCrawlServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCrawlServerNotification> shutdownCrawlServerNotificationEventer;
	// The notification of ShutdownMemoryServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownMemoryServerNotification> shutdownMemServerNotificationEventer;
	// The notification of ShutdownCoordinatorServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCoordinatorServerNotification> shutdownCoordinatorNotificationEventer;
	// The thread pool that starts up the asynchronous eventer. 11/27/2014, Bing Li
	private ThreadPool pool;
	
	private AdminEventer()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static AdminEventer instance = new AdminEventer();
	
	public static AdminEventer CONSOLE()
	{
		if (instance == null)
		{
			instance = new AdminEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the eventer. 11/27/2014, Bing Li
	 */
	public void dispose()
	{
		// Shutdown the eventer to send the notification of ShutdownCrawlServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer.dispose();
		// Shutdown the eventer to send the notification of ShutdownMemoryServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer.dispose();
		// Shutdown the eventer to send the notification of ShutdownCoordinatorServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown();
	}
	
	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the shutting down crawling server notification eventer. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer = new AsyncRemoteEventer<ShutdownCrawlServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME);
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
		this.pool.execute(this.shutdownCrawlServerNotificationEventer);
		
		// Initialize the shutting down memory server notification eventer. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer = new AsyncRemoteEventer<ShutdownMemoryServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME);
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
		this.pool.execute(this.shutdownMemServerNotificationEventer);
		
		// Initialize the shutting down coordinator server notification eventer. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME);
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
		this.pool.execute(this.shutdownCoordinatorNotificationEventer);
	}

	/*
	 * Notify the coordinator to shut down the cluster of crawler servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCrawlServer()
	{
		this.shutdownCrawlServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCrawlServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the cluster of memory servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownMemoryServer()
	{
		this.shutdownMemServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownMemoryServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the coordinator. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCoordinator()
	{
		this.shutdownCoordinatorNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCoordinatorServerNotification());
	}
}
