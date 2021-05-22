package org.greatfree.framework.multicast.rp.child;

import java.util.Calendar;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.HelloWorldAnycastNotification;
import org.greatfree.framework.multicast.message.HelloWorldBroadcastNotification;
import org.greatfree.framework.multicast.message.HelloWorldUnicastNotification;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.framework.multicast.rp.message.HelloWorldAnycastRequest;
import org.greatfree.framework.multicast.rp.message.HelloWorldBroadcastRequest;
import org.greatfree.framework.multicast.rp.message.HelloWorldUnicastRequest;
import org.greatfree.framework.multicast.rp.message.MulticastDIPRPMessageType;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.message.multicast.RPMulticastResponse;
import org.greatfree.server.ServerDispatcher;

// Created: 10/21/2018, Bing Li
public class ChildDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread, HelloWorldBroadcastNotificationThreadCreator> helloWorldBroadcastNotificationDispatcher;

	private NotificationDispatcher<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread, HelloWorldAnycastNotificationThreadCreator> helloWorldAnycastNotificationDispatcher;

	private NotificationDispatcher<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread, HelloWorldUnicastNotificationThreadCreator> helloWorldUnicastNotificationDispatcher;

	private NotificationDispatcher<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread, HelloWorldBroadcastRequestThreadCreator> helloWorldBroadcastRequestDispatcher;

	private NotificationDispatcher<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread, HelloWorldAnycastRequestThreadCreator> helloWorldAnycastRequestDispatcher;

	private NotificationDispatcher<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread, HelloWorldUnicastRequestThreadCreator> helloWorldUnicastRequestDispatcher;
	
	private NotificationDispatcher<RPMulticastResponse, ChildRPMulticastResponseThread, ChildRPMulticastResponseThreadCreator> rpResponseDispatcher;

	private NotificationDispatcher<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator> shutdownBroadcastNotificationDispatcher;

//	public ChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
		
		this.helloWorldBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldBroadcastNotification, HelloWorldBroadcastNotificationThread, HelloWorldBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.helloWorldAnycastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastNotification, HelloWorldAnycastNotificationThread, HelloWorldAnycastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldAnycastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.helloWorldUnicastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastNotification, HelloWorldUnicastNotificationThread, HelloWorldUnicastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldUnicastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.helloWorldBroadcastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldBroadcastRequest, HelloWorldBroadcastRequestThread, HelloWorldBroadcastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldBroadcastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.helloWorldAnycastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldAnycastRequest, HelloWorldAnycastRequestThread, HelloWorldAnycastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldAnycastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.helloWorldUnicastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloWorldUnicastRequest, HelloWorldUnicastRequestThread, HelloWorldUnicastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloWorldUnicastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.rpResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RPMulticastResponse, ChildRPMulticastResponseThread, ChildRPMulticastResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildRPMulticastResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		System.out.println("ChildDispatcher-dispose(): in processing ...");
		super.shutdown(timeout);
		System.out.println("ChildDispatcher-dispose(): done! ...");
		System.out.println("ChildDispatcher-dispose(): starting ...");
		this.helloWorldBroadcastNotificationDispatcher.dispose();
		this.helloWorldAnycastNotificationDispatcher.dispose();
		this.helloWorldUnicastNotificationDispatcher.dispose();
		this.helloWorldBroadcastRequestDispatcher.dispose();
		this.helloWorldAnycastRequestDispatcher.dispose();
		this.helloWorldUnicastRequestDispatcher.dispose();
		this.rpResponseDispatcher.dispose();
		this.shutdownBroadcastNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			// Process the notification of HelloWorldBroadcastNotification. 11/27/2014, Bing Li
			case MulticastDIPRPMessageType.HELLO_WORLD_BROADCAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.helloWorldBroadcastNotificationDispatcher);
				}
				this.helloWorldBroadcastNotificationDispatcher.enqueue((HelloWorldBroadcastNotification)message.getMessage());
				break;
	
			case MulticastDIPRPMessageType.HELLO_WORLD_ANYCAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldAnycastNotificationDispatcher.isReady())
				{
					super.execute(this.helloWorldAnycastNotificationDispatcher);
				}
				this.helloWorldAnycastNotificationDispatcher.enqueue((HelloWorldAnycastNotification)message.getMessage());
				break;
	
			case MulticastDIPRPMessageType.HELLO_WORLD_UNICAST_NOTIFICATION:
				System.out.println("HELLO_WORLD_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldUnicastNotificationDispatcher.isReady())
				{
					super.execute(this.helloWorldUnicastNotificationDispatcher);
				}
				this.helloWorldUnicastNotificationDispatcher.enqueue((HelloWorldUnicastNotification)message.getMessage());
				break;
				
			case MulticastDIPRPMessageType.HELLO_WORLD_BROADCAST_REQUEST:
				System.out.println("HELLO_WORLD_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldBroadcastRequestDispatcher.isReady())
				{
					super.execute(this.helloWorldBroadcastRequestDispatcher);
				}
				this.helloWorldBroadcastRequestDispatcher.enqueue((HelloWorldBroadcastRequest)message.getMessage());
				break;
				
			case MulticastDIPRPMessageType.HELLO_WORLD_ANYCAST_REQUEST:
				System.out.println("HELLO_WORLD_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldAnycastRequestDispatcher.isReady())
				{
					super.execute(this.helloWorldAnycastRequestDispatcher);
				}
				this.helloWorldAnycastRequestDispatcher.enqueue((HelloWorldAnycastRequest)message.getMessage());
				break;
				
			case MulticastDIPRPMessageType.HELLO_WORLD_UNICAST_REQUEST:
				System.out.println("HELLO_WORLD_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.helloWorldUnicastRequestDispatcher.isReady())
				{
					super.execute(this.helloWorldUnicastRequestDispatcher);
				}
				this.helloWorldUnicastRequestDispatcher.enqueue((HelloWorldUnicastRequest)message.getMessage());
				break;

			case SystemMessageType.RP_MULTICAST_RESPONSE:
				System.out.println("RP_MULTICAST_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.rpResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.rpResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldUnicastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.rpResponseDispatcher.enqueue((RPMulticastResponse)message.getMessage());
				break;

			case MulticastDIPRPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownBroadcastNotificationDispatcher);
				}
				this.shutdownBroadcastNotificationDispatcher.enqueue((ShutdownChildrenBroadcastNotification)message.getMessage());
				break;
		}
	}

}
