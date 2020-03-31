package ca.dp.mncs.circle.server;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import ca.dp.mncs.circle.message.CircleConfig;
import ca.dp.mncs.circle.message.LikeNotification;
import ca.dp.mncs.circle.message.PollLikeRequest;
import ca.dp.mncs.circle.message.PollLikeResponse;
import ca.dp.mncs.circle.message.PollLikeStream;
import ca.dp.mncs.circle.message.PollPostRequest;
import ca.dp.mncs.circle.message.PollPostResponse;
import ca.dp.mncs.circle.message.PollPostStream;
import ca.dp.mncs.circle.message.PostRequest;
import ca.dp.mncs.circle.message.PostResponse;
import ca.dp.mncs.circle.message.PostStream;

// Created: 02/25/2020, Bing Li
class CircleDispatcher extends ServerDispatcher<ServerMessage>
{
	private RequestDispatcher<PostRequest, PostStream, PostResponse, PostRequestThread, PostRequestThreadCreator> postRequestDispatcher;
	private NotificationDispatcher<LikeNotification, LikeNotificationThread, LikeNotificationThreadCreator> likeNotificationDispatcher;

	private RequestDispatcher<PollPostRequest, PollPostStream, PollPostResponse, PollPostRequestThread, PollPostRequestThreadCreator> pollPostRequestDispatcher;
	private RequestDispatcher<PollLikeRequest, PollLikeStream, PollLikeResponse, PollLikeRequestThread, PollLikeRequestThreadCreator> pollLikeRequestDispatcher;

	public CircleDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.postRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostRequest, PostStream, PostResponse, PostRequestThread, PostRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.likeNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<LikeNotification, LikeNotificationThread, LikeNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new LikeNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pollPostRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PollPostRequest, PollPostStream, PollPostResponse, PollPostRequestThread, PollPostRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PollPostRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pollLikeRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PollLikeRequest, PollLikeStream, PollLikeResponse, PollLikeRequestThread, PollLikeRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PollLikeRequestThreadCreator())
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
		this.postRequestDispatcher.dispose();
		this.likeNotificationDispatcher.dispose();
		this.pollPostRequestDispatcher.dispose();
		this.pollLikeRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CircleConfig.POST_REQUEST:
				System.out.println("POST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.postRequestDispatcher.isReady())
				{
					super.execute(this.postRequestDispatcher);
				}
				this.postRequestDispatcher.enqueue(new PostStream(message.getOutStream(), message.getLock(), (PostRequest)message.getMessage()));
				break;
				
			case CircleConfig.LIKE_NOTIFICATION:
				System.out.println("LIKE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.likeNotificationDispatcher.isReady())
				{
					super.execute(this.likeNotificationDispatcher);
				}
				this.likeNotificationDispatcher.enqueue((LikeNotification)message.getMessage());
				break;

			case CircleConfig.POLL_POST_REQUEST:
				System.out.println("POLL_POST_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.pollPostRequestDispatcher.isReady())
				{
					super.execute(this.pollPostRequestDispatcher);
				}
				this.pollPostRequestDispatcher.enqueue(new PollPostStream(message.getOutStream(), message.getLock(), (PollPostRequest)message.getMessage()));
				break;

			case CircleConfig.POLL_LIKE_REQUEST:
				System.out.println("POLL_LIKE_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.pollLikeRequestDispatcher.isReady())
				{
					super.execute(this.pollLikeRequestDispatcher);
				}
				this.pollLikeRequestDispatcher.enqueue(new PollLikeStream(message.getOutStream(), message.getLock(), (PollLikeRequest)message.getMessage()));
				break;
		}
		
	}

}
