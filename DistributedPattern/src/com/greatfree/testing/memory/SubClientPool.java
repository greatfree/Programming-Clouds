package com.greatfree.testing.memory;

import java.io.IOException;

import com.greatfree.remote.FreeClientPool;

/*
 * The pool is responsible for creating and managing instance of FreeClient to achieve the goal of using as small number of instances of FreeClient to send messages to the children memory servers in a high performance. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class SubClientPool
{
	// An instance of FreeClientPool is defined to interact with the children memory servers. 11/28/2014, Bing Li
	private FreeClientPool clientPool;
	
	private SubClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/28/2014, Bing Li
	 */
	private static SubClientPool instance = new SubClientPool();
	
	public static SubClientPool STORE()
	{
		if (instance == null)
		{
			instance = new SubClientPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 11/28/2014, Bing Li
	 */
	public void dispose()
	{
		try
		{
			this.clientPool.dispose();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/*
	 * Initialize the client pool. The method is called when the memory server process is started. 11/28/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/28/2014, Bing Li
		this.clientPool = new FreeClientPool(MemConfig.SUB_CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(MemConfig.SUB_CLIENT_IDLE_CHECK_DELAY, MemConfig.SUB_CLIENT_IDLE_CHECK_PERIOD, MemConfig.SUB_CLIENT_MAX_IDLE_TIME);
	}

	/*
	 * Expose the client pool. 11/28/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.clientPool;
	}
}
