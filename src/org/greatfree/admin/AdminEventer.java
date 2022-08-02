package org.greatfree.admin;

import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.ShutdownCoordinatorServerNotification;
import org.greatfree.testing.message.ShutdownCrawlServerNotification;
import org.greatfree.testing.message.ShutdownMemoryServerNotification;
import org.greatfree.testing.message.ShutdownServerNotification;

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
	// The notification of ShutdownServerNotification is sent to the server in an asynchronous manner. 01/20/2016, Bing Li
	private AsyncRemoteEventer<ShutdownServerNotification> shutdownServerNotificationEventer;
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
	public void dispose(long timeout) throws InterruptedException
	{
		// Shutdown the eventer that sends the notification of ShutdownCrawlServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownMemoryServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownCoordinatorServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownServerNotification in an asynchronous manner. 01/20/2016, Bing Li
		this.shutdownServerNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown(timeout);
	}
	
	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the shutting down crawling server notification eventer. 11/27/2014, Bing Li
//		this.shutdownCrawlServerNotificationEventer = new AsyncRemoteEventer<ShutdownCrawlServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownCrawlServerNotificationEventer = new AsyncRemoteEventer<ShutdownCrawlServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownCrawlServerNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownCrawlServerNotification>()
				.clientPool(ClientPool.ADMIN().getPool())
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
		
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownCrawlServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownCrawlServerNotificationEventer);
		
		// Initialize the shutting down memory server notification eventer. 11/27/2014, Bing Li
//		this.shutdownMemServerNotificationEventer = new AsyncRemoteEventer<ShutdownMemoryServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownMemServerNotificationEventer = new AsyncRemoteEventer<ShutdownMemoryServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownMemServerNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownMemoryServerNotification>()
				.clientPool(ClientPool.ADMIN().getPool())
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
		
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownMemServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownMemServerNotificationEventer);
		
		// Initialize the shutting down coordinator server notification eventer. 11/27/2014, Bing Li
//		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownCoordinatorServerNotification>()
				.clientPool(ClientPool.ADMIN().getPool())
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
		
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownCoordinatorNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownCoordinatorNotificationEventer);
		
		// Initialize the shutting down server notification eventer. 01/20/2016, Bing Li
//		this.shutdownServerNotificationEventer = new AsyncRemoteEventer<ShutdownServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownServerNotificationEventer = new AsyncRemoteEventer<ShutdownServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownServerNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownServerNotification>()
				.clientPool(ClientPool.ADMIN().getPool())
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
	}

	/*
	 * Notify the coordinator to shut down the cluster of crawler servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCrawlServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownCrawlServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownCrawlServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownCrawlServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCrawlServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the cluster of memory servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownMemoryServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownMemServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownMemServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownMemServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownMemoryServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the coordinator. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCoordinator()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownCoordinatorNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownCoordinatorNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownCoordinatorNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCoordinatorServerNotification());
	}
	
	/*
	 * Notify the coordinator to shut down the server. 01/20/2016, Bing Li
	 */
	public void notifyShutdownServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownServerNotificationEventer.notify(ServerConfig.SERVER_IP, ServerConfig.ADMIN_PORT, new ShutdownServerNotification());
	}
}
