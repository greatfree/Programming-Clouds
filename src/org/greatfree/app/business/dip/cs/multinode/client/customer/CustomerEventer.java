package org.greatfree.app.business.dip.cs.multinode.client.customer;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.cs.business.PlaceOrderNotification;
import org.greatfree.chat.message.cs.business.PutIntoCartNotification;
import org.greatfree.chat.message.cs.business.RemoveFromCartNotification;
import org.greatfree.client.AsyncRemoteEventer;
import org.greatfree.client.FreeClientPool;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.dip.p2p.RegistryConfig;

/*
 * This is the eventer for a customer to support the online sales. It is implemented over the DIP of the C/S Chatting. 12/21/2017, Bing Li
 */

// Created: 12/21/2017, Bing Li
public class CustomerEventer
{
	// The notification of PutIntoCartNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private AsyncRemoteEventer<PutIntoCartNotification> putIntoCartNotificationEventer;
	// The notification of RemoveFromCartNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private AsyncRemoteEventer<RemoveFromCartNotification> removeFromCartNotificationEventer;
	// The notification of PlaceOrderNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private AsyncRemoteEventer<PlaceOrderNotification> placeOrderNotificationEventer;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private CustomerEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static CustomerEventer instance = new CustomerEventer();
	
	public static CustomerEventer CE()
	{
		if (instance == null)
		{
			instance = new CustomerEventer();
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

		// Shutdown the eventer that sends the notification of PutIntoCartNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.putIntoCartNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of RemoveFromCartNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.removeFromCartNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of PlaceOrderNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.placeOrderNotificationEventer.dispose();
		
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

		// Initialize the asynchronous eventer for PutIntoCartNotification. 04/27/2017, Bing Li
		this.putIntoCartNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<PutIntoCartNotification>()
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

		// Initialize the asynchronous eventer for RemoveFromCartNotification. 04/27/2017, Bing Li
		this.removeFromCartNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<RemoveFromCartNotification>()
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

		// Initialize the asynchronous eventer for PlaceOrderNotification. 04/27/2017, Bing Li
		this.placeOrderNotificationEventer = new AsyncRemoteEventer.AsyncRemoteEventerBuilder<PlaceOrderNotification>()
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
	 * Notify the server about the merchandise to be ordered. 12/21/2017, Bing Li
	 */
	public void notifyPutIntoCart(String customerKey, String vendorKey, String mcKey, int count)
	{
		if (!this.putIntoCartNotificationEventer.isReady())
		{
			this.pool.execute(this.putIntoCartNotificationEventer);
		}
		this.putIntoCartNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PutIntoCartNotification(customerKey, vendorKey, mcKey, count));
	}

	/*
	 * Notify the server that a certain number of one type of merchandises are removed from the cart. 12/22/2017, Bing Li
	 */
	public void notifyRemoveFromCart(String customerKey, String vendorKey, String merchandiseKey, int count)
	{
		if (!this.removeFromCartNotificationEventer.isReady())
		{
			this.pool.execute(this.removeFromCartNotificationEventer);
		}
		this.removeFromCartNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new RemoveFromCartNotification(customerKey, vendorKey, merchandiseKey, count));
	}

	/*
	 * Notify the server to place an order. 12/22/2017, Bing Li
	 */
	public void notifyPlaceOrder(String vendorKey, String customerKey)
	{
		if (!this.placeOrderNotificationEventer.isReady())
		{
			this.pool.execute(this.placeOrderNotificationEventer);
		}
		this.placeOrderNotificationEventer.notify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new PlaceOrderNotification(vendorKey, customerKey));
	}
}
