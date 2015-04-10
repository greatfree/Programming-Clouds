package com.greatfree.testing.client;

import java.io.IOException;

import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.SyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.message.OnlineNotification;
import com.greatfree.testing.message.RegisterClientNotification;
import com.greatfree.testing.message.UnregisterClientNotification;
import com.greatfree.util.NodeID;

/*
 * The class is an example that applies SynchRemoteEventer and AsyncRemoteEventer. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class ClientEventer
{
	// Declare the ip of the remote server. 11/07/2014, Bing Li
	private String ip;
	// Declare the port of the remote server. 11/07/2014, Bing Li
	private int port;
	// The eventer to send the online notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineEventer;
	// The eventer to send the registering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<RegisterClientNotification> registerClientEventer;
	// The eventer to send the unregistering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<UnregisterClientNotification> unregisterClientEventer;

	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private ClientEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static ClientEventer instance = new ClientEventer();
	
	public static ClientEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new ClientEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventers. 11/07/2014, Bing Li
	 */
	public void dispose()
	{
		this.onlineEventer.dispose();
		this.registerClientEventer.dispose();
		this.unregisterClientEventer.dispose();

		// Shutdown the thread pool. 11/07/2014, Bing Li
		this.pool.shutdown();
	}

	/*
	 * Initialize the eventers. 11/07/2014, Bing Li
	 */
	public void init(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		this.onlineEventer = new SyncRemoteEventer<OnlineNotification>(ClientPool.LOCAL().getPool());
		this.registerClientEventer = new SyncRemoteEventer<RegisterClientNotification>(ClientPool.LOCAL().getPool());
		this.unregisterClientEventer = new SyncRemoteEventer<UnregisterClientNotification>(ClientPool.LOCAL().getPool());
	}

	/*
	 * Send the online notification to the remote server. 11/07/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineEventer.notify(this.ip, this.port, new OnlineNotification());
	}

	/*
	 * Send the registering notification to the remote server. 11/07/2014, Bing Li
	 */
	public void register()
	{
		try
		{
			this.registerClientEventer.notify(this.ip, this.port, new RegisterClientNotification(NodeID.DISTRIBUTED().getKey()));
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
	 * Send the unregistering notification to the remote server. 11/07/2014, Bing Li
	 */
	public void unregister()
	{
		try
		{
			this.unregisterClientEventer.notify(this.ip, this.port, new UnregisterClientNotification(NodeID.DISTRIBUTED().getKey()));
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
