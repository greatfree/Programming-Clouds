package org.greatfree.testing.coordinator.searching;

import org.greatfree.client.FreeClientPool;
import org.greatfree.testing.coordinator.CoorConfig;

/*
 * This is a pool that creates and manages instances of FreeClient to connect the search clients in a high performance. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchClientPool
{
	// An instance of FreeClientPool is defined to interact with the crawler. 11/29/2014, Bing Li
	private FreeClientPool clientPool;
	
	private SearchClientPool()
	{
	}
	
	/*
	 * A singleton definition. 11/29/2014, Bing Li
	 */
	private static SearchClientPool instance = new SearchClientPool();
	
	public static SearchClientPool COORDINATE()
	{
		if (instance == null)
		{
			instance = new SearchClientPool();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 11/29/2014, Bing Li
	 */
	public void dispose()
	{
		this.clientPool.dispose();
	}
	
	/*
	 * Initialize the client pool. The method is called when the coordinator process is started. 11/29/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client pool. 11/29/2014, Bing Li
		this.clientPool = new FreeClientPool(CoorConfig.CSERVER_CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 11/29/2014, Bing Li
		this.clientPool.setIdleChecker(CoorConfig.CSERVER_CLIENT_IDLE_CHECK_DELAY, CoorConfig.CSERVER_CLIENT_IDLE_CHECK_PERIOD, CoorConfig.CSERVER_CLIENT_MAX_IDLE_TIME);
	}
	
	/*
	 * Expose the client pool. 11/29/2014, Bing Li
	 */
	public FreeClientPool getPool()
	{
		return this.clientPool;
	}
}
