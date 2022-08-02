package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.IsPublisherExistedAnycastResponse;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterMemoryServerNotification;
import org.greatfree.testing.message.SearchKeywordBroadcastResponse;
import org.greatfree.testing.message.UnregisterMemoryServerNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond memory servers' requests and receive their notifications for the coordinator. 11/28/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/28/2014, Bing Li
//public class MemoryServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class MemoryServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the memory server registration concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<RegisterMemoryServerNotification, RegisterMemoryServerThread, RegisterMemoryServerThreadCreator> registerMemoryServerNotificationDispatcher;
	// Declare a notification dispatcher to process the memory server unregistering concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<UnregisterMemoryServerNotification, UnregisterMemoryServerThread, UnregisterMemoryServerThreadCreator> unregisterMemoryServerNotificationDispatcher;

	private NotificationDispatcher<IsPublisherExistedAnycastResponse, NotifyIsPublisherExistedThread, NotifyIsPublisherExistedThreadCreator> notifyIsPublishedReceivedDispatcher;
	private NotificationDispatcher<SearchKeywordBroadcastResponse, NotifySearchKeywordThread, NotifySearchKeywordThreadCreator> notifySearchKeywordReceivedDispatcher;

	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
//	public MemoryServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public MemoryServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the memory server registration dispatcher. 11/28/2014, Bing Li
//		this.registerMemoryServerNotificationDispatcher = new NotificationDispatcher<RegisterMemoryServerNotification, RegisterMemoryServerThread, RegisterMemoryServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterMemoryServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.registerMemoryServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterMemoryServerNotification, RegisterMemoryServerThread, RegisterMemoryServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterMemoryServerThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the memory server unregistering dispatcher. 11/27/2014, Bing Li
//		this.unregisterMemoryServerNotificationDispatcher = new NotificationDispatcher<UnregisterMemoryServerNotification, UnregisterMemoryServerThread, UnregisterMemoryServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new UnregisterMemoryServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.unregisterMemoryServerNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnregisterMemoryServerNotification, UnregisterMemoryServerThread, UnregisterMemoryServerThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new UnregisterMemoryServerThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the anycast notifying dispatcher. 11/27/2014, Bing Li
//		this.notifyIsPublishedReceivedDispatcher = new NotificationDispatcher<IsPublisherExistedAnycastResponse, NotifyIsPublisherExistedThread, NotifyIsPublisherExistedThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new NotifyIsPublisherExistedThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.notifyIsPublishedReceivedDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<IsPublisherExistedAnycastResponse, NotifyIsPublisherExistedThread, NotifyIsPublisherExistedThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new NotifyIsPublisherExistedThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the broadcast notifying dispatcher. 11/27/2014, Bing Li
//		this.notifySearchKeywordReceivedDispatcher = new NotificationDispatcher<SearchKeywordBroadcastResponse, NotifySearchKeywordThread, NotifySearchKeywordThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new NotifySearchKeywordThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.notifySearchKeywordReceivedDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SearchKeywordBroadcastResponse, NotifySearchKeywordThread, NotifySearchKeywordThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new NotifySearchKeywordThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

	}

	/*
	 * Shut down the storing message dispatcher. 11/28/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Dispose the memory server registration dispatcher. 11/28/2014, Bing Li
		this.registerMemoryServerNotificationDispatcher.dispose();
		// Dispose the memory server unregistering dispatcher. 11/28/2014, Bing Li
		this.unregisterMemoryServerNotificationDispatcher.dispose();
		// Dispose the anycast notifying dispatcher. 11/29/2014, Bing Li
		this.notifyIsPublishedReceivedDispatcher.dispose();
		// Dispose the broadcast notifying dispatcher. 11/29/2014, Bing Li
		this.notifySearchKeywordReceivedDispatcher.dispose();
		// Shutdown the server message dispatcher. 11/28/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/28/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/28/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the notification to register the memory server. 11/28/2014, Bing Li
			case MessageType.REGISTER_MEMORY_SERVER_NOTIFICATION:
				// Check whether the memory registration notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.registerMemoryServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerMemoryServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/28/2014, Bing Li
				this.registerMemoryServerNotificationDispatcher.enqueue((RegisterMemoryServerNotification)message.getMessage());
				break;
				
			// If the message is the notification to unregister the memory server. 11/28/2014, Bing Li
			case MessageType.UNREGISTER_MEMORY_SERVER_NOTIFICATION:
				// Check whether the memory unregistration notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.unregisterMemoryServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.unregisterMemoryServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/28/2014, Bing Li
				this.unregisterMemoryServerNotificationDispatcher.enqueue((UnregisterMemoryServerNotification)message.getMessage());
				break;

			// If the message is an anycast response, IsPublisherExistedAnycastResponse. 11/29/2014, Bing Li
			case MessageType.IS_PUBLISHER_EXISTED_ANYCAST_RESPONSE:
				// Check whether the is-published-received notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.notifyIsPublishedReceivedDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.notifyIsPublishedReceivedDispatcher);
				}
				// Notify the received response. 11/29/2014, Bing Li
				this.notifyIsPublishedReceivedDispatcher.enqueue((IsPublisherExistedAnycastResponse)message.getMessage());
				break;

			// If the message is an broadcast response, SearchKeywordBroadcastResponse. 11/29/2014, Bing Li
			case MessageType.SEARCH_KEYWORD_BROADCAST_RESPONSE:
				// Check whether the search response dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.notifySearchKeywordReceivedDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.notifySearchKeywordReceivedDispatcher);
				}
				// Notify the received response. 11/29/2014, Bing Li
				this.notifySearchKeywordReceivedDispatcher.enqueue((SearchKeywordBroadcastResponse)message.getMessage());
				break;
		}
	}
}
