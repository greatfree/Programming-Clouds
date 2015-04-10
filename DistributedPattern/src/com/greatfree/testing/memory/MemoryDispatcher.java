package com.greatfree.testing.memory;

import com.greatfree.concurrency.AnycastRequestDispatcher;
import com.greatfree.concurrency.BoundBroadcastRequestDispatcher;
import com.greatfree.concurrency.BoundNotificationDispatcher;
import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.AddCrawledLinkNotification;
import com.greatfree.testing.message.IsPublisherExistedAnycastRequest;
import com.greatfree.testing.message.IsPublisherExistedAnycastResponse;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.NodeKeyNotification;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the coordinator's requests and notifications for the memory server. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class MemoryDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/28/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;
	private NotificationDispatcher<AddCrawledLinkNotification, SaveCrawledLinkThread, SaveCrawledLinkThreadCreator> saveCrawledLinkNotificationDispatcher;

	private AnycastRequestDispatcher<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator> isPublisherExistedyAnycastRequestDispatcher;

	private MulticastMessageDisposer<SearchKeywordBroadcastRequest> searchKeywordRequestDisposer;
	private BoundBroadcastRequestDispatcher<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread, SearchKeywordThreadCreator> searchKeywordRequestDispatcher;
	private BoundNotificationDispatcher<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread, BroadcastSearchKeywordRequestThreadCreator> broadcastSearchKeywordRequestDispatcher;

	/*
	 * Initialize the dispatcher. 11/28/2014, Bing Li
	 */
	public MemoryDispatcher(int corePoolSize, long keepAliveTime)
	{
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/28/2014, Bing Li
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the nodeKeyNotificationDispatcher. 11/28/2014, Bing Li
		this.nodeKeyNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the nodeKeyNotificationDispatcher. 11/28/2014, Bing Li
		super.execute(this.nodeKeyNotificationDispatcher);

		// Initialize the notification dispatcher for the notification, AddCrawledLinkNotification. 11/28/2014, Bing Li
		this.saveCrawledLinkNotificationDispatcher = new NotificationDispatcher<AddCrawledLinkNotification, SaveCrawledLinkThread, SaveCrawledLinkThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new SaveCrawledLinkThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the saveCrawledLinkNotificationDispatcher. 11/28/2014, Bing Li
		this.saveCrawledLinkNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the saveCrawledLinkNotificationDispatcher. 11/28/2014, Bing Li
		super.execute(this.saveCrawledLinkNotificationDispatcher);

		// Initialize the anycast dispatcher for the request, IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
		this.isPublisherExistedyAnycastRequestDispatcher = new AnycastRequestDispatcher<IsPublisherExistedAnycastRequest, IsPublisherExistedAnycastResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>(ClientPool.STORE().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_MEMORY, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new IsPublisherExistedThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME);
		// Set the idle checking. 11/29/2014, Bing Li
		this.isPublisherExistedyAnycastRequestDispatcher.setIdleChecker(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the dispatcher. 11/29/2014, Bing Li
		super.execute(this.isPublisherExistedyAnycastRequestDispatcher);
		
		this.searchKeywordRequestDisposer = new MulticastMessageDisposer<SearchKeywordBroadcastRequest>();
		this.searchKeywordRequestDispatcher = new BoundBroadcastRequestDispatcher<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread, SearchKeywordThreadCreator>(ClientPool.STORE().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_MEMORY, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, this.searchKeywordRequestDisposer, new SearchKeywordThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME);
		this.searchKeywordRequestDispatcher.setIdleChecker(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD);
		super.execute(this.searchKeywordRequestDispatcher);
		
		this.broadcastSearchKeywordRequestDispatcher = new BoundNotificationDispatcher<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread, BroadcastSearchKeywordRequestThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.searchKeywordRequestDisposer, new BroadcastSearchKeywordRequestThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		this.broadcastSearchKeywordRequestDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		super.execute(this.broadcastSearchKeywordRequestDispatcher);
	}

	/*
	 * Shutdown the dispatcher. 11/28/2014, Bing Li
	 */
	public void shutdown()
	{
		this.nodeKeyNotificationDispatcher.dispose();
		this.saveCrawledLinkNotificationDispatcher.dispose();
		this.isPublisherExistedyAnycastRequestDispatcher.dispose();
		this.searchKeywordRequestDispatcher.dispose();
		this.broadcastSearchKeywordRequestDispatcher.dispose();
		super.shutdown();
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/28/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		SearchKeywordBroadcastRequest broadcastRequest;
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/28/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/28/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;
				
			// Process the notification of AddCrawledLinkNotification. 11/28/2014, Bing Li
			case MessageType.ADD_CRAWLED_LINK_NOTIFICATION:
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/28/2014, Bing Li
				this.saveCrawledLinkNotificationDispatcher.enqueue((AddCrawledLinkNotification)message.getMessage());
				break;

			// Process the anycast request of IsPublisherExistedAnycastRequest. 11/29/2014, Bing Li
			case MessageType.IS_PUBLISHER_EXISTED_ANYCAST_REQUEST:
				// Enqueue the anycast request into the anycast request dispatcher. The requests are queued and processed asynchronously. 11/29/2014, Bing Li
				this.isPublisherExistedyAnycastRequestDispatcher.enqueue((IsPublisherExistedAnycastRequest)message.getMessage());
				break;

			// Process the broadcast request of SearchKeywordBroadcastRequest. 11/29/2014, Bing Li
			case MessageType.SEARCH_KEYWORD_BROADCAST_REQUEST:
				// Cast the request. 11/29/2014, Bing Li
				broadcastRequest = (SearchKeywordBroadcastRequest)message.getMessage();
				// Put the request into the search dispatcher to retrieve. 11/29/2014, Bing Li
				this.searchKeywordRequestDispatcher.enqueue(broadcastRequest);
				// Put the request into the broadcast dispatcher to forward it to the local node's children. 11/29/2014, Bing Li
				this.broadcastSearchKeywordRequestDispatcher.enqueue(broadcastRequest);
				break;
		}
	}
}
