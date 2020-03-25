package org.greatfree.testing.coordinator.searching;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.IsPublisherExistedRequest;
import org.greatfree.testing.message.IsPublisherExistedResponse;
import org.greatfree.testing.message.IsPublisherExistedStream;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.SearchKeywordRequest;
import org.greatfree.testing.message.SearchKeywordResponse;
import org.greatfree.testing.message.SearchKeywordStream;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond searchers' requests and receive their notifications for the coordinator. 11/29/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization request dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/29/2014, Bing Li
//public class SearchServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class SearchServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/29/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a request dispatcher to respond users requests concurrently. 11/29/2014, Bing Li
	private RequestDispatcher<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator> isPublisherExistedRequestDispatcher;
	// Declare a request dispatcher to respond users requests concurrently. 11/29/2014, Bing Li
	private RequestDispatcher<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread, SearchKeywordThreadCreator> searchKeywordRequestDispatcher;
	
	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
//	public SearchServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public SearchServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher. 01/14/2016, Bing Li
//		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new InitReadFeedbackThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());

		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new InitReadFeedbackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the request dispatcher. 11/29/2014, Bing Li
//		this.isPublisherExistedRequestDispatcher = new RequestDispatcher<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new IsPublisherExistedThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.isPublisherExistedRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new IsPublisherExistedThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the request dispatcher. 11/29/2014, Bing Li
//		this.searchKeywordRequestDispatcher = new RequestDispatcher<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread, SearchKeywordThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new SearchKeywordThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.searchKeywordRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread, SearchKeywordThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SearchKeywordThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

	}

	/*
	 * Shut down the server message dispatcher. 11/29/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		this.initReadFeedbackNotificationDispatcher.dispose();
		this.isPublisherExistedRequestDispatcher.dispose();
		this.searchKeywordRequestDispatcher.dispose();
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/29/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/29/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the one of initializing notification. 11/29/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				// Check whether the notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/29/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;

			// Process the search request. 11/29/2014, Bing Li
			case MessageType.IS_PUBLISHER_EXISTED_REQUEST:
				// Check whether the dispatcher is ready or not. 01/14/2016, Bing Li
				if (this.isPublisherExistedRequestDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.isPublisherExistedRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/29/2014, Bing Li
				this.isPublisherExistedRequestDispatcher.enqueue(new IsPublisherExistedStream(message.getOutStream(), message.getLock(), (IsPublisherExistedRequest)message.getMessage()));
				break;
				
			// Process the search request. 11/29/2014, Bing Li
			case MessageType.SEARCH_KEYWORD_REQUEST:
				// Check whether the dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.searchKeywordRequestDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.searchKeywordRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/29/2014, Bing Li
				this.searchKeywordRequestDispatcher.enqueue(new SearchKeywordStream(message.getOutStream(), message.getLock(), (SearchKeywordRequest)message.getMessage()));
				break;
		}
	}
}
