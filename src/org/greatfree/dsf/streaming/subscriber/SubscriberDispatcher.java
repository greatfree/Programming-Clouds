package org.greatfree.dsf.streaming.subscriber;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.streaming.message.SearchRequest;
import org.greatfree.dsf.streaming.message.SearchResponse;
import org.greatfree.dsf.streaming.message.SearchStream;
import org.greatfree.dsf.streaming.message.StreamMessageType;
import org.greatfree.dsf.streaming.message.StreamNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 03/19/2020, Bing Li
class SubscriberDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<StreamNotification, StreamNotificationThread, StreamNotificationThreadCreator> streamNotificationDispatcher;

	private RequestDispatcher<SearchRequest, SearchStream, SearchResponse, SearchRequestThread, SearchRequestThreadCreator> searchRequestDispatcher;

	public SubscriberDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

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

		this.searchRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SearchRequest, SearchStream, SearchResponse, SearchRequestThread, SearchRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchRequestThreadCreator())
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
		this.streamNotificationDispatcher.dispose();
		this.searchRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case StreamMessageType.STREAM_NOTIFICATION:
				System.out.println("STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.streamNotificationDispatcher.isReady())
				{
					super.execute(this.streamNotificationDispatcher);
				}
				this.streamNotificationDispatcher.enqueue((StreamNotification)message.getMessage());
				break;
				
			case StreamMessageType.SEARCH_REQUEST:
				System.out.println("SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.searchRequestDispatcher.isReady())
				{
					super.execute(this.searchRequestDispatcher);
				}
				this.searchRequestDispatcher.enqueue(new SearchStream(message.getOutStream(), message.getLock(), (SearchRequest)message.getMessage()));
				break;
		}
	}

}
