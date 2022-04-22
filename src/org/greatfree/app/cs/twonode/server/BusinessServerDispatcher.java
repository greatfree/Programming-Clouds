package org.greatfree.app.cs.twonode.server;

import java.util.Calendar;

import org.greatfree.app.cs.twonode.message.BusinessMessageType;
import org.greatfree.app.cs.twonode.message.MerchandisePollRequest;
import org.greatfree.app.cs.twonode.message.MerchandisePollResponse;
import org.greatfree.app.cs.twonode.message.MerchandisePollStream;
import org.greatfree.app.cs.twonode.message.MerchandiseRequest;
import org.greatfree.app.cs.twonode.message.MerchandiseResponse;
import org.greatfree.app.cs.twonode.message.MerchandiseStream;
import org.greatfree.app.cs.twonode.message.OrderDecisionNotification;
import org.greatfree.app.cs.twonode.message.PostMerchandiseNotification;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 07/25/2018, Bing Li
class BusinessServerDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator> mRequestDispatcher;
	private NotificationDispatcher<OrderDecisionNotification, OrderDecisionNotificationThread, OrderDecisionNotificationThreadCreator> oNotificationDispatcher;

	private NotificationDispatcher<PostMerchandiseNotification, PostMerchandiseNotificationThread, PostMerchandiseNotificationThreadCreator> postNotificationDispatcher;
	private RequestDispatcher<MerchandisePollRequest, MerchandisePollStream, MerchandisePollResponse, MerchandisePollRequestThread, MerchandisePollRequestThreadCreator> pollRequestDispatcher;

//	public BusinessServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public BusinessServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.mRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MerchandiseRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.oNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<OrderDecisionNotification, OrderDecisionNotificationThread, OrderDecisionNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new OrderDecisionNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.postNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PostMerchandiseNotification, PostMerchandiseNotificationThread, PostMerchandiseNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostMerchandiseNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pollRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MerchandisePollRequest, MerchandisePollStream, MerchandisePollResponse, MerchandisePollRequestThread, MerchandisePollRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MerchandisePollRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	/*
	public void shutdown(long timeout) throws InterruptedException
	{
		this.mRequestDispatcher.dispose();
		this.oNotificationDispatcher.dispose();
		this.postNotificationDispatcher.dispose();
		this.pollRequestDispatcher.dispose();
		super.shutdown(timeout);
	}
	
	public void consume(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case BusinessMessageType.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.mRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.mRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.mRequestDispatcher.enqueue(new MerchandiseStream(message.getOutStream(), message.getLock(), (MerchandiseRequest)message.getMessage()));
				break;
				
			case BusinessMessageType.ORDER_DECISION_NOTIFICATION:
				System.out.println("ORDER_DECISION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.oNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.oNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.oNotificationDispatcher.enqueue((OrderDecisionNotification)message.getMessage());
				break;
				
			case BusinessMessageType.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.postNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.postNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.postNotificationDispatcher.enqueue((PostMerchandiseNotification)message.getMessage());
				break;
				
			case BusinessMessageType.MERCHANDISE_POLL_REQUEST:
				System.out.println("MERCHANDISE_POLL_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.pollRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.pollRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.pollRequestDispatcher.enqueue(new MerchandisePollStream(message.getOutStream(), message.getLock(), (MerchandisePollRequest)message.getMessage()));
				break;
		}
	}
	*/

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.mRequestDispatcher.dispose();
		this.oNotificationDispatcher.dispose();
		this.postNotificationDispatcher.dispose();
		this.pollRequestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case BusinessMessageType.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.mRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.mRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.mRequestDispatcher.enqueue(new MerchandiseStream(message.getOutStream(), message.getLock(), (MerchandiseRequest)message.getMessage()));
				break;
				
			case BusinessMessageType.ORDER_DECISION_NOTIFICATION:
				System.out.println("ORDER_DECISION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.oNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.oNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.oNotificationDispatcher.enqueue((OrderDecisionNotification)message.getMessage());
				break;
				
			case BusinessMessageType.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.postNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.postNotificationDispatcher);
				}
				// Enqueue the instance of ChatNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.postNotificationDispatcher.enqueue((PostMerchandiseNotification)message.getMessage());
				break;
				
			case BusinessMessageType.MERCHANDISE_POLL_REQUEST:
				System.out.println("MERCHANDISE_POLL_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.pollRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.pollRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.pollRequestDispatcher.enqueue(new MerchandisePollStream(message.getOutStream(), message.getLock(), (MerchandisePollRequest)message.getMessage()));
				break;
		}
	}
}
