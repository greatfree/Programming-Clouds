package org.greatfree.testing.cluster.coordinator.client;

import java.util.Calendar;

import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.InitReadFeedbackThread;
import org.greatfree.server.InitReadFeedbackThreadCreator;
import org.greatfree.server.RegisterClientThread;
import org.greatfree.server.RegisterClientThreadCreator;
import org.greatfree.server.abandoned.ServerDispatcher;
import org.greatfree.testing.message.ClientForAnycastNotification;
import org.greatfree.testing.message.ClientForAnycastRequest;
import org.greatfree.testing.message.ClientForAnycastResponse;
import org.greatfree.testing.message.ClientForAnycastStream;
import org.greatfree.testing.message.ClientForBroadcastNotification;
import org.greatfree.testing.message.ClientForBroadcastRequest;
import org.greatfree.testing.message.ClientForBroadcastResponse;
import org.greatfree.testing.message.ClientForBroadcastStream;
import org.greatfree.testing.message.ClientForUnicastNotification;
import org.greatfree.testing.message.ClientForUnicastRequest;
import org.greatfree.testing.message.ClientForUnicastResponse;
import org.greatfree.testing.message.ClientForUnicastStream;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;
import org.greatfree.testing.message.ShutdownServerNotification;
import org.greatfree.testing.message.UnregisterClientNotification;
import org.greatfree.testing.server.ShutdownThread;
import org.greatfree.testing.server.ShutdownThreadCreator;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive their notifications for the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
//public class ClientServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public class ClientServerDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;

	// Declare a notification dispatcher to process the client unregistering concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<UnregisterClientNotification, UnregisterClientThread, UnregisterClientThreadCreator> unregisterClientNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for broadcasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForBroadcastNotification, ClientForBroadcastNotificationThread, ClientForBroadcastNotificationThreadCreator> clientForBroadcastNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for unicasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForUnicastNotification, ClientForUnicastNotificationThread, ClientForUnicastNotificationThreadCreator> clientForUnicastNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for anycasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForAnycastNotification, ClientForAnycastNotificationThread, ClientForAnycastNotificationThreadCreator> clientForAnycastNotificationDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForBroadcastResponse to the relevant remote client when an instance of ClientForBroadcastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread, ClientForBroadcastRequestThreadCreator> clientForBroadcastRequestDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForUnicastResponse to the relevant remote client when an instance of ClientForUnicastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread, ClientForUnicastRequestThreadCreator> clientForUnicastRequestDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForAnycastResponse to the relevant remote client when an instance of ClientForAnycastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread, ClientForAnycastRequestThreadCreator> clientForAnycastRequestDispatcher;

	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a notification dispatcher to shutdown the server when such a notification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator> shutdownNotificationDispatcher;

	/*
	 * Initialize. 11/24/2014, Bing Li
	 */
	public ClientServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
//		this.registerClientNotificationDispatcher = new NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.registerClientNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new RegisterClientThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client unregistering notification dispatcher. 11/30/2014, Bing Li
//		this.unregisterClientNotificationDispatcher = new NotificationDispatcher<UnregisterClientNotification, UnregisterClientThread, UnregisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new UnregisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.unregisterClientNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<UnregisterClientNotification, UnregisterClientThread, UnregisterClientThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new UnregisterClientThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-broadcast-notification dispatcher. 11/30/2014, Bing Li
//		this.clientForBroadcastNotificationDispatcher = new NotificationDispatcher<ClientForBroadcastNotification, ClientForBroadcastNotificationThread, ClientForBroadcastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForBroadcastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.clientForBroadcastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClientForBroadcastNotification, ClientForBroadcastNotificationThread, ClientForBroadcastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForBroadcastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-unicast-notification dispatcher. 11/30/2014, Bing Li
//		this.clientForUnicastNotificationDispatcher = new NotificationDispatcher<ClientForUnicastNotification, ClientForUnicastNotificationThread, ClientForUnicastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForUnicastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());

		this.clientForUnicastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClientForUnicastNotification, ClientForUnicastNotificationThread, ClientForUnicastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForUnicastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-anycast-notification dispatcher. 11/30/2014, Bing Li
//		this.clientForAnycastNotificationDispatcher = new NotificationDispatcher<ClientForAnycastNotification, ClientForAnycastNotificationThread, ClientForAnycastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForAnycastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.clientForAnycastNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ClientForAnycastNotification, ClientForAnycastNotificationThread, ClientForAnycastNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForAnycastNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-broadcast-request dispatcher. 11/30/2014, Bing Li
//		this.clientForBroadcastRequestDispatcher = new RequestDispatcher<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread, ClientForBroadcastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForBroadcastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.clientForBroadcastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread, ClientForBroadcastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForBroadcastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-unicast-request dispatcher. 11/30/2014, Bing Li
//		this.clientForUnicastRequestDispatcher = new RequestDispatcher<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread, ClientForUnicastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForUnicastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.clientForUnicastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread, ClientForUnicastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForUnicastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the client-for-anycast-request dispatcher. 11/30/2014, Bing Li
//		this.clientForAnycastRequestDispatcher = new RequestDispatcher<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread, ClientForAnycastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForAnycastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.clientForAnycastRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread, ClientForAnycastRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ClientForAnycastRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
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

		// Initialize the shutdown notification dispatcher. 11/30/2014, Bing Li
//		this.shutdownNotificationDispatcher = new NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, super.getSchedulerPool());
		
		this.shutdownNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new ShutdownThreadCreator())
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
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
//	public void shutdown(long timeout) throws InterruptedException
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the unregister dispatcher. 01/14/2016, Bing Li
		this.unregisterClientNotificationDispatcher.dispose();
		// Dispose the broadcast notification dispatcher. 02/15/2016, Bing Li
		this.clientForBroadcastNotificationDispatcher.dispose();
		// Dispose the unicast notification dispatcher. 02/15/2016, Bing Li
		this.clientForUnicastNotificationDispatcher.dispose();
		// Dispose the anycast notification dispatcher. 02/15/2016, Bing Li
		this.clientForAnycastNotificationDispatcher.dispose();
		// Dispose the broadcast request dispatcher. 02/15/2016, Bing Li
		this.clientForBroadcastRequestDispatcher.dispose();
		// Dispose the unicast request dispatcher. 02/15/2016, Bing Li
		this.clientForUnicastRequestDispatcher.dispose();
		// Dispose the anycast request dispatcher. 02/15/2016, Bing Li
		this.clientForAnycastRequestDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Dispose the dispatcher for shutdown. 11/09/2014, Bing Li
		this.shutdownNotificationDispatcher.dispose();
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		super.shutdown(timeout);
	}
	
	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
//	public void consume(OutMessageStream<ServerMessage> message)
	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the registry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.registerClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerClientNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
				
			case MessageType.UNREGISTER_CLIENT_NOTIFICATION:
				System.out.println("UNREGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the unregistry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.unregisterClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.unregisterClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.unregisterClientNotificationDispatcher.enqueue((UnregisterClientNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_BROADCAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client broadcast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForBroadcastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForBroadcastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForBroadcastNotificationDispatcher.enqueue((ClientForBroadcastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_UNICAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client unicast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForUnicastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForUnicastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForUnicastNotificationDispatcher.enqueue((ClientForUnicastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_ANYCAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client anycast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForAnycastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForAnycastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForAnycastNotificationDispatcher.enqueue((ClientForAnycastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_BROADCAST_REQUEST:
				System.out.println("CLIENT_FOR_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client broadcast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForBroadcastRequestDispatcher.isReady())
				{
					// Execute the client broadcast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForBroadcastRequestDispatcher);
				}
				// Enqueue the instance of ClientForBroadcastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForBroadcastRequestDispatcher.enqueue(new ClientForBroadcastStream(message.getOutStream(), message.getLock(), (ClientForBroadcastRequest)message.getMessage()));
				break;
				
			case MessageType.CLIENT_FOR_UNICAST_REQUEST:
				System.out.println("CLIENT_FOR_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client unicast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForUnicastRequestDispatcher.isReady())
				{
					// Execute the client unicast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForUnicastRequestDispatcher);
				}
				// Enqueue the instance of ClientForUnicastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForUnicastRequestDispatcher.enqueue(new ClientForUnicastStream(message.getOutStream(), message.getLock(), (ClientForUnicastRequest)message.getMessage()));
				break;
				
			case MessageType.CLIENT_FOR_ANYCAST_REQUEST:
				System.out.println("CLIENT_FOR_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client anycast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForAnycastRequestDispatcher.isReady())
				{
					// Execute the client anycast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForAnycastRequestDispatcher);
				}
				// Enqueue the instance of ClientForAnycastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForAnycastRequestDispatcher.enqueue(new ClientForAnycastStream(message.getOutStream(), message.getLock(), (ClientForAnycastRequest)message.getMessage()));
				break;

			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				System.out.println("INIT_READ_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the reading initialization dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
				
			case MessageType.SHUTDOWN_REGULAR_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_REGULAR_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}
}
