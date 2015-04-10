package com.greatfree.remote;

import java.io.IOException;

import com.greatfree.multicast.ServerMessage;

/*
 * The eventer sends notifications to remote servers in a synchronous manner without waiting for responses. The sending method, notify(), are blocking. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class SyncRemoteEventer<Notification extends ServerMessage>
{
	// The pool for FreeClient is needed to issue relevant clients to send notifications. 11/05/2014, Bing Li
	private FreeClientPool clientPool;

	/*
	 * Usually, the FreeClient pool is shared by multiple eventers. So, it is assigned to the eventer when initializing the eventer. 11/05/2014, Bing Li
	 */
	public SyncRemoteEventer(FreeClientPool clientPool)
	{
		this.clientPool = clientPool;
	}

	/*
	 * The resource consumed by the eventer is the FreeClient pool. Because it is shared, it should not be disposed here. Just leave the interface. 11/05/2014, Bing Li
	 */
	public void dispose()
	{
	}

	/*
	 * Send the notification to the remote server. Since no asynchronous mechanisms are available, the method is blocking. 11/05/2014, Bing Li
	 */
	public void notify(String ip, int port, Notification message) throws IOException, InterruptedException
	{
		// Send the notification. 11/23/2014, Bing Li
		this.clientPool.send(new IPPort(ip, port), message);
	}
}
