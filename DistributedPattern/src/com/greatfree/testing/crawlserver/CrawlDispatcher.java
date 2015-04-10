package com.greatfree.testing.crawlserver;

import com.greatfree.concurrency.BoundNotificationDispatcher;
import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.message.CrawlLoadNotification;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.NodeKeyNotification;
import com.greatfree.testing.message.StartCrawlMultiNotification;
import com.greatfree.testing.message.StopCrawlMultiNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the coordinator's requests and notifications for the crawling server. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class CrawlDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/25/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, startCrawlNotificationDispatcher and multicastStartCrawlNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of StartCrawlMultiNotification finally. 11/27/2014, Bing Li
	private MulticastMessageDisposer<StartCrawlMultiNotification> startCrawlNotificationDisposer;
	// The dispatcher to start the crawling after getting the notification of StartCrawlMultiNotification. It must be synchronized by the binder, startCrawlNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread, StartCrawlThreadCreator> startCrawlNotificationDispatcher;
	// The dispatcher to disseminate the notification of StartCrawlMultiNotification to children nodes. It must be synchronized by the binder, startCrawlNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread, MulticastStartCrawlNotificationThreadCreator> multicastStartCrawlNotificationDispatcher;
	
	// Declare a instance of notification dispatcher to deal with received the notification that contains the crawling workload. 11/28/2014, Bing Li
	private NotificationDispatcher<CrawlLoadNotification, AssignURLLoadThread, AssignURLLoadThreadCreator> crawlLoadNotificationDispatcher;
	
	// A instance of notification dispatcher to deal with received the stop crawling notification. 11/27/2014, Bing Li
	private NotificationDispatcher<StopCrawlMultiNotification, StopCrawlThread, StopCrawlThreadCreator> stopCrawlNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/25/2014, Bing Li
	 */
	public CrawlDispatcher(int corePoolSize, long keepAliveTime)
	{
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/25/2014, Bing Li
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the nodeKeyNotificationDispatcher. 11/25/2014, Bing Li
		this.nodeKeyNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the nodeKeyNotificationDispatcher. 11/25/2014, Bing Li
		super.execute(this.nodeKeyNotificationDispatcher);

		// Initialize the disposer for the notification of StartCrawlMultiNotification, which works as a binder. 11/27/2014, Bing Li
		this.startCrawlNotificationDisposer = new MulticastMessageDisposer<StartCrawlMultiNotification>();
		// Initialize the bound notification dispatcher for the notification, StartCrawlMultiNotification, to start crawling. 11/27/2014, Bing Li
		this.startCrawlNotificationDispatcher = new BoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, StartCrawlThread, StartCrawlThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.startCrawlNotificationDisposer, new StartCrawlThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the startCrawlNotificationDispatcher. 11/27/2014, Bing Li
		this.startCrawlNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the startCrawlNotificationDispatcher. 11/27/2014, Bing Li
		super.execute(this.startCrawlNotificationDispatcher);

		// Initialize the bound notification dispatcher for the notification, StartCrawlMultiNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
		this.multicastStartCrawlNotificationDispatcher = new BoundNotificationDispatcher<StartCrawlMultiNotification, MulticastMessageDisposer<StartCrawlMultiNotification>, MulticastStartCrawlNotificationThread, MulticastStartCrawlNotificationThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.startCrawlNotificationDisposer, new MulticastStartCrawlNotificationThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the multicastStartCrawlNotificationDispatcher. 11/27/2014, Bing Li
		this.multicastStartCrawlNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the multicastStartCrawlNotificationDispatcher. 11/27/2014, Bing Li
		super.execute(this.multicastStartCrawlNotificationDispatcher);

		// Initialize the notification dispatcher for the notification, CrawlLoadNotification. 11/28/2014, Bing Li
		this.crawlLoadNotificationDispatcher = new NotificationDispatcher<CrawlLoadNotification, AssignURLLoadThread, AssignURLLoadThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new AssignURLLoadThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the nodeKeyNotificationDispatcher. 11/25/2014, Bing Li
		this.crawlLoadNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the nodeKeyNotificationDispatcher. 11/25/2014, Bing Li
		super.execute(this.crawlLoadNotificationDispatcher);
		
		// Initialize the notification dispatcher for the notification, StopCrawlMultiNotification. 11/27/2014, Bing Li
		this.stopCrawlNotificationDispatcher = new NotificationDispatcher<StopCrawlMultiNotification, StopCrawlThread, StopCrawlThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new StopCrawlThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME);
		// Set the idle checking of the stopCrawlNotificationDispatcher. 11/27/2014, Bing Li
		this.stopCrawlNotificationDispatcher.setIdleChecker(ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD);
		// Start the stopCrawlNotificationDispatcher. 11/27/2014, Bing Li
		super.execute(this.stopCrawlNotificationDispatcher);
	}

	/*
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
	public void shutdown()
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
		super.shutdown();
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// The notification is shared by multiple threads. 11/27/2014, Bing Li
		StartCrawlMultiNotification startCrawlMultiNotification;
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/25/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/25/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;
				
			// Process the notification of StartCrawlMultiNotification. 11/27/2014, Bing Li
			case MessageType.START_CRAWL_MULTI_NOTIFICATION:
				// Cast the message. 11/27/2014, Bing Li
				startCrawlMultiNotification = (StartCrawlMultiNotification)message.getMessage();
				// Enqueue the notification into those bound notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.startCrawlNotificationDispatcher.enqueue(startCrawlMultiNotification);
				this.multicastStartCrawlNotificationDispatcher.enqueue(startCrawlMultiNotification);
				break;
				
			// Process the notification of CrawlLoadNotification. 11/27/2014, Bing Li
			case MessageType.CRAWL_LOAD_NOTIFICATION:
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/27/2014, Bing Li
				this.crawlLoadNotificationDispatcher.enqueue((CrawlLoadNotification)message.getMessage());
				break;
				
			// Process the notification of StopCrawlMultiNotification. 11/27/2014, Bing Li
			case MessageType.STOP_CRAWL_MULTI_NOTIFICATION:
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/27/2014, Bing Li
				this.stopCrawlNotificationDispatcher.enqueue((StopCrawlMultiNotification)message.getMessage());
				break;
		}
	}
}
