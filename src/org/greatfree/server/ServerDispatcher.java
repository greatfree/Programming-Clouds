package org.greatfree.server;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;

/*
 * The server key is added as a new field. 03/30/2020, Bing Li
 * 
 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
 */

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive clients' notifications for the server. The dispatcher contains the concurrency mechanism that is used to establish the fundamental connection rather than high level applications. In another word, the high level application needs to build on it. 04/19/2017, Bing Li
 */

// Created: 04/19/2017, Bing Li
//public abstract class ServerDispatcher extends ServerMessageDispatcher<ServerMessage>
public abstract class ServerDispatcher<Message extends ServerMessage>
{
	// The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
	private String serverKey;
	
	// Declare an instance of ThreadPool that is used to execute threads concurrently. 11/07/2014, Bing Li
	private ThreadPool pool;

	// The scheduler is used to check the status of threads managed by the dispatcher. 05/08/2018, Bing Li
	private ScheduledThreadPoolExecutor scheduler;

	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	
	// The boolean field is added to avoid processing incoming messages after the server dispatcher is shut down. 11/17/2019, Bing Li
	private AtomicBoolean isDown;

	/*
	 * Using the constructor, the server side shares thread pool with the TCP listeners. It lowers the resource consumption on the server. When the dispatcher is used to implement a server, the constructor is employed. 01/13/2019, Bing Li
	 */
	
//	public ServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
//	public ServerDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public ServerDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/04/2014, Bing Li
//		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
		
		this.pool = new ThreadPool(serverThreadPoolSize, serverThreadKeepAliveTime);
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
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
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
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
//				.scheduler(super.getSchedulerPool())
				.scheduler(this.scheduler)
				.build();
		
		// The boolean field is added to avoid processing incoming messages after the server dispatcher is shut down. 11/17/2019, Bing Li
		this.isDown = new AtomicBoolean(false);
	}

	public abstract void dispose(long timeout) throws InterruptedException;
	public abstract void process(MessageStream<ServerMessage> message);
	
	public String getServerKey()
	{
		return this.serverKey;
	}
	
	public void setServerKey(String key)
	{
		this.serverKey = key;
	}
	
	/*
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
	protected void shutdown(long timeout) throws InterruptedException
	{
//		System.out.println("ServerDispatcher-shutdown(): starting to shutdown ...");
		this.isDown.set(true);
//		System.out.println("ServerDispatcher-shutdown(): shutdown is performed ...");
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
//		super.shutdown(timeout);
		this.pool.shutdown(timeout);
		this.scheduler.shutdownNow();
	}

	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
	public void consume(MessageStream<ServerMessage> message)
	{
		/*
		if (message == null)
		{
			System.out.println("ServerDispatcher-consume(): message is NULL");
		}
		else
		{
			System.out.println("ServerDispatcher-consume(): message is NOT NULL");
			if (message.getMessage() == null)
			{
				System.out.println("ServerDispatcher-consume(): getMessage is NULL");
			}
			else
			{
				System.out.println("ServerDispatcher-consume(): getMessage is NOT NULL");
			}
		}
		*/
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
	 * The method is added to avoid processing incoming messages after the server dispatcher is shut down. 11/17/2019, Bing Li
	 */
	public boolean isDown()
	{
		return this.isDown.get();
	}

	/*
	 * Return the scheduler instance if needed. 02/01/2016, Bing Li
	 */
	public ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}
	
	/*
	public void setThreadPool(ThreadPool pool)
	{
		this.pool = pool;
	}
	*/
	
	/*
	 * I do not remember why the thread pool was invisible. The SharedThreadPool is not a good design. I think the pool of the dispatcher can be shared at the server side. 11/04/2018, Bing Li
	 * 
	 * The eventers share the thread pool for the listeners. So the one can be invisible. 07/31/2018, Bing Li
	 * 
	 * Now the notification dispatcher and the request dispatcher have their own thread pools. The pool is not shared by them. However, for a peer, since it needs to send notification asynchronously. The thread pool is useful. 07/31/2018, Bing Li
	 * 
	 * Expose the thread pool, which is usually shared among the notification dispatchers and the request dispatchers. 05/13/2018, Bing Li
	 */
	public ThreadPool getThreadPool()
	{
		return this.pool;
//		return SharedThreadPool.SHARED().getPool();
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
