package ca.streaming.news.subscriber;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.streaming.message.SearchRequest;
import org.greatfree.dip.streaming.message.SearchResponse;
import org.greatfree.dip.streaming.message.SearchStream;
import org.greatfree.dip.streaming.message.StreamMessageType;
import org.greatfree.dip.streaming.subscriber.SearchRequestThread;
import org.greatfree.dip.streaming.subscriber.SearchRequestThreadCreator;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import ca.streaming.news.message.CommentNotification;
import ca.streaming.news.message.IsVideoExistedRequest;
import ca.streaming.news.message.IsVideoExistedResponse;
import ca.streaming.news.message.IsVideoExistedStream;
import ca.streaming.news.message.JournalistPostNotification;
import ca.streaming.news.message.MicroblogNotification;
import ca.streaming.news.message.NewsDataType;
import ca.streaming.news.message.NewsFeedNotification;
import ca.streaming.news.message.VideoPieceNotification;
import ca.streaming.news.message.VideoSearchRequest;
import ca.streaming.news.message.VideoSearchResponse;
import ca.streaming.news.message.VideoSearchStream;

// Created: 04/03/2020, Bing Li
class NewsSubscriberDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<JournalistPostNotification, JournalistPostNotificationThread, JournalistPostNotificationThreadCreator> journalistNotificationDispatcher;

	private NotificationDispatcher<CommentNotification, CommentNotificationThread, CommentNotificationThreadCreator> commentNotificationDispatcher;

	private NotificationDispatcher<MicroblogNotification, MicroblogNotificationThread, MicroblogNotificationThreadCreator> microblogNotificationDispatcher;

	private NotificationDispatcher<NewsFeedNotification, NewsFeedNotificationThread, NewsFeedNotificationThreadCreator> newsFeedNotificationDispatcher;

	private NotificationDispatcher<VideoPieceNotification, VideoPieceNotificationThread, VideoPieceNotificationThreadCreator> videoPieceNotificationDispatcher;

	private RequestDispatcher<SearchRequest, SearchStream, SearchResponse, SearchRequestThread, SearchRequestThreadCreator> searchRequestDispatcher;

	private RequestDispatcher<IsVideoExistedRequest, IsVideoExistedStream, IsVideoExistedResponse, IsVideoExistedRequestThread, IsVideoExistedRequestThreadCreator> isVideoExistedRequestDispatcher;

	private RequestDispatcher<VideoSearchRequest, VideoSearchStream, VideoSearchResponse, VideoSearchRequestThread, VideoSearchRequestThreadCreator> videoSearchRequestDispatcher;

	public NewsSubscriberDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.journalistNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<JournalistPostNotification, JournalistPostNotificationThread, JournalistPostNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new JournalistPostNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.commentNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CommentNotification, CommentNotificationThread, CommentNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new CommentNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.microblogNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<MicroblogNotification, MicroblogNotificationThread, MicroblogNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new MicroblogNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.newsFeedNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<NewsFeedNotification, NewsFeedNotificationThread, NewsFeedNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new NewsFeedNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.videoPieceNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<VideoPieceNotification, VideoPieceNotificationThread, VideoPieceNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new VideoPieceNotificationThreadCreator())
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

		this.isVideoExistedRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsVideoExistedRequest, IsVideoExistedStream, IsVideoExistedResponse, IsVideoExistedRequestThread, IsVideoExistedRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IsVideoExistedRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.videoSearchRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<VideoSearchRequest, VideoSearchStream, VideoSearchResponse, VideoSearchRequestThread, VideoSearchRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new VideoSearchRequestThreadCreator())
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
		this.journalistNotificationDispatcher.dispose();
		this.commentNotificationDispatcher.dispose();
		this.microblogNotificationDispatcher.dispose();
		this.newsFeedNotificationDispatcher.dispose();
		this.videoPieceNotificationDispatcher.dispose();
		this.searchRequestDispatcher.dispose();
		this.isVideoExistedRequestDispatcher.dispose();
		this.videoSearchRequestDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case NewsDataType.JOURNALIST_POST_NOTIFICATION:
				System.out.println("JOURNALIST_POST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.journalistNotificationDispatcher.isReady())
				{
					super.execute(this.journalistNotificationDispatcher);
				}
				this.journalistNotificationDispatcher.enqueue((JournalistPostNotification)message.getMessage());
				break;
				
			case NewsDataType.COMMENT_NOTIFICATION:
				System.out.println("COMMENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.commentNotificationDispatcher.isReady())
				{
					super.execute(this.commentNotificationDispatcher);
				}
				this.commentNotificationDispatcher.enqueue((CommentNotification)message.getMessage());
				break;
				
			case NewsDataType.MICROBLOG_NOTIFICATION:
				System.out.println("MICROBLOG_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.microblogNotificationDispatcher.isReady())
				{
					super.execute(this.microblogNotificationDispatcher);
				}
				this.microblogNotificationDispatcher.enqueue((MicroblogNotification)message.getMessage());
				break;
				
			case NewsDataType.NEWS_FEED_NOTIFICATION:
				System.out.println("NEWS_FEED_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.newsFeedNotificationDispatcher.isReady())
				{
					super.execute(this.newsFeedNotificationDispatcher);
				}
				this.newsFeedNotificationDispatcher.enqueue((NewsFeedNotification)message.getMessage());
				break;
				
			case NewsDataType.VIDEO_PIECE_NOTIFICATION:
				System.out.println("VIDEO_PIECE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.videoPieceNotificationDispatcher.isReady())
				{
					super.execute(this.videoPieceNotificationDispatcher);
				}
				this.videoPieceNotificationDispatcher.enqueue((VideoPieceNotification)message.getMessage());
				break;

			case StreamMessageType.SEARCH_REQUEST:
				System.out.println("SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.searchRequestDispatcher.isReady())
				{
					super.execute(this.searchRequestDispatcher);
				}
				this.searchRequestDispatcher.enqueue(new SearchStream(message.getOutStream(), message.getLock(), (SearchRequest)message.getMessage()));
				break;

			case NewsDataType.IS_VIDEO_EXISTED_REQUEST:
				System.out.println("IS_VIDEO_EXISTED_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.isVideoExistedRequestDispatcher.isReady())
				{
					super.execute(this.isVideoExistedRequestDispatcher);
				}
				this.isVideoExistedRequestDispatcher.enqueue(new IsVideoExistedStream(message.getOutStream(), message.getLock(), (IsVideoExistedRequest)message.getMessage()));
				break;
				
			case NewsDataType.VIDEO_SEARCH_REQUEST:
				System.out.println("VIDEO_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.videoSearchRequestDispatcher.isReady())
				{
					super.execute(this.videoSearchRequestDispatcher);
				}
				this.videoSearchRequestDispatcher.enqueue(new VideoSearchStream(message.getOutStream(), message.getLock(), (VideoSearchRequest)message.getMessage()));
				break;

		}
	}

}
