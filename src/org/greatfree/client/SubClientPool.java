package org.greatfree.client;

import java.io.IOException;

import org.greatfree.data.ClientConfig;

/*
 * The pool is responsible for creating and managing instance of FreeClient to achieve the goal of using as small number of instances of FreeClient to send messages to the children distributed nodes in a high performance. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class SubClientPool
{
	// An instance of FreeClientPool is defined to interact with the children distributed nodes. 11/27/2014, Bing Li
	private FreeClientPool clientPool;
	
	private SubClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/27/2014, Bing Li
	 */
	private static SubClientPool instance = new SubClientPool();
	
	public static SubClientPool SERVER()
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
	 * Dispose the free client pool. 11/27/2014, Bing Li
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
	 * Initialize the client pool. The method is called when the distributed node process is started. 11/27/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/23/2014, Bing Li
		this.clientPool = new FreeClientPool(ClientConfig.CLIENT_POOL_SIZE);
		this.clientPool.setIdleChecker(ClientConfig.CLIENT_IDLE_CHECK_DELAY, ClientConfig.CLIENT_IDLE_CHECK_PERIOD, ClientConfig.CLIENT_MAX_IDLE_TIME);
	}

	/*
	 * Expose the client pool. 11/27/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.clientPool;
	}
}
