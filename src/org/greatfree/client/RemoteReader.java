package org.greatfree.client;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageConfig;
import org.greatfree.util.UtilConfig;

/*
 * The class is responsible for sending a request to the remote end, waiting for the response and returning it to the local end which sends the request. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class RemoteReader
{
	// As a singleton in a process, it should avoid the RemoteReader to be initialized more than once. The flag indicates whether the class is initialized or not. 04/30/2017, Bing Li
	private AtomicBoolean isInitialized;

	// The pool to manage the instances of FreeClient. 11/06/2014, Bing Li
	private FreeReaderPool clientPool;

	/*
	 * Initialize. 11/06/2014, Bing Li
	 */
	private RemoteReader()
	{
		this.isInitialized = new AtomicBoolean(false);
	}

	// A singleton is defined. 11/06/2014, Bing Li
	private static RemoteReader instance = new RemoteReader();
	
	public static RemoteReader REMOTE()
	{
		if (instance == null)
		{
			instance = new RemoteReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Shutdown the reader. 11/07/2014, Bing Li
	 */
	public void shutdown() throws IOException
	{
		this.clientPool.shutdown();
	}

	/*
	 * Initialize the reader. 11/07/2014, Bing Li
	 */
	public void init(int poolSize)
	{
		if (!this.isInitialized.get())
		{
			this.clientPool = new FreeReaderPool(poolSize);
			this.isInitialized.set(true);
		}
	}

	/*
	 * Notify that the corresponding ObjectOutputStream is initialized. 11/07/2014, Bing Li
	 */
	public void notifyOutStreamDone()
	{
		this.clientPool.notifyOutStreamDone();
	}

	/*
	 * Send a request to the remote node at the IP address and the port number and wait until the response is received. 09/21/2014, Bing Li
	 * 
	 * The parameters:
	 * 
	 * 		String nodeKey: the local client key that is unique to differentiate it from others client on the remote server;
	 * 
	 * 		String ip: the IP address of the remote node;
	 * 
	 * 		int port: the port number of the remote node;
	 * 
	 * 		ServerMessage request: the request sent to the remote node;
	 * 
	 * The return value:
	 * 
	 * 		ServerMessage: the response to be received after the request is sent.
	 * 
	 */

	public ServerMessage read(String nodeKey, String ip, int port, ServerMessage request) throws RemoteReadException, IOException, ClassNotFoundException
	{
		// Get the instance of FreeClient by the IP address and the port number. 09/21/2014, Bing Li
		FreeClient client = this.clientPool.get(nodeKey, new IPResource(ip, port));
		if (client != UtilConfig.NO_CLIENT)
		{
			// The lock is hold by each particular instance of FreeClient. It guarantees that the sending, receiving and collecting become an atomic operation, which can never be interrupted by other concurrent operations of the client. 09/21/2014, Bing Li
			client.getLock().lock();
			try
			{
				return client.sendWithResponse(request);
			}
			finally
			{
				// Collect the client. 09/21/2014, Bing Li
				this.clientPool.collect(client);
				client.getLock().unlock();
			}
		}
		return SystemMessageConfig.NO_MESSAGE;
	}

	public ServerMessage read(String nodeKey, String ip, int port, ServerMessage request, int timeout) throws RemoteReadException, IOException, ClassNotFoundException, SocketTimeoutException
	{
		// Get the instance of FreeClient by the IP address and the port number. 09/21/2014, Bing Li
		FreeClient client = this.clientPool.get(nodeKey, new IPResource(ip, port, timeout));
		if (client != UtilConfig.NO_CLIENT)
		{
			// The lock is hold by each particular instance of FreeClient. It guarantees that the sending, receiving and collecting become an atomic operation, which can never be interrupted by other concurrent operations of the client. 09/21/2014, Bing Li
			client.getLock().lock();
			try
			{
				return client.sendWithResponse(request);
			}
			finally
			{
				// Collect the client. 09/21/2014, Bing Li
				this.clientPool.collect(client);
				client.getLock().unlock();
			}
		}
		return SystemMessageConfig.NO_MESSAGE;
	}

	/*
	 * Send a request to the remote node at the client key and wait until the response is received. 09/21/2014, Bing Li
	 * 
	 * The parameters:
	 * 
	 * 		String nodeKey: the local client key that is unique to differentiate it from others client on the remote server;
	 * 
	 * 		String clientKey: the client key that represents the remote node, upon which the IP address and the port can be retrieved from the client pool;
	 * 
	 * 		ServerMessage request: the request sent to the remote node;
	 * 
	 * The return value:
	 * 
	 * 		ServerMessage: the response to be received after the request is sent.
	 * 
	 */
	public ServerMessage read(String nodeKey, String clientKey, ServerMessage request) throws RemoteReadException, IOException, ClassNotFoundException
	{
		// Get the instance of FreeClient by the client key. 09/21/2014, Bing Li
		FreeClient client = clientPool.get(nodeKey, clientKey);
		if (client != UtilConfig.NO_CLIENT)
		{
			// The lock is hold by each particular instance of FreeClient. It guarantees that the sending, receiving and collecting become an atomic operation, which can never be interrupted by other concurrent operations of the client. 09/21/2014, Bing Li
			client.getLock().lock();
			try
			{
				// Send a request, wait for the response and return it. 09/21/2014, Bing Li
				return client.sendWithResponse(request);
			}
			finally
			{
				// Collect the client. 09/21/2014, Bing Li
				clientPool.collect(client);
				client.getLock().unlock();
			}
		}
		return SystemMessageConfig.NO_MESSAGE;
	}
}
