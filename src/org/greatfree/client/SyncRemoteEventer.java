package org.greatfree.client;

import java.io.IOException;
import java.util.Set;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

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
	public void dispose() throws IOException
	{
		this.clientPool.dispose();
	}
	
	public void clearIPs()
	{
		this.clientPool.clearAll();
	}
	
	public void addIP(String ip, int port)
	{
		this.clientPool.addIP(ip, port);
	}

	/*
	 * 
	 * I am trying to fix the possible bug in the method. 02/17/2019, Bing Li
	 * 
	 */
	/*
	 * Send the notification to the remote server. Since no asynchronous mechanisms are available, the method is blocking. 11/05/2014, Bing Li
	 */
	public void notify(String ip, int port, Notification message) throws IOException, InterruptedException
	{
		// Send the notification. 11/23/2014, Bing Li
		this.clientPool.send(new IPResource(ip, port), message);
	}
	
	public void notify(String clientKey, Notification message) throws IOException
	{
		this.clientPool.send(clientKey, message);
	}
	
	public void notify(IPResource ipPort, Notification message) throws IOException
	{
		this.clientPool.send(ipPort, message);
	}
	
	public int getClientSize()
	{
		return this.clientPool.getClientSourceSize();
	}
	
	public Set<String> getClientKeys()
	{
		return this.clientPool.getClientKeys();
	}
	
	public IPAddress getIPAddress(String clientKey)
	{
		return this.clientPool.getIPAddress(clientKey);
	}
	
	public void removeClient(String clientKey) throws IOException
	{
		this.clientPool.removeClient(clientKey);
	}
}
