package org.greatfree.testing.cluster.coordinator.admin;

import java.util.Calendar;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.ShutdownCoordinatorNotification;
import org.greatfree.testing.message.ShutdownDNNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the administrator's notifications for the coordinator. As the administrator is a human operator, the design is more complicated than required. Just an exercise. :-) 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
//public class AdminServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class AdminServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the DN shutting-down concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<ShutdownDNNotification, ShutdownDNThread, ShutdownDNThreadCreator> shutdownDNNotificationDispatcher;

	// Declare a notification dispatcher to process the coordinator shutting-down concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<ShutdownCoordinatorNotification, ShutdownCoordinatorThread, ShutdownCoordinatorThreadCreator> shutdownCoordinatorNotificationDispatcher;

	/*
	 * Initialize. 11/27/2014, Bing Li
	 */
	public AdminServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/27/2014, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the shutdown dispatcher. 11/27/2014, Bing Li
//		this.shutdownDNNotificationDispatcher = new NotificationDispatcher<ShutdownDNNotification, ShutdownDNThread, ShutdownDNThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownDNThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.shutdownDNNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownDNNotification, ShutdownDNThread, ShutdownDNThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownDNThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the shutdown dispatcher. 11/27/2014, Bing Li
//		this.shutdownCoordinatorNotificationDispatcher = new NotificationDispatcher<ShutdownCoordinatorNotification, ShutdownCoordinatorThread, ShutdownCoordinatorThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownCoordinatorThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());

		this.shutdownCoordinatorNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownCoordinatorNotification, ShutdownCoordinatorThread, ShutdownCoordinatorThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownCoordinatorThreadCreator())
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
		// Dispose the DN shutting-down dispatcher. 11/27/2014, Bing Li
		this.shutdownDNNotificationDispatcher.dispose();
		// Dispose the coordinator shutting-down dispatcher. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationDispatcher.dispose();
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
			// If the message is the notification to register the DN server. 11/27/2014, Bing Li
			case MessageType.SHUTDOWN_DN_NOTIFICATION:
				System.out.println("SHUTDOWN_DN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownDNNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownDNNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/27/2014, Bing Li
				this.shutdownDNNotificationDispatcher.enqueue((ShutdownDNNotification)message.getMessage());
				break;
				
				// If the message is the notification to register the coordinator server. 11/27/2014, Bing Li
			case MessageType.SHUTDOWN_COORDINATOR_NOTIFICATION:
				System.out.println("SHUTDOWN_COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownCoordinatorNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownCoordinatorNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/27/2014, Bing Li
				this.shutdownCoordinatorNotificationDispatcher.enqueue((ShutdownCoordinatorNotification)message.getMessage());
				break;
		}
	}
}
