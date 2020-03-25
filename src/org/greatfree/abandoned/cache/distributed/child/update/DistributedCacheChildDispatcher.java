package org.greatfree.abandoned.cache.distributed.child.update;

import java.util.Calendar;

import org.greatfree.cache.message.CacheMessageType;
import org.greatfree.cache.message.update.PutNotification;
import org.greatfree.client.OutMessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 07/17/2017, Bing Li
public class DistributedCacheChildDispatcher extends ServerDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process unicast messages to the child when such a notification is received. 05/19/2016, Bing Li
	private NotificationDispatcher<PutNotification, PutNotificationThread, PutNotificationThreadCreator> putNotificationDispatcher;

//	public DistributedCacheChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public DistributedCacheChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, HelloWorldAnycastNotification. 11/27/2014, Bing Li
		this.putNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PutNotification, PutNotificationThread, PutNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PutNotificationThreadCreator())
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
	/*
	public void shutdown(long timeout) throws InterruptedException
	{
		this.putNotificationDispatcher.dispose();
		
		super.shutdown(timeout);
	}
	*/
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
	/*
	public void consume(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CacheMessageType.PUT_NOTIFICATION:
				System.out.println("PUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.putNotificationDispatcher.isReady())
				{
					super.execute(this.putNotificationDispatcher);
				}
				this.putNotificationDispatcher.enqueue((PutNotification)message.getMessage());
				break;
		}
	}
	*/

	/*
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.putNotificationDispatcher.dispose();
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CacheMessageType.PUT_NOTIFICATION:
				System.out.println("PUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.putNotificationDispatcher.isReady())
				{
					super.execute(this.putNotificationDispatcher);
				}
				this.putNotificationDispatcher.enqueue((PutNotification)message.getMessage());
				break;
		}
	}
}
