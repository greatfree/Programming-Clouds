package ca.streaming.news.child;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.MulticastDIPMessageType;
import org.greatfree.dip.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.dip.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.dip.streaming.message.StreamMessageType;
import org.greatfree.dip.streaming.message.SubscribeNotification;
import org.greatfree.dip.streaming.message.UnsubscribeNotification;
import org.greatfree.dip.streaming.unicast.child.SubscribeNotificationThread;
import org.greatfree.dip.streaming.unicast.child.SubscribeNotificationThreadCreator;
import org.greatfree.dip.streaming.unicast.child.UnsubscribeNotificationThread;
import org.greatfree.dip.streaming.unicast.child.UnsubscribeNotificationThreadCreator;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.server.ServerDispatcher;

import ca.streaming.news.message.CommentNotification;
import ca.streaming.news.message.JournalistPostNotification;
import ca.streaming.news.message.MicroblogNotification;
import ca.streaming.news.message.NewsDataType;
import ca.streaming.news.message.NewsFeedNotification;
import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/01/2020, Bing Li
class NewsChildDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> rootIPBroadcastNotificationDispatcher;

	private NotificationDispatcher<SubscribeNotification, SubscribeNotificationThread, SubscribeNotificationThreadCreator> subscribeNotificationDispatcher;

	private NotificationDispatcher<UnsubscribeNotification, UnsubscribeNotificationThread, UnsubscribeNotificationThreadCreator> unsubscribeNotificationDispatcher;

	private NotificationDispatcher<JournalistPostNotification, JournalistPostNotificationThread, JournalistPostNotificationThreadCreator> journalistNotificationDispatcher;

	private NotificationDispatcher<CommentNotification, CommentNotificationThread, CommentNotificationThreadCreator> commentNotificationDispatcher;

	private NotificationDispatcher<MicroblogNotification, MicroblogNotificationThread, MicroblogNotificationThreadCreator> microblogNotificationDispatcher;

	private NotificationDispatcher<NewsFeedNotification, NewsFeedNotificationThread, NewsFeedNotificationThreadCreator> newsFeedNotificationDispatcher;

	private NotificationDispatcher<VideoPieceNotification, VideoPieceNotificationThread, VideoPieceNotificationThreadCreator> videoPieceNotificationDispatcher;

	private NotificationDispatcher<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator> shutdownBroadcastNotificationDispatcher;

	public NewsChildDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.rootIPBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RootIPAddressBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.subscribeNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SubscribeNotification, SubscribeNotificationThread, SubscribeNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SubscribeNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.unsubscribeNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnsubscribeNotification, UnsubscribeNotificationThread, UnsubscribeNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new UnsubscribeNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

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
		
		this.shutdownBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ShutdownChildrenBroadcastNotificationThread, ShutdownChildrenBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenBroadcastNotificationThreadCreator())
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
		this.rootIPBroadcastNotificationDispatcher.dispose();
		this.subscribeNotificationDispatcher.dispose();
		this.unsubscribeNotificationDispatcher.dispose();
		this.journalistNotificationDispatcher.dispose();
		this.commentNotificationDispatcher.dispose();
		this.microblogNotificationDispatcher.dispose();
		this.newsFeedNotificationDispatcher.dispose();
		this.videoPieceNotificationDispatcher.dispose();
		this.shutdownBroadcastNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				System.out.println("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.rootIPBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.rootIPBroadcastNotificationDispatcher);
				}
				this.rootIPBroadcastNotificationDispatcher.enqueue((RootIPAddressBroadcastNotification)message.getMessage());
				break;

			case StreamMessageType.SUBSCRIBE_NOTIFICATION:
				System.out.println("SUBSCRIBE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.subscribeNotificationDispatcher.isReady())
				{
					super.execute(this.subscribeNotificationDispatcher);
				}
				this.subscribeNotificationDispatcher.enqueue((SubscribeNotification)message.getMessage());
				break;

			case StreamMessageType.UNSUBSCRIBE_NOTIFICATION:
				System.out.println("UNSUBSCRIBE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.unsubscribeNotificationDispatcher.isReady())
				{
					super.execute(this.unsubscribeNotificationDispatcher);
				}
				this.unsubscribeNotificationDispatcher.enqueue((UnsubscribeNotification)message.getMessage());
				break;

				
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

				
			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownBroadcastNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownBroadcastNotificationDispatcher);
				}
				this.shutdownBroadcastNotificationDispatcher.enqueue((ShutdownChildrenBroadcastNotification)message.getMessage());
				break;
		}		
	}

}
