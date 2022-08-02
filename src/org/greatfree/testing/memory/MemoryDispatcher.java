package org.greatfree.testing.memory;

import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.concurrency.reactive.AnycastRequestDispatcher;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.OldBoundNotificationDispatcher;
import org.greatfree.concurrency.reactive.OldBoundRequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.cluster.dn.RegisterThread;
import org.greatfree.testing.cluster.dn.RegisterThreadCreator;
import org.greatfree.testing.message.AddCrawledLinkNotification;
import org.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import org.greatfree.testing.message.IsPublisherExistedAnycastResponse;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.testing.message.SearchKeywordBroadcastRequest;
import org.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the coordinator's requests and notifications for the memory server. 11/27/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization notification dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/27/2014, Bing Li
//public class MemoryDispatcher extends ServerMessageDispatcher<ServerMessage>
public class MemoryDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare an instance of notification dispatcher to deal with the received notification that contains the node key. 11/28/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;
	// Declare an instance of notification dispatcher to deal with the received notification to save crawled link. 11/28/2014, Bing Li
	private NotificationDispatcher<AddCrawledLinkNotification, SaveCrawledLinkThread, SaveCrawledLinkThreadCreator> saveCrawledLinkNotificationDispatcher;

	// Declare the anycast dispatcher for the request, IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
	private AnycastRequestDispatcher<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator> isPublisherExistedyAnycastRequestDispatcher;

	// Declare the message disposer to collect search requests after it is handled and broadcast. 01/14/2016, Bing Li
	private MulticastMessageDisposer<SearchKeywordBroadcastRequest> searchKeywordRequestDisposer;
	// Declare the dispatcher to handle the search request. 01/14/2016, Bing Li 
	private OldBoundRequestDispatcher<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread, SearchKeywordThreadCreator> searchKeywordRequestDispatcher;
	// Declare the broadcast dispatcher to broadcast the search request. 01/14/2016, Bing Li
	private OldBoundNotificationDispatcher<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread, BroadcastSearchKeywordRequestThreadCreator> broadcastSearchKeywordRequestDispatcher;

	/*
	 * Initialize the dispatcher. 11/28/2014, Bing Li
	 */
//	public MemoryDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public MemoryDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/28/2014, Bing Li
//		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<NodeKeyNotification, RegisterThread, RegisterThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, AddCrawledLinkNotification. 11/28/2014, Bing Li
//		this.saveCrawledLinkNotificationDispatcher = new NotificationDispatcher<AddCrawledLinkNotification, SaveCrawledLinkThread, SaveCrawledLinkThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new SaveCrawledLinkThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.saveCrawledLinkNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<AddCrawledLinkNotification, SaveCrawledLinkThread, SaveCrawledLinkThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SaveCrawledLinkThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();


		// Initialize the anycast dispatcher for the request, IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
//		this.isPublisherExistedyAnycastRequestDispatcher = new AnycastRequestDispatcher<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>(ClientPool.STORE().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_MEMORY, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new IsPublisherExistedThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.isPublisherExistedyAnycastRequestDispatcher = new AnycastRequestDispatcher.AnycastRequestDispatcherBuilder<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>()
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.maxTaskSize(ServerConfig.REQUEST_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_REQUEST_THREAD_SIZE)
				.scheduler(super.getScheduler())
				// The pool size and keep-alive-time are not useful if the thread pool is shared since they are set outside the dispatcher. 04/19/2018, Bing Li
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.clientPool(ClientPool.STORE().getPool())
				.serverAddress(ServerConfig.COORDINATOR_ADDRESS)
				.serverPort(ServerConfig.COORDINATOR_PORT_FOR_MEMORY)
				.threadCreator(new IsPublisherExistedThreadCreator())
				.build();
		// Set the idle checking. 11/29/2014, Bing Li
//		this.isPublisherExistedyAnycastRequestDispatcher.setIdleChecker(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the dispatcher. 11/29/2014, Bing Li
//		super.execute(this.isPublisherExistedyAnycastRequestDispatcher);
		
		// Initialize the message disposer to collect search requests after it is handled and broadcast. 01/14/2016, Bing Li
		this.searchKeywordRequestDisposer = new MulticastMessageDisposer<SearchKeywordBroadcastRequest>();
		// Initialize the dispatcher to handle the search request. 01/14/2016, Bing Li 
//		this.searchKeywordRequestDispatcher = new OldBoundRequestDispatcher<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread, SearchKeywordThreadCreator>(ClientPool.STORE().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_MEMORY, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, this.searchKeywordRequestDisposer, new SearchKeywordThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.searchKeywordRequestDispatcher = new OldBoundRequestDispatcher.BoundRequestDispatcherBuilder<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread, SearchKeywordThreadCreator>()
				.clientPool(ClientPoolSingleton.SERVER().getPool())
				.rootIP(ServerConfig.COORDINATOR_ADDRESS)
				.rootPort(ServerConfig.COORDINATOR_DN_PORT)
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.searchKeywordRequestDisposer)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SearchKeywordThreadCreator())
				.maxTaskSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Set the idle checking. 11/29/2014, Bing Li
//		this.searchKeywordRequestDispatcher.setIdleChecker(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the dispatcher. 11/29/2014, Bing Li
//		super.execute(this.searchKeywordRequestDispatcher);
		
		// Initialize the broadcast dispatcher to broadcast the search request. 01/14/2016, Bing Li
//		this.broadcastSearchKeywordRequestDispatcher = new OldBoundNotificationDispatcher<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread, BroadcastSearchKeywordRequestThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.searchKeywordRequestDisposer, new BroadcastSearchKeywordRequestThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		// Set the idle checking. 11/29/2014, Bing Li
//		this.broadcastSearchKeywordRequestDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the dispatcher. 11/29/2014, Bing Li
//		super.execute(this.broadcastSearchKeywordRequestDispatcher);
		
		this.broadcastSearchKeywordRequestDispatcher = new OldBoundNotificationDispatcher.BoundNotificationDispatcherBuilder<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread, BroadcastSearchKeywordRequestThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.searchKeywordRequestDisposer)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new BroadcastSearchKeywordRequestThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

	}

	/*
	 * Shutdown the dispatcher. 11/28/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		this.nodeKeyNotificationDispatcher.dispose();
		this.saveCrawledLinkNotificationDispatcher.dispose();
		this.isPublisherExistedyAnycastRequestDispatcher.dispose();
		this.searchKeywordRequestDispatcher.dispose();
		this.broadcastSearchKeywordRequestDispatcher.dispose();
		super.shutdown(timeout);
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/28/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		SearchKeywordBroadcastRequest broadcastRequest;
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/28/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				// Check whether the notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.nodeKeyNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.nodeKeyNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/28/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;
				
			// Process the notification of AddCrawledLinkNotification. 11/28/2014, Bing Li
			case MessageType.ADD_CRAWLED_LINK_NOTIFICATION:
				// Check whether the notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.saveCrawledLinkNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.saveCrawledLinkNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/28/2014, Bing Li
				this.saveCrawledLinkNotificationDispatcher.enqueue((AddCrawledLinkNotification)message.getMessage());
				break;

			// Process the anycast request of IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
			case MessageType.IS_PUBLISHER_EXISTED_ANYCAST_REQUEST:
				// Check whether the anycast request dispatcher is ready or not. 02/02/2016, Bing Li
				if (!this.isPublisherExistedyAnycastRequestDispatcher.isReady())
				{
					// Execute the anycast request dispatcher as a thread. 02/02/2016, Bing Li
					super.execute(this.isPublisherExistedyAnycastRequestDispatcher);
				}
				// Enqueue the anycast request into the anycast request dispatcher. The requests are queued and processed asynchronously. 11/29/2014, Bing Li
				this.isPublisherExistedyAnycastRequestDispatcher.enqueue((IsPublisherExistedAnycastRequest)message.getMessage());
				break;

			// Process the broadcast request of SearchKeywordBroadcastRequest. 11/29/2014, Bing Li
			case MessageType.SEARCH_KEYWORD_BROADCAST_REQUEST:
				// Cast the request. 11/29/2014, Bing Li
				broadcastRequest = (SearchKeywordBroadcastRequest)message.getMessage();
				// Check whether the bound broadcast request dispatcher is ready or not. 02/02/2016, Bing Li
				if (!this.searchKeywordRequestDispatcher.isReady())
				{
					// Execute the bound broadcast request dispatchre. 02/02/2016, Bing Li
					super.execute(this.searchKeywordRequestDispatcher);
				}
				// Put the request into the search dispatcher to retrieve. 11/29/2014, Bing Li
				this.searchKeywordRequestDispatcher.enqueue(broadcastRequest);

				// Check whether the bound notification dispatcher is ready or not. 02/02/2016, Bing Li
				if (!this.broadcastSearchKeywordRequestDispatcher.isReady())
				{
					// Execute the bound notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.broadcastSearchKeywordRequestDispatcher);
				}
				// Put the request into the broadcast dispatcher to forward it to the local node's children. 11/29/2014, Bing Li
				this.broadcastSearchKeywordRequestDispatcher.enqueue(broadcastRequest);
				break;
		}
	}
}
