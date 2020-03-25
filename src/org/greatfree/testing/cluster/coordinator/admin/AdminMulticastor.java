package org.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.reuse.ResourcePool;
import org.greatfree.testing.cluster.coordinator.dn.DNServerClientPool;

/*
 * This is a singleton that contains all of the multicastor pools to administer the system. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class AdminMulticastor
{
	// The pool for the multicastor which multicasts the notification of StopDNMultiNotification. 11/27/2014, Bing Li
	private ResourcePool<StopDNMulticastorSource, StopDNMulticastor, StopDNMulticastorCreator, StopDNMulticastorDisposer> stopDNMulticastorPool;


	private AdminMulticastor()
	{
	}

	/*
	 * A singleton implementation. 11/27/2014, Bing Li
	 */
	private static AdminMulticastor instance = new AdminMulticastor();
	
	public static AdminMulticastor ADMIN()
	{
		if (instance == null)
		{
			instance = new AdminMulticastor();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose all of the pools. 11/27/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.stopDNMulticastorPool.shutdown();
	}
	
	/*
	 * Initialize the pools. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.stopDNMulticastorPool = new ResourcePool<StopDNMulticastorSource, StopDNMulticastor, StopDNMulticastorCreator, StopDNMulticastorDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new StopDNMulticastorCreator(), new StopDNMulticastorDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
	}
	
	/*
	 * Disseminate the notification of StartCrawlMultiNotification to all of the crawlers. 11/27/2014, Bing Li
	 */
	public void disseminateStopDNs() throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StartCrawlMulticastor from the pool. 11/27/2014, Bing Li
		StopDNMulticastor multicastor = this.stopDNMulticastorPool.get(new StopDNMulticastorSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new StopDNNotificationCreator()));
		// Check whether the multicastor is valid. 11/27/2014, Bing Li
		if (multicastor != null)
		{
			// Disseminate the notification. The notification contains no data. Thus, it is not necessary to put any arguments here. Just place a null. 11/27/2014, Bing Li
			multicastor.disseminate(null);
			// Collect the instance of StartCrawlMulticastor. 11/27/2014, Bing Li
			this.stopDNMulticastorPool.collect(multicastor);
		}
	}
}
