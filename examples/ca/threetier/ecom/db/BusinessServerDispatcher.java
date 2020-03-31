package ca.threetier.ecom.db;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.MerchandiseStream;
import ca.dp.tncs.message.PlaceOrderNotification;
import ca.dp.tncs.message.TNCSConfig;

// Created: 02/22/2020, Bing Li
class BusinessServerDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator> requestDispatcher;
	
	private NotificationDispatcher<PlaceOrderNotification, PlaceOrderNotificationThread, PlaceOrderNotificationThreadCreator> notificationDispatcher;

	public BusinessServerDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

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

		this.notificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PlaceOrderNotification, PlaceOrderNotificationThread, PlaceOrderNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PlaceOrderNotificationThreadCreator())
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
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case TNCSConfig.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.requestDispatcher.isReady())
				{
					super.execute(this.requestDispatcher);
				}
				this.requestDispatcher.enqueue(new MerchandiseStream(message.getOutStream(), message.getLock(), (MerchandiseRequest)message.getMessage()));
				break;
				
			case TNCSConfig.PLACE_ORDER_NOTIFICATION:
				System.out.println("PLACE_ORDER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.notificationDispatcher.isReady())
				{
					super.execute(this.notificationDispatcher);
				}
				this.notificationDispatcher.enqueue((PlaceOrderNotification)message.getMessage());
				break;
		
		}
	}

}
