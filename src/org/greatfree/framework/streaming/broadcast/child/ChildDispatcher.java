package org.greatfree.framework.streaming.broadcast.child;

import java.util.Calendar;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.framework.streaming.message.StreamMessageType;
import org.greatfree.framework.streaming.message.StreamNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.RootAddressNotification;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 03/19/2020, Bing Li
class ChildDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<RootAddressNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> rootIPBroadcastNotificationDispatcher;

	private NotificationDispatcher<StreamNotification, StreamNotificationThread, StreamNotificationThreadCreator> streamNotificationDispatcher;
	
	private NotificationDispatcher<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator> shutdownBroadcastNotificationDispatcher;

	public ChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.rootIPBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RootAddressNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootIPAddressBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.streamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<StreamNotification, StreamNotificationThread, StreamNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new StreamNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.shutdownBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
		this.streamNotificationDispatcher.dispose();
		this.shutdownBroadcastNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				System.out.println("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.rootIPBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.rootIPBroadcastNotificationDispatcher);
				}
				this.rootIPBroadcastNotificationDispatcher.enqueue((RootAddressNotification)message.getMessage());
				break;

			case StreamMessageType.STREAM_NOTIFICATION:
				System.out.println("STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.streamNotificationDispatcher.isReady())
				{
					super.execute(this.streamNotificationDispatcher);
				}
				this.streamNotificationDispatcher.enqueue((StreamNotification)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION:
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
