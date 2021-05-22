package org.greatfree.app.cps.coordinator;

import java.util.Calendar;

import org.greatfree.app.cps.message.BusinessMessageType;
import org.greatfree.app.cps.message.MerchandiseRequest;
import org.greatfree.app.cps.message.MerchandiseResponse;
import org.greatfree.app.cps.message.MerchandiseStream;
import org.greatfree.app.cps.message.OrderNotification;
import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 08/14/2018, Bing Li
public class VendorCoordinatorDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<OrderNotification, OrderNotificationThread, OrderNotificationThreadCreator> notificationDispatcher;
	private RequestDispatcher<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator> requestDispatcher;

//	public VendorCoordinatorDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public VendorCoordinatorDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<OrderNotification, OrderNotificationThread, OrderNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new OrderNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.requestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MerchandiseRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.notificationDispatcher.dispose();
		this.requestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case BusinessMessageType.ORDER_NOTIFICATION:
				System.out.println("ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.notificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.notificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.notificationDispatcher.enqueue((OrderNotification)message.getMessage());
				break;

			case BusinessMessageType.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.requestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.requestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.requestDispatcher.enqueue(new MerchandiseStream(message.getOutStream(), message.getLock(), (MerchandiseRequest)message.getMessage()));
				break;
		}
	}

}
