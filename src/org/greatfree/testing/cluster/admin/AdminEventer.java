package org.greatfree.testing.cluster.admin;

import org.greatfree.admin.ClientPool;
import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.message.ShutdownCoordinatorNotification;
import org.greatfree.testing.message.ShutdownDNNotification;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class AdminEventer
{
	// The notification of ShutdownDNNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownDNNotification> shutdownDNNotificationEventer;
	// The notification of ShutdownCoordinatorNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCoordinatorNotification> shutdownCoordinatorNotificationEventer;
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
		// Shutdown the eventer that sends the notification of ShutdownDNNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownDNNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownCoordinatorNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown(timeout);
	}
	
	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the shutting down DN notification eventer. 11/27/2014, Bing Li
//		this.shutdownDNNotificationEventer = new AsyncRemoteEventer<ShutdownDNNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownDNNotificationEventer = new AsyncRemoteEventer<ShutdownDNNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownDNNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownDNNotification>()
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

		// Initialize the shutting down coordinator server notification eventer. 11/27/2014, Bing Li
//		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
//		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.ASYNC_EVENT_QUEUE_SIZE, ClientConfig.ASYNC_EVENTER_SIZE, ClientConfig.ASYNC_EVENTING_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_TIME, ClientConfig.ASYNC_EVENTER_WAIT_ROUND, ClientConfig.ASYNC_EVENT_IDLE_CHECK_DELAY, ClientConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD, ClientConfig.SCHEDULER_POOL_SIZE, ClientConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownCoordinatorNotification>()
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
	 * Notify the coordinator to shut down the cluster of DNs. 11/27/2014, Bing Li
	 */
	public void notifyShutdownDN()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownDNNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownDNNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownDNNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownDNNotification());
	}

	/*
	 * Notify the coordinator to shut down the cluster of DNs. 11/27/2014, Bing Li
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
		this.shutdownCoordinatorNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCoordinatorNotification());
	}
}
