package org.greatfree.chat.client.business.dip;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.ChatTools;
import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.dsf.cs.multinode.message.AddPartnerNotification;
import org.greatfree.dsf.cs.multinode.message.ChatNotification;
import org.greatfree.dsf.p2p.RegistryConfig;

// Created: 04/23/2017, Bing Li
public class ChatEventer
{
	// The notification of AddFriendNotification is sent to the chatting server in an asynchronous manner. 04/17/2017, Bing Li
	private AsyncRemoteEventer<AddPartnerNotification> addFriendNotificationEventer;
	// The notification of ChatNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private AsyncRemoteEventer<ChatNotification> chatNotificationEventer;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private ChatEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static ChatEventer instance = new ChatEventer();
	
	public static ChatEventer RE()
	{
		if (instance == null)
		{
			instance = new ChatEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose(long timeout) throws InterruptedException, IOException
	{
		// Shutdown the client pool. 05/26/2017, Bing Li
//		ChatClientPool.CLIENT().dispose();
		this.clientPool.dispose();
		// Shutdown the eventer that sends the notification of AddFriendNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.addFriendNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ChatNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.chatNotificationEventer.dispose();
		
		// Shutdown the thread pool. 11/07/2014, Bing Li
		this.pool.shutdown(timeout);
	}
	
	/*
	 * Initialize the eventers. 11/07/2014, Bing Li
	 */
	public void init()
	{
		 // Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		// Initialize the thread pool. 05/27/2017, Bing Li
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		// Initialize the asynchronous eventer for AddFriendNotification. 04/27/2017, Bing Li
		this.addFriendNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<AddPartnerNotification>()
//				.threadPool(this.pool)
//				.clientPool(ChatClientPool.CLIENT().getPool())
				.clientPool(this.clientPool)
				.eventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.scheduler(Scheduler.GREATFREE().getSchedulerPool())
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		// Initialize the asynchronous eventer for ChatNotification. 04/27/2017, Bing Li
		this.chatNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ChatNotification>()
//				.threadPool(this.pool)
//				.clientPool(ChatClientPool.CLIENT().getPool())
				.clientPool(this.clientPool)
				.eventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.scheduler(Scheduler.GREATFREE().getSchedulerPool())
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

	}

	/*
	 * Notify the chatting server to shut down the chatting server. 04/17/2017, Bing Li
	 */
	public void notifyAddFriend()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.addFriendNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.addFriendNotificationEventer);
		}
		// Send the notification. 04/17/2017, Bing Li
		this.addFriendNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new AddPartnerNotification(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getPartnerKey(), "Hello!"));
	}

	/*
	 * Notify the chatting server with a new chatting message. 04/17/2017, Bing Li
	 */
	public void notifyChat(String message)
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.chatNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.chatNotificationEventer);
		}
		// Send the notification. 04/17/2017, Bing Li
		this.chatNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ChatNotification(ChatTools.getChatSessionKey(ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getPartnerKey()), message, ChatMaintainer.CS().getLocalUserKey(), ChatMaintainer.CS().getLocalUsername(), ChatMaintainer.CS().getPartnerKey(), ChatMaintainer.CS().getPartner()));
	}
}
