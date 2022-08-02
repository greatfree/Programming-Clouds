package org.greatfree.server.abandoned;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.InitReadFeedbackThread;
import org.greatfree.server.InitReadFeedbackThreadCreator;
import org.greatfree.server.MessageStream;
import org.greatfree.server.RegisterClientThread;
import org.greatfree.server.RegisterClientThreadCreator;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;

/*
 * Some old code uses the abandoned dispatcher. The dispatcher aims to keep the old code. 01/13/2019, Bing Li
 */

// Created: 01/13/2019, Bing Li
public abstract class ServerDispatcher<Message extends ServerMessage>
{
	// Declare an instance of ThreadPool that is used to execute threads concurrently. 11/07/2014, Bing Li
	private ThreadPool pool;

	// The scheduler is used to check the status of threads managed by the dispatcher. 05/08/2018, Bing Li
	private ScheduledThreadPoolExecutor scheduler;

	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;

	/*
	 * Using the constructor, the server side uses an independent thread pool. This is an old design. Usually, it is not used. 01/13/2019, Bing Li
	 */
	public ServerDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/04/2014, Bing Li
//		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		
		this.pool = new ThreadPool(threadPoolSize, threadKeepAliveTime);
//		this.pool = pool;
		// Set the pool size. 02/01/2016, Bing Li
		this.scheduler = new ScheduledThreadPoolExecutor(schedulerPoolSize);
		// The the lasted time to keep a thread alive. 02/01/2016, Bing Li
		this.scheduler.setKeepAliveTime(schedulerKeepAliveTime, TimeUnit.MILLISECONDS);
		// Set the core thread's timeout. When no tasks are available the relevant threads need to be collected and killed. 02/01/2016, Bing Li
		this.scheduler.allowCoreThreadTimeOut(true);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
//				.threadPool(this.pool)
				.threadCreator(new RegisterClientThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
//				.scheduler(super.getSchedulerPool())
				.scheduler(this.scheduler)
				.build();

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new InitReadFeedbackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
//				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
//				.scheduler(super.getSchedulerPool())
				.scheduler(this.scheduler)
				.build();
	}
	public abstract void dispose(long timeout) throws InterruptedException;
	public abstract void process(MessageStream<ServerMessage> message);
	
	/*
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
	protected void shutdown(long timeout) throws InterruptedException
	{
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
//		super.shutdown(timeout);
//		this.pool.shutdown(timeout);
		this.scheduler.shutdownNow();
	}

	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
	public void consume(MessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
//				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the registry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.registerClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
//					super.execute(this.registerClientNotificationDispatcher);
					this.execute(this.registerClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerClientNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
			
			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
//				System.out.println("INIT_READ_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the reading initialization dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
//					super.execute(this.initReadFeedbackNotificationDispatcher);
					this.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
		}
	}

	/*
	 * Return the scheduler instance if needed. 02/01/2016, Bing Li
	 */
	protected ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}
	
	
	
	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
//	@Override
	protected void execute(Thread thread)
	{
		this.pool.execute(thread);
//		SharedThreadPool.SHARED().getPool().execute(thread);
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
//	@Override
	protected void execute(Runnable thread)
	{
		this.pool.execute(thread);
//		SharedThreadPool.SHARED().getPool().execute(thread);
	}
}
