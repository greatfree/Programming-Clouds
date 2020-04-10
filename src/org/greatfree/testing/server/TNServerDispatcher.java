package org.greatfree.testing.server;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.TNNotification;

// Created: 04/10/2020, Bing Li
class TNServerDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<TNNotification, TNNotificationThread, TNNotificationThreadCreator> tnNotificationDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownServerNotificationThread, ShutdownServerNotificationThreadCreator> shutdownNotificationDispatcher;

	public TNServerDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.tnNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<TNNotification, TNNotificationThread, TNNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new TNNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownServerNotificationThread, ShutdownServerNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownServerNotificationThreadCreator())
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
		this.tnNotificationDispatcher.dispose();
		this.shutdownNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MessageType.TN_NOTIFICATION:
				System.out.println("TN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.tnNotificationDispatcher.isReady())
				{
					System.out.println("tnNotificationDispatcher is excuted ...");
					super.execute(this.tnNotificationDispatcher);
				}
				this.tnNotificationDispatcher.enqueue((TNNotification)message.getMessage());
				break;

			case ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					System.out.println("tnNotificationDispatcher is excuted ...");
					super.execute(this.shutdownNotificationDispatcher);
				}
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}

}
