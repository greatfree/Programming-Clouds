package org.greatfree.server.nio;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.concurrency.ThreadPool;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;
import org.greatfree.server.InitReadFeedbackThread;
import org.greatfree.server.InitReadFeedbackThreadCreator;
import org.greatfree.server.RegisterClientThread;
import org.greatfree.server.RegisterClientThreadCreator;
import org.greatfree.testing.message.MessageType;
import org.greatfree.testing.message.RegisterClientNotification;

/**
 * 
 * @author Bing Li
 * 
 * 02/02/2022, Bing Li
 *
 */
public abstract class ServerDispatcher<Message extends ServerMessage>
{
	private String serverKey;
	private ThreadPool pool;
	private ScheduledThreadPoolExecutor scheduler;
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	private AtomicBoolean isDown;
	
	public ServerDispatcher(int serverThreadPoolSize, long serverThreadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		this.pool = new ThreadPool(serverThreadPoolSize, serverThreadKeepAliveTime);
		this.scheduler = new ScheduledThreadPoolExecutor(schedulerPoolSize);
		this.scheduler.setKeepAliveTime(schedulerKeepAliveTime, TimeUnit.MILLISECONDS);
		this.scheduler.allowCoreThreadTimeOut(true);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new RegisterClientThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(this.scheduler)
				.build();

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new InitReadFeedbackThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
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
		this.isDown.set(true);
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		this.pool.shutdown(timeout);
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
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
	protected void execute(Thread thread)
	{
		this.pool.execute(thread);
	}

	/*
	 * The empty implementation to start the thread. 11/07/2014, Bing Li
	 */
	protected void execute(Runnable thread)
	{
		this.pool.execute(thread);
	}
}
