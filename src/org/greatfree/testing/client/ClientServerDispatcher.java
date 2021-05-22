package org.greatfree.testing.client;

import java.util.Calendar;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadFeedbackNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.NodeKeyNotification;

/*
 * The server dispatcher resides on the client rather than on the classic server. It has two goals. First, if the client is a peer end, i.e., a server plus a client, the dispatcher is required. Second, it helps a client to initialize instances of FreeClient to read remote data. The 2nd goal is needed in most clients. This is what it is done in the sample. 11/07/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/07/2014, Bing Li
//public class ClientServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class ClientServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/09/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;
	
	// Declare a instance of notification dispatcher to deal with received the feedback for ObjectInputStream. 11/07/2014, Bing Li
	private NotificationDispatcher<InitReadFeedbackNotification, SetInputStreamThread, SetInputStreamThreadCreator> setInputStreamNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/07/2014, Bing Li
	 */
	public ClientServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Initialize the parent class. 11/07/2014, Bing Li
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/09/2014, Bing Li
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

		// Initialize the notification dispatcher for the notification, InitReadFeedbackNotification. 11/07/2014, Bing Li
//		this.setInputStreamNotificationDispatcher = new NotificationDispatcher<InitReadFeedbackNotification, SetInputStreamThread, SetInputStreamThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new SetInputStreamThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.setInputStreamNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<InitReadFeedbackNotification, SetInputStreamThread, SetInputStreamThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new SetInputStreamThreadCreator())
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
	 * Shutdown the dispatcher. 11/07/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Shutdown the notification dispatcher for setting the node key. 11/07/2014, Bing Li
		this.nodeKeyNotificationDispatcher.dispose();
		// Shutdown the notification dispatcher for initializing ObjectInputStream. 11/07/2014, Bing Li
		this.setInputStreamNotificationDispatcher.dispose();
		// Shutdown the parent dispatcher. 11/07/2014, Bing Li
		super.shutdown(timeout);
	}

	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/07/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		// Detect the message type. 11/07/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/09/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				System.out.println("NODE_KEY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the node-key notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.nodeKeyNotificationDispatcher.isReady())
				{
					// Execute the node-key notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.nodeKeyNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/09/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;

			// Process the notification of the type, InitReadFeedbackNotification. 11/07/2014, Bing Li
			case SystemMessageType.INIT_READ_FEEDBACK_NOTIFICATION:
				System.out.println("INIT_READ_FEEDBACK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the set-input-stream notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.setInputStreamNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.setInputStreamNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/07/2014, Bing Li
				this.setInputStreamNotificationDispatcher.enqueue((InitReadFeedbackNotification)message.getMessage());
				break;
		}
	}
}
