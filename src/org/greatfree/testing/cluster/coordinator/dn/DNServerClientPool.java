package org.greatfree.testing.cluster.coordinator.dn;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.testing.coordinator.CoorConfig;
import org.greatfree.util.ServerStatus;

// Created: 11/22/2016, Bing Li
public class DNServerClientPool
{
	// An instance of FreeClientPool is defined to interact with the crawler. 11/28/2014, Bing Li
	private FreeClientPool clientPool;
	
	private DNServerClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/28/2014, Bing Li
	 */
	private static DNServerClientPool instance = new DNServerClientPool();
	
	public static DNServerClientPool COORDINATE()
	{
		if (instance == null)
		{
			instance = new DNServerClientPool();
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
			ServerStatus.FREE().printException(e);
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
