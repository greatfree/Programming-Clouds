package org.greatfree.testing.coordinator.admin;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.ShutdownCrawlServerNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the administrator's notifications for the coordinator. As the administrator is a human operator, the design is more complicated than required. Just an exercise. :-) 11/27/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/27/2014, Bing Li
//public class AdminServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class AdminServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the crawler shutting-down concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<ShutdownCrawlServerNotification, ShutdownCrawlServerThread, ShutdownCrawlServerThreadCreator> shutdownCrawlServerNotificationDispatcher;

	/*
	 * Initialize. 11/27/2014, Bing Li
	 */
	public AdminServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/27/2014, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the shutdown dispatcher. 11/27/2014, Bing Li
//		this.shutdownCrawlServerNotificationDispatcher = new NotificationDispatcher<ShutdownCrawlServerNotification, ShutdownCrawlServerThread, ShutdownCrawlServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownCrawlServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.shutdownCrawlServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownCrawlServerNotification, ShutdownCrawlServerThread, ShutdownCrawlServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownCrawlServerThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
	}

	/*
	 * Shut down the administration message dispatcher. 11/27/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Dispose the crawler shutting-down dispatcher. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationDispatcher.dispose();
		// Shutdown the server message dispatcher. 11/27/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/27/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/27/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the notification to register the crawler server. 11/27/2014, Bing Li
			case MessageType.SHUTDOWN_CRAWL_SERVER_NOTIFICATION:
				// Check whether the shutdown notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownCrawlServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownCrawlServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/27/2014, Bing Li
				this.shutdownCrawlServerNotificationDispatcher.enqueue((ShutdownCrawlServerNotification)message.getMessage());
				break;
		}
	}
}
