package com.greatfree.testing.client;

import java.io.IOException;

import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.data.ClientConfig;

/*
 * This is a class that manages all of instances of FreeClient to connect to the remote server. 09/21/2014, Bing Li
 */

// Created: 09/21/2014, Bing Li
public class ClientPool
{
	// Define an instance of FreeClientPool. 09/21/2014, Bing Li
	private FreeClientPool pool;

	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private ClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/23/2014, Bing Li
	 */
	private static ClientPool instance = new ClientPool();
	
	public static ClientPool LOCAL()
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
	 * Dispose the free client pool. 11/23/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		this.pool.dispose();
	}
	
	/*
	 * Initialize the client pool. The method is called when the client process is started. 09/17/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/23/2014, Bing Li
		this.pool = new FreeClientPool(ClientConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 11/23/2014, Bing Li
		this.pool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);
	}

	/*
	 * Expose the client pool. 11/23/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.pool;
	}
}
