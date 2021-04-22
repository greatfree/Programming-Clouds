package org.greatfree.app.search.dip.multicast.root.entry;

import java.util.Calendar;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.app.search.dip.multicast.message.SearchMessageType;
import org.greatfree.app.search.dip.multicast.message.SearchMultiResponse;
import org.greatfree.app.search.dip.multicast.message.SearchRequest;
import org.greatfree.app.search.dip.multicast.message.SearchResponse;
import org.greatfree.app.search.dip.multicast.message.SearchStream;
import org.greatfree.chat.message.ChatMessageType;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.framework.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 09/28/2018, Bing Li
class SearchEntryDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<CrawledPagesNotification, CrawledPagesNotificationThread, CrawledPagesNotificationThreadCreator> crawledPagesNotificationDispatcher;

	private NotificationDispatcher<LocationNotification, LocationNotificationThread, LocationNotificationThreadCreator> locationNotificationDispatcher;
	
	private RequestDispatcher<SearchRequest, SearchStream, SearchResponse, SearchRequestThread, SearchRequestThreadCreator> searchBroadcastRequestDispatcher;

	private NotificationDispatcher<SearchMultiResponse, SearchMultiResponseThread, SearchMultiResponseThreadCreator> searchMultiResponseDispatcher;

	private NotificationDispatcher<ShutdownChildrenAdminNotification, ShutdownStorageNodesNotificationThread, ShutdownStorageNodesNotificationThreadCreator> shutdownStorageNodesNotificationDispatcher;

	private NotificationDispatcher<ShutdownServerNotification, ShutdownSearchEntryThread, ShutdownSearchEntryThreadCreator> shutdownSearchEntryNotificationDispatcher;

//	public SearchEntryDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public SearchEntryDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		this.crawledPagesNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CrawledPagesNotification, CrawledPagesNotificationThread, CrawledPagesNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new CrawledPagesNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.locationNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<LocationNotification, LocationNotificationThread, LocationNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new LocationNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
		
		this.searchBroadcastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SearchRequest, SearchStream, SearchResponse, SearchRequestThread, SearchRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.searchMultiResponseDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SearchMultiResponse, SearchMultiResponseThread, SearchMultiResponseThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchMultiResponseThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownStorageNodesNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenAdminNotification, ShutdownStorageNodesNotificationThread, ShutdownStorageNodesNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownStorageNodesNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownSearchEntryNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownSearchEntryThread, ShutdownSearchEntryThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownSearchEntryThreadCreator())
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
		this.crawledPagesNotificationDispatcher.dispose();
		this.locationNotificationDispatcher.dispose();
		this.searchBroadcastRequestDispatcher.dispose();
		this.searchMultiResponseDispatcher.dispose();
		this.shutdownStorageNodesNotificationDispatcher.dispose();
		this.shutdownSearchEntryNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case SearchMessageType.CRAWLED_PAGES_NOTIFICATION:
				System.out.println("CRAWLED_PAGES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.crawledPagesNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.crawledPagesNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.crawledPagesNotificationDispatcher.enqueue((CrawledPagesNotification)message.getMessage());
				break;
				
			case SearchMessageType.LOCATION_NOTIFICATION:
				System.out.println("LOCATION_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.locationNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.locationNotificationDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.locationNotificationDispatcher.enqueue((LocationNotification)message.getMessage());
				break;
				
			case SearchMessageType.SEARCH_REQUEST:
				System.out.println("SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.searchBroadcastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.searchBroadcastRequestDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.searchBroadcastRequestDispatcher.enqueue(new SearchStream(message.getOutStream(), message.getLock(), (SearchRequest)message.getMessage()));
				break;
				
			case SearchMessageType.SEARCH_MULTI_RESPONSE:
				System.out.println("SEARCH_MULTI_RESPONSE received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.searchMultiResponseDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.searchMultiResponseDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.searchMultiResponseDispatcher.enqueue((SearchMultiResponse)message.getMessage());
				break;
				
			case MulticastDIPMessageType.SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION:
				System.out.println("SHUTDOWN_CHILDREN_ADMIN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.shutdownStorageNodesNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.shutdownStorageNodesNotificationDispatcher);
				}
				// Enqueue the instance of ShutdownChildrenAdminNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.shutdownStorageNodesNotificationDispatcher.enqueue((ShutdownChildrenAdminNotification)message.getMessage());
				break;
				
			case ChatMessageType.SHUTDOWN_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.shutdownSearchEntryNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.shutdownSearchEntryNotificationDispatcher);
				}
				// Enqueue the instance of ShutdownChildrenAdminNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.shutdownSearchEntryNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}

}
