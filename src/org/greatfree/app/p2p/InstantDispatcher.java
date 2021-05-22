package org.greatfree.app.p2p;

import java.util.Calendar;

import org.greatfree.app.p2p.message.GreetingRequest;
import org.greatfree.app.p2p.message.GreetingResponse;
import org.greatfree.app.p2p.message.GreetingStream;
import org.greatfree.app.p2p.message.HelloNotification;
import org.greatfree.app.p2p.message.P2PMessageType;
import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 08/19/2018, Bing Li
class InstantDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<GreetingRequest, GreetingStream, GreetingResponse, GreetingRequestThread, GreetingRequestThreadCreator> requestDispatcher;
	private NotificationDispatcher<HelloNotification, HelloNotificationThread, HelloNotificationThreadCreator> notificationDispatcher;

//	public InstantDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public InstantDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<GreetingRequest, GreetingStream, GreetingResponse, GreetingRequestThread, GreetingRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new GreetingRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<HelloNotification, HelloNotificationThread, HelloNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new HelloNotificationThreadCreator())
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
		this.requestDispatcher.dispose();
		this.notificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case P2PMessageType.GREETING_REQUEST:
				System.out.println("GREETING_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.requestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.requestDispatcher.enqueue(new GreetingStream(message.getOutStream(), message.getLock(), (GreetingRequest)message.getMessage()));
				break;
				
			case P2PMessageType.HELLO_NOTIFICATION:
				System.out.println("HELLO_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((HelloNotification)message.getMessage());
				break;
			
		}
	}

}
