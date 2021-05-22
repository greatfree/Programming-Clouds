package org.greatfree.framework.streaming.broadcast.pubsub;

import java.util.Calendar;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.streaming.message.AddStreamNotification;
import org.greatfree.framework.streaming.message.OutStream;
import org.greatfree.framework.streaming.message.RemoveStreamNotification;
import org.greatfree.framework.streaming.message.ShutdownPubSubNotification;
import org.greatfree.framework.streaming.message.StreamMessageType;
import org.greatfree.framework.streaming.message.StreamRequest;
import org.greatfree.framework.streaming.message.StreamResponse;
import org.greatfree.framework.streaming.message.SubscribeOutStream;
import org.greatfree.framework.streaming.message.SubscribeStreamRequest;
import org.greatfree.framework.streaming.message.SubscribeStreamResponse;
import org.greatfree.framework.streaming.message.SubscribersRequest;
import org.greatfree.framework.streaming.message.SubscribersResponse;
import org.greatfree.framework.streaming.message.SubscribersStream;
import org.greatfree.framework.streaming.message.UnsubscribeStreamNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 03/18/2020, Bing Li
class PubSubDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<AddStreamNotification, AddStreamNotificationThread, AddStreamNotificationThreadCreator> addStreamNotificationDispatcher;

	private NotificationDispatcher<RemoveStreamNotification, RemoveStreamNotificationThread, RemoveStreamNotificationThreadCreator> removeStreamNotificationDispatcher;
	
	private RequestDispatcher<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread, SubscribeStreamRequestThreadCreator> subscribeRequestDispatcher;

	private NotificationDispatcher<UnsubscribeStreamNotification, UnsubscribeStreamNotificationThread, UnsubscribeStreamNotificationThreadCreator> unsubscribeStreamNotificationDispatcher;

	private RequestDispatcher<SubscribersRequest, SubscribersStream, SubscribersResponse, SubscribersRequestThread, SubscribersRequestThreadCreator> subscribersRequestDispatcher;

	private RequestDispatcher<StreamRequest, OutStream, StreamResponse, StreamRequestThread, StreamRequestThreadCreator> streamRequestDispatcher;

	private NotificationDispatcher<ShutdownPubSubNotification, ShutdownPubSubNotificationThread, ShutdownPubSubNotificationThreadCreator> shutdownNotificationDispatcher;

	public PubSubDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		
		this.addStreamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AddStreamNotification, AddStreamNotificationThread, AddStreamNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new AddStreamNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.removeStreamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RemoveStreamNotification, RemoveStreamNotificationThread, RemoveStreamNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RemoveStreamNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.subscribeRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SubscribeStreamRequest, SubscribeOutStream, SubscribeStreamResponse, SubscribeStreamRequestThread, SubscribeStreamRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new SubscribeStreamRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.unsubscribeStreamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnsubscribeStreamNotification, UnsubscribeStreamNotificationThread, UnsubscribeStreamNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new UnsubscribeStreamNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.subscribersRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SubscribersRequest, SubscribersStream, SubscribersResponse, SubscribersRequestThread, SubscribersRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new SubscribersRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.streamRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<StreamRequest, OutStream, StreamResponse, StreamRequestThread, StreamRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new StreamRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownPubSubNotification, ShutdownPubSubNotificationThread, ShutdownPubSubNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownPubSubNotificationThreadCreator())
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
		this.addStreamNotificationDispatcher.dispose();
		this.removeStreamNotificationDispatcher.dispose();
		this.subscribeRequestDispatcher.dispose();
		this.unsubscribeStreamNotificationDispatcher.dispose();
		this.subscribersRequestDispatcher.dispose();
		this.streamRequestDispatcher.dispose();
		this.shutdownNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case StreamMessageType.ADD_STREAM_NOTIFICATION:
				System.out.println("ADD_STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.addStreamNotificationDispatcher.isReady())
				{
					super.execute(this.addStreamNotificationDispatcher);
				}
				this.addStreamNotificationDispatcher.enqueue((AddStreamNotification)message.getMessage());
				break;

			case StreamMessageType.REMOVE_STREAM_NOTIFICATION:
				System.out.println("REMOVE_STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.removeStreamNotificationDispatcher.isReady())
				{
					super.execute(this.removeStreamNotificationDispatcher);
				}
				this.removeStreamNotificationDispatcher.enqueue((RemoveStreamNotification)message.getMessage());
				break;
				
			case StreamMessageType.SUBSCRIBE_STREAM_REQUEST:
				System.out.println("SUBSCRIBE_STREAM_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.subscribeRequestDispatcher.isReady())
				{
					super.execute(this.subscribeRequestDispatcher);
				}
				this.subscribeRequestDispatcher.enqueue(new SubscribeOutStream(message.getOutStream(), message.getLock(), (SubscribeStreamRequest)message.getMessage()));
				break;

			case StreamMessageType.UNSUBSCRIBE_STREAM_NOTIFICATION:
				System.out.println("UNSUBSCRIBE_STREAM_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.unsubscribeStreamNotificationDispatcher.isReady())
				{
					super.execute(this.unsubscribeStreamNotificationDispatcher);
				}
				this.unsubscribeStreamNotificationDispatcher.enqueue((UnsubscribeStreamNotification)message.getMessage());
				break;
				
			case StreamMessageType.SUBSCRIBERS_REQUEST:
				System.out.println("SUBSCRIBERS_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.subscribersRequestDispatcher.isReady())
				{
					super.execute(this.subscribersRequestDispatcher);
				}
				this.subscribersRequestDispatcher.enqueue(new SubscribersStream(message.getOutStream(), message.getLock(), (SubscribersRequest)message.getMessage()));
				break;
				
			case StreamMessageType.STREAM_REQUEST:
				System.out.println("STREAM_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.streamRequestDispatcher.isReady())
				{
					super.execute(this.streamRequestDispatcher);
				}
				this.streamRequestDispatcher.enqueue(new OutStream(message.getOutStream(), message.getLock(), (StreamRequest)message.getMessage()));
				break;
				
			case StreamMessageType.SHUTDOWN_PUBSUB_NOTIFICATION:
				System.out.println("SHUTDOWN_PUBSUB_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownNotificationDispatcher);
				}
				this.shutdownNotificationDispatcher.enqueue((ShutdownPubSubNotification)message.getMessage());
				break;
		}
	}

}
