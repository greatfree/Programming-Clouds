package org.greatfree.framework.admin;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.framework.p2p.RegistryConfig;

/*
 * The pool is responsible for creating and managing instance of FreeClient to achieve the goal of using a small number of instances of FreeClient to send administration messages to the chatting server in a high performance. 04/17/2017, Bing Li
 */

// Created: 04/17/2017, Bing Li
class AdminClientPool
{
	// An instance of FreeClientPool is defined to interact with the coordinator. 04/17/2017, Bing Li
	private FreeClientPool pool;

	private AdminClientPool()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static AdminClientPool instance = new AdminClientPool();
	
	public static AdminClientPool ADMIN()
	{
		if (instance == null)
		{
			instance = new AdminClientPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 04/17/2017, Bing Li
	 */
	public void dispose()
	{
		try
		{
			this.pool.dispose();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Initialize the client pool. The method is called when the crawler process is started. 04/17/2017, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 04/17/2017, Bing Li
		this.pool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.pool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);
	}
	
	/*
	 * Expose the client pool. 04/17/2017, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.pool;
	}

}
