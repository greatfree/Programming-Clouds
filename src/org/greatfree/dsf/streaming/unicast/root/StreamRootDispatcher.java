package org.greatfree.dsf.streaming.unicast.root;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.multicast.message.MulticastDIPMessageType;
import org.greatfree.dsf.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.dsf.streaming.message.StreamMessageType;
import org.greatfree.dsf.streaming.message.StreamNotification;
import org.greatfree.dsf.streaming.message.SubscribeNotification;
import org.greatfree.dsf.streaming.message.UnsubscribeNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 03/22/2020, Bing Li
class StreamRootDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<SubscribeNotification, SubscribeNotificationThread, SubscribeNotificationThreadCreator> subscribeNotificationDispatcher;

	private NotificationDispatcher<UnsubscribeNotification, UnsubscribeNotificationThread, UnsubscribeNotificationThreadCreator> unsubscribeNotificationDispatcher;

	private NotificationDispatcher<StreamNotification, StreamNotificationThread, StreamNotificationThreadCreator> streamNotificationDispatcher;

	private NotificationDispatcher<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator> shutdownChildrenNotificationDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownServerThread, ShutdownServerThreadCreator> shutdownServerNotificationDispatcher;

	public StreamRootDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.subscribeNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SubscribeNotification, SubscribeNotificationThread, SubscribeNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SubscribeNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.unsubscribeNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnsubscribeNotification, UnsubscribeNotificationThread, UnsubscribeNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new UnsubscribeNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.streamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<StreamNotification, StreamNotificationThread, StreamNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new StreamNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownChildrenNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenAdminNotification, ShutdownChildrenAdminNotificationThread, ShutdownChildrenAdminNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenAdminNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownServerThread, ShutdownServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownServerThreadCreator())
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
		this.subscribeNotificationDispatcher.dispose();
		this.unsubscribeNotificationDispatcher.dispose();
		this.streamNotificationDispatcher.dispose();
		this.shutdownChildrenNotificationDispatcher.dispose();
		this.shutdownServerNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case StreamMessageType.SUBSCRIBE_NOTIFICATION:
				System.out.println("SUBSCRIBE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.subscribeNotificationDispatcher.isReady())
				{
					super.execute(this.subscribeNotificationDispatcher);
				}
				this.subscribeNotificationDispatcher.enqueue((SubscribeNotification)message.getMessage());
				break;

			case StreamMessageType.UNSUBSCRIBE_NOTIFICATION:
				System.out.println("UNSUBSCRIBE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.unsubscribeNotificationDispatcher.isReady())
				{
					super.execute(this.unsubscribeNotificationDispatcher);
				}
				this.unsubscribeNotificationDispatcher.enqueue((UnsubscribeNotification)message.getMessage());
				break;

			case StreamMessageType.STREAM_NOTIFICATION:
				System.out.println("STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.streamNotificationDispatcher.isReady())
				{
					super.execute(this.streamNotificationDispatcher);
				}
				this.streamNotificationDispatcher.enqueue((StreamNotification)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownChildrenNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownChildrenNotificationDispatcher);
				}
				this.shutdownChildrenNotificationDispatcher.enqueue((ShutdownChildrenAdminNotification)message.getMessage());
				break;
				
			case ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownServerNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownServerNotificationDispatcher);
				}
				this.shutdownServerNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}

}
