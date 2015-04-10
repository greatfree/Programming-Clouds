package com.greatfree.testing.server;

import java.io.IOException;

import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.data.ServerConfig;

/*
 * The pool is responsible for creating and managing instance of FreeClient to achieve the goal of using as small number of instances of FreeClient to send messages to the client in a high performance. 11/24/2014, Bing Li
 */

// Created: 09/17/2014, Bing Li
public class ClientPool
{
	// An instance of FreeClientPool is defined to interact with the client. 11/24/2014, Bing Li
	private FreeClientPool pool;

	/*
	 * Define the singleton wrapper. 09/17/2014, Bing Li
	 */
	private ClientPool()
	{
	}
	
	/*
	 * Define the singleton wrapper. The static method to access the instance of the client pool is named P2P, which represents the peer-to-peer. Since the pool is usually used by an eventing server which connects to the remote end, a P2P architecture is formed. So the method is named like this. 09/17/2014, Bing Li
	 */
	private static ClientPool instance = new ClientPool();

	public static ClientPool SERVER()
	{
		if (instance == null)
		{
			instance = new ClientPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 11/24/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		this.pool.dispose();
	}
	
	/*
	 * Initialize the client pool. The method is called when the crawler process is started. 11/24/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/24/2014, Bing Li
		this.pool = new FreeClientPool(ServerConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 11/24/2014, Bing Li
		this.pool.setIdleChecker(ServerConfig.CLIENT_IDLE_CHECK_DELAY, ServerConfig.CLIENT_IDLE_CHECK_PERIOD, ServerConfig.CLIENT_MAX_IDLE_TIME);
	}

	/*
	 * Expose the client pool. 11/24/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.pool;
	}
}
