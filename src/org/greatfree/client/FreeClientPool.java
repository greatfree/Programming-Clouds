package org.greatfree.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;

import org.greatfree.message.ServerMessage;
import org.greatfree.reuse.RetrievablePool;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

/*
 * The pool, RetrievablePool, is mainly used for the resource of FreeClient. Some problems exist when instances of FreeClient are exposed outside since they might be disposed inside in the pool.
 * 
 * It is a better solution to wrap the instances of FreeClient and the management on them. The stuffs should be invisible to outside. For that, a new pool, FreeClientPool, is proposed. 11/19/2014, Bing Li
 */

public class FreeClientPool
{
	// Declare an instance of Retrievable to hide instances of FreeClient from outside. 11/19/2014, Bing Li
	private RetrievablePool<IPResource, FreeClient, FreeClientCreator, FreeClientDisposer> pool;

	/*
	 * Initialize the pool. 11/19/2014, Bing Li
	 */
	public FreeClientPool(int poolSize)
	{
		this.pool = new RetrievablePool<IPResource, FreeClient, FreeClientCreator, FreeClientDisposer>(poolSize, new FreeClientCreator(), new FreeClientDisposer());
	}

	/*
	 * Dispose the resource, i.e., the pool in the case. 11/20/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		this.pool.shutdown();
	}

	/*
	 * Set the idle checking. 11/20/2014, Bing Li
	 */
	public void setIdleChecker(long delay, long period, long maxIdleTime)
	{
		this.pool.setIdleChecker(delay, period, maxIdleTime);
	}

	/*
	 * Send a message to an IP/port which is enclosed in the instance of IPPort. The method wraps three steps as below. Thus, those stuffs are invisible to users such that it avoids possible conflicts to share instances of FreeClient. 11/20/2014, Bing Li
	 * 
	 * 		Getting an instance of FreeClient
	 * 		
	 * 		Sending the message
	 * 		
	 * 		Collecting the instance of FreeClient.
	 */
	public void send(IPResource ipPort, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(ipPort);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
//			System.out.println("FreeClientPool-send(): client is obtained!");
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
		else
		{
			System.out.println("FreeClientPool-send(): client is not obtained!");
		}
	}

	/*
	 * Send a message to an IP/port. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String ip, int port, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the IP/port. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(new IPResource(ip, port));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
	}

	/*
	 * Send a message to a remote node by its key. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String clientKey, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(clientKey);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
//			System.out.println("FreeClientPool-send(): " + client);
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
		}
	}

	/*
	 * Send a request message to a remote node in the form of IPPort and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(IPResource ipPort, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(ipPort);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	/*
	 * Send a request message to a remote node in the form of IP/port and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(String ip, int port, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(new IPResource(ip, port));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	public ServerMessage request(String ip, int port, ServerMessage req, int timeout) throws IOException, ClassNotFoundException, SocketTimeoutException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(new IPResource(ip, port, timeout));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	/*
	 * Send a request message to a remote node by its client key and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public ServerMessage request(String clientKey, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.pool.get(clientKey);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != UtilConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.pool.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}

	/*
	 * Add IP address to the TCP client pool. 05/08/2017, Bing Li
	 */
//	public void addIP(String clientKey, String ip, int port)
	public void addIP(String ip, int port)
	{
		this.pool.addSource(new IPResource(ip, port));
	}

	/*
	public void addIP(String peerKey, String peerName, String ip, int port)
	{
//		this.pool.addSource(new IPResource(new IPAddress(peerKey, peerName, ip, port)));
		this.pool.addSource(new IPResource(peerKey, peerName, ip, port));
	}
	*/

	public void clearAll()
	{
		this.pool.clearSource();
	}
	
	public void removeSource(String clientKey)
	{
		this.pool.removeSource(clientKey);
	}
	
	/*
	 * Remove the client that leaves the cluster. 10/02/2018, Bing Li
	 */
	public void removeIP(String clientKey) throws IOException
	{
		this.pool.removeResource(clientKey);
	}

	/*
	 * Check whether the client is existed. 11/20/2014, Bing Li
	 */
	public boolean isSourceExisted(String ip, int port)
	{
		// Check whether the source to create the instance of FreeClient is existed. 11/20/2014, Bing Li
		return this.pool.isSourceExisted(new IPResource(ip, port));
	}
	
	/*
	 * Check whether the client is existed. 11/20/2014, Bing Li
	 */
	public boolean isClientExisted(String key)
	{
		return this.pool.isResourceExisted(key);
	}

	/*
	 * Get the IP of a client key. 11/20/2014, Bing Li
	 */
	public String getIP(String clientKey)
	{
		return this.pool.getSource(clientKey).getIP();
	}

	/*
	 * Get the IPPort of a client key. 05/12/2017, Bing Li
	 */
	public IPAddress getIPAddress(String clientKey)
	{
		return this.pool.getSource(clientKey).getAddress();
	}

	/*
	 * Get the client count in the pool. 11/20/2014, Bing Li
	 */
	public int getClientSourceSize()
	{
		return this.pool.getSourceSize();
	}
	
	public int getClientSize()
	{
		return this.pool.getClientSize();
	}

	/*
	 * Get the instance of IPPort by the client key. 11/20/2014, Bing Li
	 */
	public IPResource getIPPort(String clientKey)
	{
		return this.pool.getSource(clientKey);
	}

	/*
	 * Get the client keys in the pool. 11/20/2014, Bing Li
	 */
	public Set<String> getClientKeys()
	{
		return this.pool.getAllObjectKeys();
	}

	/*
	 * Get the count of the clients which are working. 11/20/2014, Bing Li
	 */
	public int getBusyClientCount()
	{
		return this.pool.getBusyResourceSize();
	}
	
	/*
	 * Get the count of the clients which are idle. 11/20/2014, Bing Li
	 */
	public int getIdleClientCount()
	{
		return this.pool.getIdleResourceSize();
	}

	/*
	 * Remote a client by its client key. 11/20/2014, Bing Li
	 */
	public void removeClient(String clientKey) throws IOException
	{
		this.pool.removeResource(clientKey);
	}
}
