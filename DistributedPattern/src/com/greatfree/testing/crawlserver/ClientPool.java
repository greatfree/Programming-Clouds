package com.greatfree.testing.crawlserver;

import java.io.IOException;

import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.data.ClientConfig;

/*
 * The pool is responsible for creating and managing instance of FreeClient to achieve the goal of using as small number of instances of FreeClient to send messages to the coordinator in a high performance. 11/23/2014, Bing Li
 */

// Created: 11/23/2014, Bing Li
public class ClientPool
{
	// An instance of FreeClientPool is defined to interact with the coordinator. 11/23/2014, Bing Li
	private FreeClientPool pool;
	
	private ClientPool()
	{
	}

	/*
	 * A singleton definition. 11/23/2014, Bing Li
	 */
	private static ClientPool instance = new ClientPool();
	
	public static ClientPool CRAWL()
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
	 * Initialize the client pool. The method is called when the crawler process is started. 11/23/2014, Bing Li
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
