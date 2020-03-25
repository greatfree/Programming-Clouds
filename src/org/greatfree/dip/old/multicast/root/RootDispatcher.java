package org.greatfree.dip.old.multicast.root;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.HelloWorldAnycastResponse;
import org.greatfree.dip.multicast.message.HelloWorldBroadcastResponse;
import org.greatfree.dip.multicast.message.HelloWorldUnicastResponse;
import org.greatfree.dip.multicast.message.MulticastDIPMessageType;
import org.greatfree.dip.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

/*
 * The dispatcher is employed by the root of the cluster to dispatch messages from the distributed nodes within the cluster. 05/10/2017, Bing Li
 */

// Created: 05/10/2017, Bing Li
class RootDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to receive remote responses which aim to answer the broadcast request. 05/21/2017, Bing Li
	private NotificationDispatcher<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread, HelloWorldBroadcastResponseThreadCreator> helloWorldBroadcastResponseDispatcher;

	// Declare a notification dispatcher to receive remote responses which aim to answer the anycast request. 05/21/2017, Bing Li
	private NotificationDispatcher<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator> helloWorldAnycastResponseDispatcher;

	// Declare a notification dispatcher to receive remote responses which aim to answer the unicast request. 05/21/2017, Bing Li
	private NotificationDispatcher<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator> helloWorldUnicastResponseDispatcher;

	// Declare a notification dispatcher to process ShutdownChildrenAdminNotification to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator> shutdownChildrenNotificationDispatcher;

//	public RootDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public RootDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, HelloWorldBroadcastResponse. 11/27/2014, Bing Li
		this.helloWorldBroadcastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldBroadcastResponse, HelloWorldBroadcastResponseThread, HelloWorldBroadcastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldBroadcastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, HelloWorldAnycastResponse. 11/27/2014, Bing Li
		this.helloWorldAnycastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastResponse, HelloWorldAnycastResponseThread, HelloWorldAnycastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldAnycastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, HelloWorldUnicastResponse. 11/27/2014, Bing Li
		this.helloWorldUnicastResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastResponse, HelloWorldUnicastResponseThread, HelloWorldUnicastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new HelloWorldUnicastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, ShutdownChildrenAdminNotification. 11/27/2014, Bing Li
		this.shutdownChildrenNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownChildrenAdminNotificationThreadCreator())
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
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.helloWorldBroadcastResponseDispatcher.dispose();
		this.helloWorldAnycastResponseDispatcher.dispose();
		this.helloWorldUnicastResponseDispatcher.dispose();
		this.shutdownChildrenNotificationDispatcher.dispose();
	}

	/*
	 * Process the available messages in a concurrent way. 04/17/2017, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{
			case MulticastDIPMessageType.HELLO_WORLD_BROADCAST_RESPONSE:
				System.out.println("HELLO_WORLD_BROADCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldBroadcastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldBroadcastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.helloWorldBroadcastResponseDispatcher.enqueue((HelloWorldBroadcastResponse)message.getMessage());
				break;
				
			case MulticastDIPMessageType.HELLO_WORLD_ANYCAST_RESPONSE:
				System.out.println("HELLO_WORLD_ANYCAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldAnycastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldAnycastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldAnycastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.helloWorldAnycastResponseDispatcher.enqueue((HelloWorldAnycastResponse)message.getMessage());
				break;

			case MulticastDIPMessageType.HELLO_WORLD_UNICAST_RESPONSE:
				System.out.println("HELLO_WORLD_UNICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.helloWorldUnicastResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.helloWorldUnicastResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldUnicastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.helloWorldUnicastResponseDispatcher.enqueue((HelloWorldUnicastResponse)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.shutdownChildrenNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.shutdownChildrenNotificationDispatcher);
				}
				// Enqueue the instance of ShutdownChildrenAdminNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.shutdownChildrenNotificationDispatcher.enqueue((ShutdownChildrenAdminNotification)message.getMessage());
				break;
		}
	}
}
