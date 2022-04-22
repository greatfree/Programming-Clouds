package org.greatfree.app.cps.terminal;

import java.util.Calendar;

import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 08/14/2018, Bing Li
public class ManDBDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<ShutdownServerNotification, ShutdownDBServerThread, ShutdownDBServerThreadCreator> shutdownNotificationDispatcher;

//	public ManDBDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ManDBDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownDBServerThread, ShutdownDBServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownDBServerThreadCreator())
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
		this.shutdownNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 04/17/2017, Bing Li
		switch (message.getMessage().getType())
		{	
			case SystemMessageType.SHUTDOWN_SERVER_NOTIFICATION:
					System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
					// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
					if (!this.shutdownNotificationDispatcher.isReady())
					{
						// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
						super.execute(this.shutdownNotificationDispatcher);
					}
					// Enqueue the instance of ShutdownChatServerNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
					this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
					break;
		}
	}

}
