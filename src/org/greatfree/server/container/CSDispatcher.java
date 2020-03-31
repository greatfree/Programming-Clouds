package org.greatfree.server.container;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.container.RequestStream;
import org.greatfree.server.ServerDispatcher;

// Created: 12/18/2018, Bing Li
class CSDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<Notification, NotificationThread, NotificationThreadCreator> notificationDispatcher;
	private RequestDispatcher<Request, RequestStream, ServerMessage, RequestThread, RequestThreadCreator> requestDispatcher;

//	public CSDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public CSDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
	}
	
	public void init()
	{
		if (ServerProfile.CS().isDefault())
		{
			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, NotificationThread, NotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
					.threadCreator(new NotificationThreadCreator())
					.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();

			System.out.println("CSDispatcher-Constructor(): server key = " + super.getServerKey());
			
			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<Request, RequestStream, ServerMessage, RequestThread, RequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
					.threadCreator(new RequestThreadCreator())
					.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
					.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
					.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
					.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
					.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
					.scheduler(super.getScheduler())
					.build();
		}
		else
		{
			this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<Notification, NotificationThread, NotificationThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getNotificationDispatcherPoolSize())
					.threadCreator(new NotificationThreadCreator())
					.notificationQueueSize(ServerProfile.CS().getNotificationQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getNotificationDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getNotificationDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getNotificationDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();

			this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<Request, RequestStream, ServerMessage, RequestThread, RequestThreadCreator>()
					.serverKey(super.getServerKey())
					.poolSize(ServerProfile.CS().getRequestDispatcherPoolSize())
					.threadCreator(new RequestThreadCreator())
					.requestQueueSize(ServerProfile.CS().getRequestQueueSize())
					.dispatcherWaitTime(ServerProfile.CS().getRequestDispatcherWaitTime())
					.waitRound(ServerProfile.CS().getNotificationDispatcherWaitRound())
					.idleCheckDelay(ServerProfile.CS().getRequestDispatcherIdleCheckDelay())
					.idleCheckPeriod(ServerProfile.CS().getRequestDispatcherIdleCheckPeriod())
					.scheduler(super.getScheduler())
					.build();
		}
	}

	/*
	 * The below comment is NOT correct. 11/17/2019, Bing Li
	 * 
	 * The keyword, synchronized, is required. When the server dispatcher is being shutdown, no incoming messages should be processed. Otherwise, the system cannot be closed normally because the notification dispatcher or the request dispatcher is still alive for incoming messages. 11/17/2019, Bing Li
	 */
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// The below line should be executed first such that incoming messages are not processed any longer. Otherwise, the system cannot be closed normally because the notification dispatcher or the request dispatcher is still alive for incoming messages. 11/17/2019, Bing Li
		super.shutdown(timeout);
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
	}

	/*
	 * The below comment is NOT correct. 11/17/2019, Bing Li
	 * 
	 * The keyword, synchronized, is required. When the server dispatcher is being shutdown, no incoming messages should be processed. Otherwise, the system cannot be closed normally because the notification dispatcher or the request dispatcher is still alive for incoming messages. 11/17/2019, Bing Li
	 */
	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CSMessageType.NOTIFICATION:
				System.out.println("NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of Notification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((Notification)message.getMessage());
				break;
				
			case CSMessageType.REQUEST:
				System.out.println("REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.requestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the instance of Request into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.requestDispatcher.enqueue(new RequestStream(message.getOutStream(), message.getLock(), (Request)message.getMessage()));
				break;
		}
	}

}
