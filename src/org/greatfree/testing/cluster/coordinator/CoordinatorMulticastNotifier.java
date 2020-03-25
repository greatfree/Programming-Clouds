package org.greatfree.testing.cluster.coordinator;

import java.io.IOException;
import java.util.Set;

import org.greatfree.data.ServerConfig;
import org.greatfree.reuse.ResourcePool;
import org.greatfree.testing.cluster.coordinator.dn.AnycastNotificationCreator;
import org.greatfree.testing.cluster.coordinator.dn.AnycastNotifier;
import org.greatfree.testing.cluster.coordinator.dn.AnycastNotifierCreator;
import org.greatfree.testing.cluster.coordinator.dn.AnycastNotifierDisposer;
import org.greatfree.testing.cluster.coordinator.dn.AnycastNotifierSource;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastNotificationCreator;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastNotifier;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastNotifierCreator;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastNotifierDisposer;
import org.greatfree.testing.cluster.coordinator.dn.BroadcastNotifierSource;
import org.greatfree.testing.cluster.coordinator.dn.DNServerClientPool;
import org.greatfree.testing.cluster.coordinator.dn.UnicastNotificationCreator;
import org.greatfree.testing.cluster.coordinator.dn.UnicastNotifier;
import org.greatfree.testing.cluster.coordinator.dn.UnicastNotifierCreator;
import org.greatfree.testing.cluster.coordinator.dn.UnicastNotifierDisposer;
import org.greatfree.testing.cluster.coordinator.dn.UnicastNotifierSource;

import com.google.common.collect.Sets;

/*
 * This is a singleton that contains all of the notification multicastor pools. Those multicastors are critical to compose a cluster for all of the DN servers. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class CoordinatorMulticastNotifier
{
	// The pool for the multicastor which broadcasts the notification of BroadcastNotification. 11/26/2014, Bing Li
	private ResourcePool<BroadcastNotifierSource, BroadcastNotifier, BroadcastNotifierCreator, BroadcastNotifierDisposer> broadcastNotifierPool;
	// The pool for the multicastor which unicasts the notification of UnicastNotification. 11/26/2014, Bing Li
	private ResourcePool<UnicastNotifierSource, UnicastNotifier, UnicastNotifierCreator, UnicastNotifierDisposer> unicastNotifierPool;
	// The pool for the multicastor which anycasts the notification of AnycastNotification. 11/26/2014, Bing Li
	private ResourcePool<AnycastNotifierSource, AnycastNotifier, AnycastNotifierCreator, AnycastNotifierDisposer> anycastNotifierPool;

	private CoordinatorMulticastNotifier()
	{
	}

	/*
	 * A singleton implementation. 11/26/2014, Bing Li
	 */
	private static CoordinatorMulticastNotifier instance = new CoordinatorMulticastNotifier();
	
	public static CoordinatorMulticastNotifier COORDINATE()
	{
		if (instance == null)
		{
			instance = new CoordinatorMulticastNotifier();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose all of the pools. 11/26/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.broadcastNotifierPool.shutdown();
		this.unicastNotifierPool.shutdown();
		this.anycastNotifierPool.shutdown();
	}
	
	/*
	 * Initialize the pools. 11/26/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the broadcastor pool/ 11/25/2016, Bing Li
		this.broadcastNotifierPool = new ResourcePool<BroadcastNotifierSource, BroadcastNotifier, BroadcastNotifierCreator, BroadcastNotifierDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new BroadcastNotifierCreator(), new BroadcastNotifierDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
		// Initialize the unicastor pool/ 11/25/2016, Bing Li
		this.unicastNotifierPool = new ResourcePool<UnicastNotifierSource, UnicastNotifier, UnicastNotifierCreator, UnicastNotifierDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new UnicastNotifierCreator(), new UnicastNotifierDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
		// Initialize the anycastor pool/ 11/25/2016, Bing Li
		this.anycastNotifierPool = new ResourcePool<AnycastNotifierSource, AnycastNotifier, AnycastNotifierCreator, AnycastNotifierDisposer>(ServerConfig.MULTICASTOR_POOL_SIZE, new AnycastNotifierCreator(), new AnycastNotifierDisposer(), ServerConfig.MULTICASTOR_POOL_WAIT_TIME);
	}
	
	/*
	 * Disseminate the notification of BroadcastNotification to all of the DNs. 11/26/2014, Bing Li
	 */
	public void disseminateBroadcastNotification(String message) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of BroadcastNotifier from the pool. 11/26/2014, Bing Li
		BroadcastNotifier notifier = this.broadcastNotifierPool.get(new BroadcastNotifierSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new BroadcastNotificationCreator()));
		// Check whether the BroadcastNotifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the broadcast notification. 11/26/2014, Bing Li
			notifier.disseminate(message);
			// Collect the instance of BroadcastNotifier. 11/26/2014, Bing Li
			this.broadcastNotifierPool.collect(notifier);
		}
	}
	
	/*
	 * Disseminate the notification of BroadcastNotification to all of the DNs. 11/26/2014, Bing Li
	 */
	public void disseminateUnicastNotification(String message, String dnKey) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of UnicastNotifier from the pool. 11/26/2014, Bing Li
		UnicastNotifier notifier = this.unicastNotifierPool.get(new UnicastNotifierSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new UnicastNotificationCreator()));
		// Check whether the UnicastNotifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// It is suggested to define a new interface such that it is not necessary to put the dnKey into a Set. 11/25/2016, Bing Li
			Set<String> dnKeys = Sets.newHashSet();
			dnKeys.add(dnKey);
			// Disseminate the unicast notification. 11/26/2014, Bing Li
			notifier.disseminate(message, dnKeys);
			// Collect the instance of UnicastNotifier. 11/26/2014, Bing Li
			this.unicastNotifierPool.collect(notifier);
		}
	}
	
	/*
	 * Disseminate the notification of AnycastNotification to all of the DNs. 11/26/2014, Bing Li
	 */
	public void disseminateAnycastNotification(String message) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of AnycastNotifier from the pool. 11/26/2014, Bing Li
		AnycastNotifier notifier = this.anycastNotifierPool.get(new AnycastNotifierSource(DNServerClientPool.COORDINATE().getPool(), ServerConfig.ROOT_MULTICAST_BRANCH_COUNT, ServerConfig.MULTICAST_BRANCH_COUNT, new AnycastNotificationCreator()));
		// Check whether the AnycastNotifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the anycast notification. 11/26/2014, Bing Li
			notifier.disseminate(message);
			// Collect the instance of AnycastNotifier. 11/26/2014, Bing Li
			this.anycastNotifierPool.collect(notifier);
		}
	}
}
