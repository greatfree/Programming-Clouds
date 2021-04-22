package org.greatfree.cluster.child;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.server.ServerDispatcher;

// Created: 09/23/2018, Bing Li
class ChildDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> rootIPBroadcastNotificationDispatcher;

	private NotificationDispatcher<Notification, ChildNotificationThread, ChildNotificationThreadCreator> notificationDispatcher;
	
	private NotificationDispatcher<Request, ChildRequestThread, ChildRequestThreadCreator> requestDispatcher;

//	public ChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, ChatNotification
		this.rootIPBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootIPAddressBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, ChildNotificationThread, ChildNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.requestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Request, ChildRequestThread, ChildRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ChildRequestThreadCreator())
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
		super.shutdown(timeout);
		this.rootIPBroadcastNotificationDispatcher.dispose();
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				System.out.println("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.rootIPBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.rootIPBroadcastNotificationDispatcher);
				}
				this.rootIPBroadcastNotificationDispatcher.enqueue((RootIPAddressBroadcastNotification)message.getMessage());
				break;
				
			case MulticastMessageType.NOTIFICATION:
				System.out.println("NOTIFICATION received at " + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((Notification)message.getMessage());
				break;
				
			case MulticastMessageType.REQUEST:
				System.out.println("REQUEST received at " + Calendar.getInstance().getTime());
				if (!this.requestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.requestDispatcher.enqueue((Request)message.getMessage());
				break;
		}
	}

}
