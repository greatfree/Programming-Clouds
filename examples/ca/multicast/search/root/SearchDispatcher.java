package ca.multicast.search.root;

import java.util.Calendar;

import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.MulticastDIPMessageType;
import org.greatfree.dip.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

import ca.multicast.search.message.ClientSearchQueryRequest;
import ca.multicast.search.message.ClientSearchQueryResponse;
import ca.multicast.search.message.ClientSearchQueryStream;
import ca.multicast.search.message.CrawlDataNotification;
import ca.multicast.search.message.SearchConfig;
import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/14/2020, Bing Li
class SearchDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<CrawlDataNotification, CrawlDataNotificationThread, CrawlDataNotificationThreadCreator> crawlNotificationDispatcher;

	private RequestDispatcher<ClientSearchQueryRequest, ClientSearchQueryStream, ClientSearchQueryResponse, SearchQueryRequestThread, SearchQueryRequestThreadCreator> searchRequestDispatcher;
	
	private NotificationDispatcher<SearchQueryResponse, SearchQueryResponseThread, SearchQueryResponseThreadCreator> searchResponseDispatcher;

	private NotificationDispatcher<ShutdownChildrenAdminNotification, ShutdownChildrenNotificationThread, ShutdownChildrenNotificationThreadCreator> shutdownChildrenNotificationDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownRootThread, ShutdownRootThreadCreator> shutdownRootNotificationDispatcher;

	public SearchDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(serverThreadPoolSize, serverThreadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		this.crawlNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CrawlDataNotification, CrawlDataNotificationThread, CrawlDataNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new CrawlDataNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.searchRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientSearchQueryRequest, ClientSearchQueryStream, ClientSearchQueryResponse, SearchQueryRequestThread, SearchQueryRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchQueryRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.searchResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SearchQueryResponse, SearchQueryResponseThread, SearchQueryResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchQueryResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownChildrenNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenAdminNotification, ShutdownChildrenNotificationThread, ShutdownChildrenNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownChildrenNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownRootNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownRootThread, ShutdownRootThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownRootThreadCreator())
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
		this.crawlNotificationDispatcher.dispose();
		this.searchRequestDispatcher.dispose();
		this.searchResponseDispatcher.dispose();
		this.shutdownChildrenNotificationDispatcher.dispose();
		this.shutdownRootNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case SearchConfig.CRAWL_DATA_NOTIFICATION:
				System.out.println("CRAWL_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.crawlNotificationDispatcher.isReady())
				{
					super.execute(this.crawlNotificationDispatcher);
				}
				this.crawlNotificationDispatcher.enqueue((CrawlDataNotification)message.getMessage());
				break;
				
			case SearchConfig.CLIENT_SEARCH_QUERY_REQUEST:
				System.out.println("CLIENT_SEARCH_QUERY_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.searchRequestDispatcher.isReady())
				{
					super.execute(this.searchRequestDispatcher);
				}
				this.searchRequestDispatcher.enqueue(new ClientSearchQueryStream(message.getOutStream(), message.getLock(), (ClientSearchQueryRequest)message.getMessage()));
				break;
				
			case SearchConfig.SEARCH_QUERY_RESPONSE:
				System.out.println("SEARCH_QUERY_RESPONSE received @" + Calendar.getInstance().getTime());
				if (!this.searchResponseDispatcher.isReady())
				{
					super.execute(this.searchResponseDispatcher);
				}
				this.searchResponseDispatcher.enqueue((SearchQueryResponse)message.getMessage());
				break;

			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownChildrenNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownChildrenNotificationDispatcher);
				}
				this.shutdownChildrenNotificationDispatcher.enqueue((ShutdownChildrenAdminNotification)message.getMessage());
				break;
				
				
			case ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.shutdownRootNotificationDispatcher.isReady())
				{
					super.execute(this.shutdownRootNotificationDispatcher);
				}
				this.shutdownRootNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
		
	}

}
