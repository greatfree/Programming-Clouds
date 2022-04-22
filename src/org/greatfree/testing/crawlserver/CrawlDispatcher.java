package org.greatfree.testing.crawlserver;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.OldBoundNotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.cluster.dn.RegisterThread;
import org.greatfree.testing.cluster.dn.RegisterThreadCreator;
import org.greatfree.testing.message.CrawlLoadNotification;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.NodeKeyNotification;
import org.greatfree.testing.message.StartCrawlMultiNotification;
import org.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the coordinator's requests and notifications for the crawling server. 11/23/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/23/2014, Bing Li
//public class CrawlDispatcher extends ServerMessageDispatcher<ServerMessage>
public class CrawlDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/25/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, startCrawlNotificationDispatcher and multicastStartCrawlNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of StartCrawlMultiNotification finally. 11/27/2014, Bing Li
	private MulticastMessageDisposer<StartCrawlMultiNotification> startCrawlNotificationDisposer;
	// The dispatcher to start the crawling after getting the notification of StartCrawlMultiNotification. It must be synchronized by the binder, startCrawlNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private OldBoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread, StartCrawlThreadCreator> startCrawlNotificationDispatcher;
	// The dispatcher to disseminate the notification of StartCrawlMultiNotification to children nodes. It must be synchronized by the binder, startCrawlNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private OldBoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread, MulticastStartCrawlNotificationThreadCreator> multicastStartCrawlNotificationDispatcher;
	
	// Declare a instance of notification dispatcher to deal with received the notification that contains the crawling workload. 11/28/2014, Bing Li
	private NotificationDispatcher<CrawlLoadNotification, AssignURLLoadThread, AssignURLLoadThreadCreator> crawlLoadNotificationDispatcher;
	
	// A instance of notification dispatcher to deal with received the stop crawling notification. 11/27/2014, Bing Li
	private NotificationDispatcher<StopCrawlMultiNotification, StopCrawlThread, StopCrawlThreadCreator> stopCrawlNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/25/2014, Bing Li
	 */
//	public CrawlDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public CrawlDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/25/2014, Bing Li
//		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<NodeKeyNotification, RegisterThread, RegisterThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the disposer for the notification of StartCrawlMultiNotification, which works as a binder. 11/27/2014, Bing Li
		this.startCrawlNotificationDisposer = new MulticastMessageDisposer<StartCrawlMultiNotification>();
		// Initialize the bound notification dispatcher for the notification, StartCrawlMultiNotification, to start crawling. 11/27/2014, Bing Li
//		this.startCrawlNotificationDispatcher = new OldBoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread, StartCrawlThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.startCrawlNotificationDisposer, new StartCrawlThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.startCrawlNotificationDispatcher = new OldBoundNotificationDispatcher.BoundNotificationDispatcherBuilder<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread, StartCrawlThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.startCrawlNotificationDisposer)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new StartCrawlThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Set the idle checking of the startCrawlNotificationDispatcher. 11/27/2014, Bing Li
//		this.startCrawlNotificationDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the startCrawlNotificationDispatcher. 11/27/2014, Bing Li
//		super.execute(this.startCrawlNotificationDispatcher);

		// Initialize the bound notification dispatcher for the notification, StartCrawlMultiNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
//		this.multicastStartCrawlNotificationDispatcher = new OldBoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread, MulticastStartCrawlNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.startCrawlNotificationDisposer, new MulticastStartCrawlNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.multicastStartCrawlNotificationDispatcher = new OldBoundNotificationDispatcher.BoundNotificationDispatcherBuilder<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread, MulticastStartCrawlNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
				.binder(this.startCrawlNotificationDisposer)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new MulticastStartCrawlNotificationThreadCreator())
				.maxTaskSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
//				.maxThreadSize(ServerConfig.MAX_NOTIFICATION_THREAD_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Set the idle checking of the multicastStartCrawlNotificationDispatcher. 11/27/2014, Bing Li
//		this.multicastStartCrawlNotificationDispatcher.setIdleChecker(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the multicastStartCrawlNotificationDispatcher. 11/27/2014, Bing Li
//		super.execute(this.multicastStartCrawlNotificationDispatcher);

		// Initialize the notification dispatcher for the notification, CrawlLoadNotification. 11/28/2014, Bing Li
//		this.crawlLoadNotificationDispatcher = new NotificationDispatcher<CrawlLoadNotification, AssignURLLoadThread, AssignURLLoadThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new AssignURLLoadThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.crawlLoadNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CrawlLoadNotification, AssignURLLoadThread, AssignURLLoadThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new AssignURLLoadThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the notification dispatcher for the notification, StopCrawlMultiNotification. 11/27/2014, Bing Li
//		this.stopCrawlNotificationDispatcher = new NotificationDispatcher<StopCrawlMultiNotification, StopCrawlThread, StopCrawlThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new StopCrawlThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.stopCrawlNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<StopCrawlMultiNotification, StopCrawlThread, StopCrawlThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new StopCrawlThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

	}

	/*
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Shutdown the notification dispatcher for setting the node key. 11/25/2014, Bing Li
		this.nodeKeyNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for starting crawling. 11/27/2014, Bing Li
		this.startCrawlNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the crawling notification. 11/27/2014, Bing Li
		this.multicastStartCrawlNotificationDispatcher.dispose();
		// Shutdown the notification dispatcher for stopping crawling. 11/27/2014, Bing Li
		this.stopCrawlNotificationDispatcher.dispose();
		// Shutdown the parent dispatcher. 11/25/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// The notification is shared by multiple threads. 11/27/2014, Bing Li
		StartCrawlMultiNotification startCrawlMultiNotification;
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/25/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				// Check whether the notification is ready or not. 01/14/2016, Bing Li
				if (!this.nodeKeyNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.nodeKeyNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/25/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;
				
			// Process the notification of StartCrawlMultiNotification. 11/27/2014, Bing Li
			case MessageType.START_CRAWL_MULTI_NOTIFICATION:
				// Cast the message. 11/27/2014, Bing Li
				startCrawlMultiNotification = (StartCrawlMultiNotification)message.getMessage();
				// Check whether the bound notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.startCrawlNotificationDispatcher.isReady())
				{
					// Execute the bound notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.startCrawlNotificationDispatcher);
				}
				// Enqueue the notification into those bound notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.startCrawlNotificationDispatcher.enqueue(startCrawlMultiNotification);
				
				// Check whether the bound notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.multicastStartCrawlNotificationDispatcher.isReady())
				{
					// Execute the bound notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.multicastStartCrawlNotificationDispatcher);
				}
				// Enqueue the notification into those bound notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.multicastStartCrawlNotificationDispatcher.enqueue(startCrawlMultiNotification);
				break;
				
			// Process the notification of CrawlLoadNotification. 11/27/2014, Bing Li
			case MessageType.CRAWL_LOAD_NOTIFICATION:
				// Check whether the crawl load notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.crawlLoadNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.crawlLoadNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/27/2014, Bing Li
				this.crawlLoadNotificationDispatcher.enqueue((CrawlLoadNotification)message.getMessage());
				break;
				
			// Process the notification of StopCrawlMultiNotification. 11/27/2014, Bing Li
			case MessageType.STOP_CRAWL_MULTI_NOTIFICATION:
				// Check whether the stopping-crawling notification thread is ready or not. 01/14/2016, Bing Li
				if (!this.stopCrawlNotificationDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/16/2016, Bing Li
					super.execute(this.stopCrawlNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/27/2014, Bing Li
				this.stopCrawlNotificationDispatcher.enqueue((StopCrawlMultiNotification)message.getMessage());
				break;
		}
	}
}
