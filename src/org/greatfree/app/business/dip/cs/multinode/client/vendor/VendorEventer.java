package org.greatfree.app.business.dip.cs.multinode.client.vendor;

import java.io.IOException;

import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.cs.business.PostMerchandiseNotification;
import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.framework.p2p.RegistryConfig;

/*
 * This is the eventer for a vendor to support the online sales. It is implemented over the DIP of the C/S Chatting. 12/21/2017, Bing Li
 */

// Created: 12/21/2017, Bing Li
public class VendorEventer
{
	// The notification of PostMerchandiseNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private AsyncRemoteEventer<PostMerchandiseNotification> postMerchandiseNotificationEventer;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private VendorEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static VendorEventer instance = new VendorEventer();
	
	public static VendorEventer VE()
	{
		if (instance == null)
		{
			instance = new VendorEventer();
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
		this.clientPool.dispose();

		// Shutdown the eventer that sends the notification of PostMerchandiseNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.postMerchandiseNotificationEventer.dispose();
		
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

		// Initialize the asynchronous eventer for PostMerchandiseNotification. 04/27/2017, Bing Li
		this.postMerchandiseNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<PostMerchandiseNotification>()
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
	}

	/*
	 * Post one new merchandise to the server. 12/21/2017, Bing Li
	 */
	public void notifyNewMerchandises(String vendorKey, String vendorName, Merchandise mc)
	{
		if (!this.postMerchandiseNotificationEventer.isReady())
		{
			this.pool.execute(this.postMerchandiseNotificationEventer);
		}
		this.postMerchandiseNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PostMerchandiseNotification(vendorKey, vendorName, mc));
	}
}
