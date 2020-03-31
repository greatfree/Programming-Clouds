package edu.greatfree.tncs.server;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import edu.greatfree.tncs.message.MerchandiseRequest;
import edu.greatfree.tncs.message.MerchandiseResponse;
import edu.greatfree.tncs.message.MerchandiseStream;
import edu.greatfree.tncs.message.PostMerchandiseNotification;
import edu.greatfree.tncs.message.ECommerceMessageType;

// Created: 05/01/2019, Bing Li
class ECommerceDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator> merchandiseDispatcher;
	
	private NotificationDispatcher<PostMerchandiseNotification, PostMerchandiseNotificationThread, PostMerchandiseNotificationThreadCreator> postMCDDispatcher;

	public ECommerceDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.merchandiseDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MerchandiseRequest, MerchandiseStream, MerchandiseResponse, MerchandiseRequestThread, MerchandiseRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MerchandiseRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postMCDDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PostMerchandiseNotification, PostMerchandiseNotificationThread, PostMerchandiseNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostMerchandiseNotificationThreadCreator())
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
		this.merchandiseDispatcher.dispose();
		this.postMCDDispatcher.dispose();
		super.shutdown(timeout);
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case ECommerceMessageType.MERCHANDISE_REQUEST:
				System.out.println("MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.merchandiseDispatcher.isReady())
				{
					super.execute(this.merchandiseDispatcher);
				}
				this.merchandiseDispatcher.enqueue(new MerchandiseStream(message.getOutStream(), message.getLock(), (MerchandiseRequest)message.getMessage()));
				break;
				
			case ECommerceMessageType.POST_MERCHANDISE_NOTIFICATION:
				System.out.println("POST_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.postMCDDispatcher.isReady())
				{
					super.execute(this.postMCDDispatcher);
				}
				this.postMCDDispatcher.enqueue((PostMerchandiseNotification)message.getMessage());
				break;
		}
	}
}
