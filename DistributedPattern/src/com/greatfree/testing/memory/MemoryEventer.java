package com.greatfree.testing.memory;

import java.io.IOException;

import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.SyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.message.OnlineNotification;
import com.greatfree.testing.message.RegisterMemoryServerNotification;
import com.greatfree.testing.message.UnregisterMemoryServerNotification;
import com.greatfree.util.NodeID;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryEventer
{
	// The IP of the coordinator the eventer needs to notify. 11/28/2014, Bing Li
	private String ip;
	// The port of the coordinator the eventer needs to notify. 11/28/2014, Bing Li
	private int port;
	// The thread pool that starts up the asynchronous eventer. 11/28/2014, Bing Li
	private ThreadPool pool;
	// The synchronous eventer notifies the coordinator that a crawler is online. After receiving the notification, the coordinator can assign tasks and interact with the crawler for crawling. 11/28/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineNotificationEventer;
	// The synchronous eventer notifies the coordinator that a memory server needs to register. 11/28/2014, Bing Li
	private SyncRemoteEventer<RegisterMemoryServerNotification> registerMemoryServerEventer;
	// The synchronous eventer notifies the coordinator that a memory server needs to unregister. 11/28/2014, Bing Li
	private SyncRemoteEventer<UnregisterMemoryServerNotification> unregisterMemoryServerEventer;

	private MemoryEventer()
	{
	}

	/*
	 * Initialize a singleton. 11/28/2014, Bing Li
	 */
	private static MemoryEventer instance = new MemoryEventer();
	
	public static MemoryEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new MemoryEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventer. 11/28/2014, Bing Li
	 */
	public void dispose()
	{
		// Dispose the online eventer. 11/28/2014, Bing Li
		this.onlineNotificationEventer.dispose();
		// Dispose the registering eventer. 11/28/2014, Bing Li
		this.registerMemoryServerEventer.dispose();
		// Dispose the unregistering eventer. 11/28/2014, Bing Li
		this.unregisterMemoryServerEventer.dispose();
		// Shutdown the thread pool. 11/28/2014, Bing Li
		this.pool.shutdown();
	}
	
	/*
	 * Initialize the eventer. The IP/port is the coordinator to be notified. 11/28/2014, Bing Li
	 */
	public void init(String ip, int port)
	{
		// Assign the IP. 11/28/2014, Bing Li
		this.ip = ip;
		// Assign the port. 11/28/2014, Bing Li
		this.port = port;
		// Initialize a thread pool. 11/28/2014, Bing Li
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		this.onlineNotificationEventer = new SyncRemoteEventer<OnlineNotification>(ClientPool.STORE().getPool());
		this.registerMemoryServerEventer = new SyncRemoteEventer<RegisterMemoryServerNotification>(ClientPool.STORE().getPool());
		this.unregisterMemoryServerEventer = new SyncRemoteEventer<UnregisterMemoryServerNotification>(ClientPool.STORE().getPool());
	}

	/*
	 * Notify the coordinator that the memory server is online. 11/28/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineNotificationEventer.notify(this.ip, this.port, new OnlineNotification());
	}

	/*
	 * Register the memory server. 11/28/2014, Bing Li
	 */
	public void register()
	{
		try
		{
			this.registerMemoryServerEventer.notify(this.ip, this.port, new RegisterMemoryServerNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Unregister the memory server. 11/23/2014, Bing Li
	 */
	public void unregister()
	{
		try
		{
			this.unregisterMemoryServerEventer.notify(this.ip, this.port, new UnregisterMemoryServerNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
