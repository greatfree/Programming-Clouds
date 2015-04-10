package com.greatfree.testing.coordinator.memorizing;

import java.io.IOException;

import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.coordinator.CoorConfig;

/*
 * This is a pool that creates and manages instances of FreeClient to achieve the goal of using minimum instances of clients to reach a high performance. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryServerClientPool
{
	// An instance of FreeClientPool is defined to interact with the crawler. 11/28/2014, Bing Li
	private FreeClientPool clientPool;
	
	private MemoryServerClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/28/2014, Bing Li
	 */
	private static MemoryServerClientPool instance = new MemoryServerClientPool();
	
	public static MemoryServerClientPool COORDINATE()
	{
		if (instance == null)
		{
			instance = new MemoryServerClientPool();
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
	 * Initialize the client pool. The method is called when the coordinator process is started. 11/28/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/28/2014, Bing Li
		this.clientPool = new FreeClientPool(CoorConfig.CSERVER_CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 11/28/2014, Bing Li
		this.clientPool.setIdleChecker(CoorConfig.CSERVER_CLIENT_IDLE_CHECK_DELAY, CoorConfig.CSERVER_CLIENT_IDLE_CHECK_PERIOD, CoorConfig.CSERVER_CLIENT_MAX_IDLE_TIME);
	}
	
	/*
	 * Expose the client pool. 11/28/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.clientPool;
	}
}
