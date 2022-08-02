package org.greatfree.testing.cluster.coordinator.dn;

import java.util.Calendar;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;
import org.greatfree.testing.message.UnregisterClientNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond memory servers' requests and receive their notifications for the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
//public class DNServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class DNServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the DN registration concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterDNThread, RegisterDNThreadCreator> registerDNNotificationDispatcher;
	// Declare a notification dispatcher to process the DN unregistering concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<UnregisterClientNotification, UnregisterDNThread, UnregisterDNThreadCreator> unregisterDNNotificationDispatcher;

	public DNServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/27/2014, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the DN registration dispatcher. 11/27/2014, Bing Li
//		this.registerDNNotificationDispatcher = new NotificationDispatcher<RegisterClientNotification, RegisterDNThread, RegisterDNThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterDNThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.registerDNNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterClientNotification, RegisterDNThread, RegisterDNThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterDNThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the DN unregistering dispatcher. 11/27/2014, Bing Li
//		this.unregisterDNNotificationDispatcher = new NotificationDispatcher<UnregisterClientNotification, UnregisterDNThread, UnregisterDNThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new UnregisterDNThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());

		this.unregisterDNNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnregisterClientNotification, UnregisterDNThread, UnregisterDNThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new UnregisterDNThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
	}

	/*
	 * Shut down the DN message dispatcher. 11/24/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Dispose the DN registration dispatcher. 11/27/2014, Bing Li
		this.registerDNNotificationDispatcher.dispose();
		// Dispose the crawler unregistering dispatcher. 11/28/2014, Bing Li
		this.unregisterDNNotificationDispatcher.dispose();
		// Shutdown the server message dispatcher. 11/27/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/24/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/27/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the notification to register the DN server. 11/27/2014, Bing Li
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the DN registration notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.registerDNNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerDNNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerDNNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
				
				// If the message is the notification to unregister the DN server. 11/28/2014, Bing Li
			case MessageType.UNREGISTER_CLIENT_NOTIFICATION:
				System.out.println("UNREGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the DN unregistration dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.unregisterDNNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.unregisterDNNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/28/2014, Bing Li
				this.unregisterDNNotificationDispatcher.enqueue((UnregisterClientNotification)message.getMessage());
				break;
		}
	}
}
