package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.client.ClientPoolSingleton;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.concurrency.ThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.testing.message.OnlineNotification;
import org.greatfree.testing.message.RegisterClientNotification;
import org.greatfree.testing.message.UnregisterClientNotification;
import org.greatfree.util.NodeID;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class DNEventer
{
	// The IP of the coordinator the eventer needs to notify. 11/23/2014, Bing Li
	private String ip;
	// The port of the coordinator the eventer needs to notify. 11/23/2014, Bing Li
	private int port;
	// The thread pool that starts up the asynchronous eventer. 11/23/2014, Bing Li
	private ThreadPool pool;
	// The synchronous eventer notifies the coordinator that a DN is online. After receiving the notification, the coordinator can assign tasks and interact with the DN. 11/23/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineEventer;
	// The synchronous eventer notifies the coordinator that a DN needs to register. 11/23/2014, Bing Li
	private SyncRemoteEventer<RegisterClientNotification> registerClientEventer;
	// The synchronous eventer notifies the coordinator that a DB needs to unregister. 11/23/2014, Bing Li
	private SyncRemoteEventer<UnregisterClientNotification> unregisterClientEventer;

	private DNEventer()
	{
	}

	/*
	 * Initialize a singleton. 11/23/2014, Bing Li
	 */
	private static DNEventer instance = new DNEventer();
	
	public static DNEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new DNEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventer. 11/23/2014, Bing Li
	 */
	public void dispose(long timeout) throws InterruptedException, IOException
	{
		// Dispose the online eventer. 11/23/2014, Bing Li
		this.onlineEventer.dispose();
		// Dispose the registering eventer. 11/23/2014, Bing Li
		this.registerClientEventer.dispose();
		// Dispose the unregistering eventer. 11/23/2014, Bing Li
		this.unregisterClientEventer.dispose();
		
		// Shutdown the thread pool. 11/23/2014, Bing Li
		this.pool.shutdown(timeout);
	}

	/*
	 * Initialize the eventer. The IP/port is the coordinator to be notified. 11/23/2014, Bing Li
	 */
	public void init(String ipAddress, int port)
	{
		// Assign the IP. 11/23/2014, Bing Li
		this.ip = ipAddress;
		// Assign the port. 11/23/2014, Bing Li
		this.port = port;
		// Initialize a thread pool. 11/23/2014, Bing Li
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);

		this.onlineEventer = new SyncRemoteEventer<OnlineNotification>(ClientPoolSingleton.SERVER().getPool());
		this.registerClientEventer = new SyncRemoteEventer<RegisterClientNotification>(ClientPoolSingleton.SERVER().getPool());
		this.unregisterClientEventer = new SyncRemoteEventer<UnregisterClientNotification>(ClientPoolSingleton.SERVER().getPool());
	}

	/*
	 * Notify the coordinator that the DN is online. 11/23/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineEventer.notify(this.ip, this.port, new OnlineNotification());
	}
	
	/*
	 * Register the DN. 11/23/2014, Bing Li
	 */
	public void register() throws IOException, InterruptedException
	{
		this.registerClientEventer.notify(this.ip, this.port, new RegisterClientNotification(NodeID.DISTRIBUTED().getKey()));
	}
	
	/*
	 * Unregister the DN. 11/23/2014, Bing Li
	 */
	public void unregister() throws IOException, InterruptedException
	{
		this.unregisterClientEventer.notify(this.ip, this.port, new UnregisterClientNotification(NodeID.DISTRIBUTED().getKey()));
	}
}
