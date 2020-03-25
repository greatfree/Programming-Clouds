package org.greatfree.dip.admin;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.dip.multicast.MulticastConfig;
import org.greatfree.dip.multicast.message.ShutdownChildrenAdminNotification;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.dip.p2p.message.ChatPartnerResponse;
import org.greatfree.dip.streaming.message.ShutdownPubSubNotification;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

/*
 * This is an eventer that sends notifications to the chatting server for administration in a synchronous or asynchronous manner. 04/17/2014, Bing Li
*/

// Created: 04/17/2017, Bing Li
class ChatAdminEventer
{
	// The notification of ShutdownChatServerNotification is sent to the chatting server in an asynchronous manner. 04/17/2017, Bing Li
	private AsyncRemoteEventer<ShutdownServerNotification> shutdownChattingServerNotificationEventer;
	// The notification of ShutdownChildrenAdminNotification is sent to the cluster root in an asynchronous manner. 04/17/2017, Bing Li
	private AsyncRemoteEventer<ShutdownChildrenAdminNotification> shutdownClusterChildrenNotificationEventer;
	
	private AsyncRemoteEventer<ShutdownPubSubNotification> shutdownPubSubNotificationEventer;
	
	
	// The thread pool that starts up the asynchronous eventer. 04/17/2017, Bing Li
	private ThreadPool pool;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	private ChatAdminEventer()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static ChatAdminEventer instance = new ChatAdminEventer();
	
	public static ChatAdminEventer RE()
	{
		if (instance == null)
		{
			instance = new ChatAdminEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventer. 11/27/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException, IOException
	{
		// Shutdown the client pool. 05/26/2017, Bing Li
		this.clientPool.dispose();
		// Shutdown the eventer that sends the notification of ShutdownChatServerNotification to the CS chatting server in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownChattingServerNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownChildrenAdminNotification to the cluster root in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownClusterChildrenNotificationEventer.dispose();
		
		this.shutdownPubSubNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		 // Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		// Initialize the thread pool. 05/27/2017, Bing Li
		this.pool = new ThreadPool(RegistryConfig.EVENTER_THREAD_POOL_SIZE, RegistryConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the asynchronous eventer for ShutdownChatServerNotification. 04/27/2017, Bing Li
		this.shutdownChattingServerNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownServerNotification>()
//				.threadPool(this.pool)
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
		
		// Initialize the asynchronous eventer for ShutdownChildrenAdminNotification. 04/27/2017, Bing Li
		this.shutdownClusterChildrenNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownChildrenAdminNotification>()
//				.threadPool(this.pool)
				.clientPool(this.clientPool)
				.eventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		this.shutdownPubSubNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<ShutdownPubSubNotification>()
				.clientPool(this.clientPool)
				.eventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.eventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.eventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.eventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.waitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.idleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.idleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(ClientConfig.SCHEDULER_POOL_SIZE)
				.schedulerKeepAliveTime(ClientConfig.SCHEDULER_KEEP_ALIVE_TIME)
				.schedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();
	}
	
	/*
	 * Notify the chatting server to shut down. 04/17/2017, Bing Li
	 */
	public void notifyShutdownCSChatServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.shutdownChattingServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.shutdownChattingServerNotificationEventer);
		}
		// Send the notification. 04/17/2017, Bing Li
		this.shutdownChattingServerNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_ADMIN_PORT, new ShutdownServerNotification());
//		this.shutdownChattingServerNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ShutdownChatServerNotification());
	}

	/*
	 * Notify the chatting registry server to shutdown. 05/01/2017, Bing Li
	 */
	public void notifyShutdownChattingRegistryServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.shutdownChattingServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.shutdownChattingServerNotificationEventer);
		}
		// Send the notification. 04/17/2017, Bing Li
		this.shutdownChattingServerNotificationEventer.notify(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_ADMIN_PORT, new ShutdownServerNotification());
//		this.shutdownChattingServerNotificationEventer.notify(ChatConfig.CHAT_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ShutdownChatServerNotification());
	}

	/*
	 * Notify the chatting peer to shutdown. 05/01/2017, Bing Li
	 */
	public void notifyShutdownChattingPeer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.shutdownChattingServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.shutdownChattingServerNotificationEventer);
		}
		// Send the notification. 04/17/2017, Bing Li
//		this.shutdownServerNotificationEventer.notify(ChatConfig.CHAT_REGISTRY_ADDRESS, ChatConfig.CHAT_ADMIN_PORT + 5, new ShutdownChatServerNotification());
		this.shutdownChattingServerNotificationEventer.notify(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ShutdownServerNotification());
	}
	
	/*
	 * Notify the multicasting root to shutdown multicasting children. 05/20/2017, Bing Li
	 */
	public void notifyShutdownMulticastChildren()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.shutdownClusterChildrenNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.shutdownClusterChildrenNotificationEventer);
		}
		
		// Get the cluster root's IP and port. 05/20/2017, Bing Li
		ChatPartnerResponse response = ChatAdminReader.RR().searchUser(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
		
		// Send the notification. 04/17/2017, Bing Li
//		this.shutdownServerNotificationEventer.notify(ChatConfig.CHAT_REGISTRY_ADDRESS, ChatConfig.CHAT_ADMIN_PORT + 5, new ShutdownChatServerNotification());
//		this.shutdownClusterChildrenNotificationEventer.notify(ClusterConfig.CLUSTR_ROOT_IP, ChatConfig.CHAT_REGISTRY_PORT, new ShutdownChildrenAdminNotification());
		this.shutdownClusterChildrenNotificationEventer.notify(response.getIP(), response.getPort(), new ShutdownChildrenAdminNotification());
	}

	/*
	 * Shutdown the multicasting root. 05/20/2017, Bing Li
	 */
	public void notifyShutdownMulticastRoot()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 04/17/2017, Bing Li
		if (!this.shutdownChattingServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 04/17/2017, Bing Li
			this.pool.execute(this.shutdownChattingServerNotificationEventer);
		}
		// Get the cluster root's IP and port. 05/20/2017, Bing Li
		ChatPartnerResponse response = ChatAdminReader.RR().searchUser(MulticastConfig.CLUSTER_SERVER_ROOT_KEY);
		// Send the notification. 04/17/2017, Bing Li
		this.shutdownChattingServerNotificationEventer.notify(response.getIP(), response.getPort(), new ShutdownServerNotification());
	}
	
	public void notifyPubSubServer()
	{
		if (!this.shutdownPubSubNotificationEventer.isReady())
		{
			this.pool.execute(this.shutdownPubSubNotificationEventer);
		}
		
		IPAddress ip = ChatAdminReader.RR().getPubSubServerIP();
		if (ip != UtilConfig.NO_IP_ADDRESS)
		{
			this.shutdownPubSubNotificationEventer.notify(ip.getIP(), ip.getPort(), new ShutdownPubSubNotification());
		}
	}
}
