package org.greatfree.client;

import java.io.IOException;
import java.util.Set;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;

/*
 * The eventer sends notifications to remote servers in a synchronous manner without waiting for responses. The sending method, notify(), are blocking. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class SyncRemoteEventer<Notification extends ServerMessage>
{
	// The pool for FreeClient is needed to issue relevant clients to send notifications. 11/05/2014, Bing Li
	private FreeClientPool clientPool;

//	private final static Logger log = Logger.getLogger("org.greatfree.client");

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

	/*
	 * The IPResource is weird for programmers. It is better to hide it. 09/01/2022, Bing Li
	 * 
	public void notify(IPResource ipPort, Notification message) throws IOException
	{
		this.clientPool.send(ipPort, message);
	}
	*/
	
	public int getClientSize()
	{
		return this.clientPool.getClientSourceSize();
	}
	
	public Set<String> getClientKeys()
	{
		return this.clientPool.getClientKeys();
	}

	/*
	 * Get the specified size of children. 09/11/2020, Bing Li
	 */
	public Set<String> getClientKeys(int n)
	{
//		log.info("SyncRemoteEventer-getClientKeys(): n = " + n);
		Set<String> childrenKeys = this.clientPool.getClientKeys();
//		log.info("SyncRemoteEventer-getClientKeys(): childrenKeys size = " + this.clientPool.getClientKeys().size());
		if (n >= childrenKeys.size())
		{
			return childrenKeys;
		}
		else
		{
			return Rand.getRandomSet(childrenKeys, n);
		}
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
