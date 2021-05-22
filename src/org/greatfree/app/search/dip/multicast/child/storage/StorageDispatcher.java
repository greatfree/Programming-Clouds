package org.greatfree.app.search.dip.multicast.child.storage;

import java.util.Calendar;

import org.greatfree.app.search.dip.multicast.message.CrawledPagesNotification;
import org.greatfree.app.search.dip.multicast.message.LocationNotification;
import org.greatfree.app.search.dip.multicast.message.SearchMessageType;
import org.greatfree.app.search.dip.multicast.message.SearchMultiRequest;
import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.multicast.message.MulticastDIPMessageType;
import org.greatfree.framework.multicast.message.RootIPAddressBroadcastNotification;
import org.greatfree.framework.multicast.message.ShutdownChildrenBroadcastNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.server.ServerDispatcher;

// Created: 09/28/2018, Bing Li
class StorageDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<RootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationThread, RootIPAddressBroadcastNotificationThreadCreator> rootIPBroadcastNotificationDispatcher;

	private NotificationDispatcher<CrawledPagesNotification, CrawledPagesNotificationThread, CrawledPagesNotificationThreadCreator> crawledPagesNotificationDispatcher;
	
	private NotificationDispatcher<SearchMultiRequest, SearchBroadcastRequestThread, SearchBroadcastRequestThreadCreator> searchBroadcastRequestDispatcher;

	private NotificationDispatcher<LocationNotification, LocationNotificationThread, LocationNotificationThreadCreator> locationNotificationDispatcher;

	private NotificationDispatcher<ShutdownChildrenBroadcastNotification, ShutdownStorageBroadcastNotificationThread, ShutdownStorageBroadcastNotificationThreadCreator> shutdownBroadcastNotificationDispatcher;

//	public StorageDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public StorageDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

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

		this.searchBroadcastRequestDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SearchMultiRequest, SearchBroadcastRequestThread, SearchBroadcastRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SearchBroadcastRequestThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.shutdownBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownChildrenBroadcastNotification, ShutdownStorageBroadcastNotificationThread, ShutdownStorageBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ShutdownStorageBroadcastNotificationThreadCreator())
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
		this.crawledPagesNotificationDispatcher.dispose();
		this.locationNotificationDispatcher.dispose();
		this.searchBroadcastRequestDispatcher.dispose();
		this.shutdownBroadcastNotificationDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
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
				
			case SearchMessageType.SEARCH_MULTI_REQUEST:
				System.out.println("SEARCH_MULTI_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.searchBroadcastRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.searchBroadcastRequestDispatcher);
				}
				// Enqueue the instance of HelloWorldBroadcastResponse into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.searchBroadcastRequestDispatcher.enqueue((SearchMultiRequest)message.getMessage());
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
