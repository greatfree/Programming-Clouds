package org.greatfree.client;

import java.io.IOException;
import java.util.Set;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/*
 * The pool, RetrievablePool, is mainly used for the resource of FreeClient. Some problems exist when instances of FreeClient are exposed outside since they might be disposed inside in the pool.
 * 
 * It is a better solution to wrap the instances of FreeClient and the management on them. The stuffs should be invisible to outside. For that, a new pool, FreeClientPool, is proposed. 11/19/2014, Bing Li
 */

public class FreeClientPool
{
//	private final static Logger log = Logger.getLogger("org.greatfree.client");

	// Declare an instance of Retrievable to hide instances of FreeClient from outside. 11/19/2014, Bing Li
//	private RetrievablePool<IPResource, FreeClient, FreeClientCreator, FreeClientDisposer> pool;
	private ClientManager manager;

	/*
	 * Initialize the pool. 11/19/2014, Bing Li
	 */
	public FreeClientPool(int poolSize)
	{
//		this.pool = new RetrievablePool<IPResource, FreeClient, FreeClientCreator, FreeClientDisposer>(poolSize, new FreeClientCreator(), new FreeClientDisposer());
		this.manager = new ClientManager(poolSize);
	}
	
	/*
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Dispose the resource, i.e., the pool in the case. 11/20/2014, Bing Li
	 */
	public synchronized void dispose()
	{
//		this.pool.shutdown();
		this.manager.shutdown();
	}
	
	public ClientManager getManager()
	{
		return this.manager;
	}

	/*
	 * Set the idle checking. 11/20/2014, Bing Li
	 */
	public void setIdleChecker(long delay, long period, long maxIdleTime)
	{
//		this.pool.setIdleChecker(delay, period, maxIdleTime);
		this.manager.setIdleChecker(delay, period, maxIdleTime);
	}

	/*
	 * 
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
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
//		FreeClient client = this.pool.get(ipPort);
		FreeClient client = this.manager.get(ipPort, false);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
//			log.info("notification is starting to send ...");
//			System.out.println("FreeClientPool-send(): client is obtained!");
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
//			log.info("notification is sent ...");
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
		}
		else
		{
//			log.info("FreeClientPool-send(): client is not obtained!");
		}
	}

	/*
	 *
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Send a message to an IP/port. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String ip, int port, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the IP/port. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(new IPResource(ip, port), false);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
		}
	}

	/*
	 * 
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Send a message to a remote node by its key. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	public void send(String clientKey, ServerMessage msg) throws IOException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(clientKey, false);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
//			log.info("server address: " + client.getServerAddress() + ", port = " + client.getServerPort());
//			System.out.println("FreeClientPool-send(): " + client);
			// Send the message. 11/20/2014, Bing Li
			client.send(msg);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
		}
	}

	/*
	 * 
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Send a request message to a remote node in the form of IPPort and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	/*
	public ServerMessage request(IPResource ipPort, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(ipPort);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}
	*/

	/*
	 * 
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Send a request message to a remote node in the form of IP/port and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	/*
	public ServerMessage request(String ip, int port, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(new IPResource(ip, port));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}
	*/

	/*
	 *
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 */
	/*
	public ServerMessage request(String ip, int port, ServerMessage req, int timeout) throws IOException, ClassNotFoundException, SocketTimeoutException
	{
		// Get an instance of FreeClient by the instance of IPPort. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(new IPResource(ip, port, timeout));
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}
	*/

	/*
	 * 
	 * The problem is not resolved through adding synchronized. So it is removed again. The problem is caused by the sharing of FreeClientPool between the eventer and the reader. 10/10/2022, Bing Li
	 * 
	 * The synchronized modifier is placed for the method to ensure data is sent out safely before closing. 10/10/2022, Bing Li
	 * 
	 * Send a request message to a remote node by its client key and then wait until its corresponding response is received. It also ensures initializing, sending and collecting are invisible to users. 11/20/2014, Bing Li
	 */
	/*
	public ServerMessage request(String clientKey, ServerMessage req) throws IOException, ClassNotFoundException
	{
		// Get an instance of FreeClient by the client key. 11/20/2014, Bing Li
		FreeClient client = this.manager.get(clientKey);
		// Check whether the instance of FreeClient is valid. 11/20/2014, Bing Li
		if (client != ClientConfig.NO_CLIENT)
		{
			// Send the message and wait until the corresponding response is received. 11/20/2014, Bing Li
			ServerMessage res = client.sendWithResponse(req);
			// Collect the instance of FreeClient. 11/20/2014, Bing Li
			this.manager.collect(client);
			// Return the response. 11/20/2014, Bing Li
			return res;
		}
		// If the instance of FreeClient is not valid, return null. 11/20/2014, Bing Li
		return null;
	}
	*/

	/*
	 * The method is used frequently for unicasting. 09/24/2022, Bing Li
	 */
	public IPAddress getClosestIP(String characterKey)
	{
		return this.getIPAddress(Tools.getClosestKey(characterKey, this.manager.getAllObjectKeys()));
	}

	/*
	 * Add IP address to the TCP client pool. 05/08/2017, Bing Li
	 */
//	public void addIP(String clientKey, String ip, int port)
	public void addIP(String ip, int port)
	{
//		IPResource ir = new IPResource(ip, port);
//		log.info("IP key = " + ir.getObjectKey());
		this.manager.addIPResource(new IPResource(ip, port));
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
		this.manager.clear();
	}
	
	public void removeSource(String clientKey)
	{
		this.manager.removeIPResource(clientKey);
	}
	
	/*
	 * Remove the client that leaves the cluster. 10/02/2018, Bing Li
	 */
	public void removeIP(String clientKey) throws IOException
	{
		this.manager.removeClient(clientKey);
	}

	/*
	 * Check whether the client is existed. 11/20/2014, Bing Li
	 */
	public boolean isSourceExisted(String ip, int port)
	{
		// Check whether the source to create the instance of FreeClient is existed. 11/20/2014, Bing Li
		return this.manager.isSourceExisted(new IPResource(ip, port));
	}
	
	/*
	 * Check whether the client is existed. 11/20/2014, Bing Li
	 */
	public boolean isClientExisted(String key)
	{
		return this.manager.isResourceExisted(key);
	}

	/*
	 * Get the IP of a client key. 11/20/2014, Bing Li
	 */
	public String getIP(String clientKey)
	{
		return this.manager.getIPResource(clientKey).getIP();
	}

	/*
	 * Get the IPPort of a client key. 05/12/2017, Bing Li
	 */
	public IPAddress getIPAddress(String clientKey)
	{
		return this.manager.getIPResource(clientKey).getAddress();
	}

	/*
	 * Get the client count in the pool. 11/20/2014, Bing Li
	 */
	public int getClientSourceSize()
	{
		return this.manager.getSourceSize();
	}
	
	public int getClientSize()
	{
		return this.manager.getClientSize();
	}

	/*
	 * Get the instance of IPPort by the client key. 11/20/2014, Bing Li
	 */
	public IPResource getIPPort(String clientKey)
	{
		return this.manager.getIPResource(clientKey);
	}

	/*
	 * Get the client keys in the pool. 11/20/2014, Bing Li
	 */
	public Set<String> getClientKeys()
	{
		return this.manager.getAllObjectKeys();
	}

	/*
	 * Get the count of the clients which are working. 11/20/2014, Bing Li
	 */
	public int getBusyClientCount()
	{
		return this.manager.getBusyClientSize();
	}
	
	/*
	 * Get the count of the clients which are idle. 11/20/2014, Bing Li
	 */
	public int getIdleClientCount()
	{
		return this.manager.getIdleClientSize();
	}

	/*
	 * Remote a client by its client key. 11/20/2014, Bing Li
	 */
	public void removeClient(String clientKey) throws IOException
	{
		this.manager.removeClient(clientKey);
	}
	
	public void remove(String clientKey)
	{
		this.manager.removeIPResource(clientKey);
	}
}
