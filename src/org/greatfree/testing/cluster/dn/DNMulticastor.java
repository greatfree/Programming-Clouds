package org.greatfree.testing.cluster.dn;

import java.io.IOException;

import org.greatfree.client.SubClientPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.reuse.ResourcePool;
import org.greatfree.testing.message.BroadcastNotification;
import org.greatfree.testing.message.DNBroadcastRequest;
import org.greatfree.testing.message.StopDNMultiNotification;

/*
 * This is the multicasting mechanism to manage all of the multicastors to transfer notifications to its children DNs. 11/27/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class DNMulticastor
{
	// The pool for the children multicastor which multicasts the notification of BroadcastNotification. 11/27/2014, Bing Li
	private ResourcePool<BroadcastNotifierSource, BroadcastNotifier, BroadcastNotifierCreator, BroadcastNotifierDisposer> broadcastChildNotifierPool;

	// The pool for the children multicastor which multicasts the request of DNBroadcastRequest. 11/27/2014, Bing Li
	private ResourcePool<DNBroadcastRequestChildBroadcastorSource, DNBroadcastRequestChildBroadcastor, DNBroadcastRequestChildBroadcastorCreator, DNBroadcastRequestChildBroadcastorDisposer> dnRequestChildBroadcastorPool;

	// The pool for the children multicastor which multicasts the notification of StopDNMultiNotification. 11/27/2014, Bing Li
	private ResourcePool<StopDNNotificationMulticastorSource, StopDNNotificationMulticastor, StopDNNotificationMulticastorCreator, StopDNNotificationMulticastorDisposer> broadcastStopDNChildNotifierPool;

	private DNMulticastor()
	{
	}
	
	/*
	 * A singleton implementation. 11/27/2014, Bing Li
	 */
	private static DNMulticastor instance = new DNMulticastor();
	
	public static DNMulticastor CLUSTER()
	{
		if (instance == null)
		{
			instance = new DNMulticastor();
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
		this.broadcastChildNotifierPool.shutdown();
		this.dnRequestChildBroadcastorPool.shutdown();
		this.broadcastStopDNChildNotifierPool.shutdown();
	}

	/*
	 * Initialize the pools. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.broadcastChildNotifierPool = new ResourcePool<BroadcastNotifierSource, BroadcastNotifier, BroadcastNotifierCreator, BroadcastNotifierDisposer>(ClientConfig.MULTICASTOR_POOL_SIZE, new BroadcastNotifierCreator(), new BroadcastNotifierDisposer(), ClientConfig.MULTICASTOR_WAIT_TIME);
		this.dnRequestChildBroadcastorPool = new ResourcePool<DNBroadcastRequestChildBroadcastorSource, DNBroadcastRequestChildBroadcastor, DNBroadcastRequestChildBroadcastorCreator, DNBroadcastRequestChildBroadcastorDisposer>(ClientConfig.MULTICASTOR_POOL_SIZE, new DNBroadcastRequestChildBroadcastorCreator(), new DNBroadcastRequestChildBroadcastorDisposer(), ClientConfig.MULTICASTOR_WAIT_TIME);
		this.broadcastStopDNChildNotifierPool = new ResourcePool<StopDNNotificationMulticastorSource, StopDNNotificationMulticastor, StopDNNotificationMulticastorCreator, StopDNNotificationMulticastorDisposer>(ClientConfig.MULTICASTOR_POOL_SIZE, new StopDNNotificationMulticastorCreator(), new StopDNNotificationMulticastorDisposer(), ClientConfig.MULTICASTOR_WAIT_TIME);
	}
	
	/*
	 * Disseminate the notification of BroadcastNotification to the children node of the local one. 11/27/2014, Bing Li
	 */
	public void disseminateBroadcastNotification(BroadcastNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of BroadcastNotifier from the pool. 11/27/2014, Bing Li
		BroadcastNotifier notifier = this.broadcastChildNotifierPool.get(new BroadcastNotifierSource(SubClientPool.SERVER().getPool(), ClientConfig.MULTICAST_BRANCH_COUNT, ServerConfig.COORDINATOR_DN_PORT, new BroadcastNotificationCreator()));
		// Check whether the notifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the notification. 11/27/2014, Bing Li
			notifier.disseminate(notification);
			// Collect the notifier. 11/25/2016, Bing Li
			this.broadcastChildNotifierPool.collect(notifier);
		}
	}

	/*
	 * Disseminate the notification of DNBroadcastRequest to the children node of the local one. 11/27/2014, Bing Li
	 */
	public void disseminateBroadcastRequest(DNBroadcastRequest request) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of DNBroadcastRequestChildBroadcastor from the pool. 11/27/2014, Bing Li
		DNBroadcastRequestChildBroadcastor notifier = this.dnRequestChildBroadcastorPool.get(new DNBroadcastRequestChildBroadcastorSource(SubClientPool.SERVER().getPool(), ClientConfig.MULTICAST_BRANCH_COUNT, ServerConfig.COORDINATOR_DN_PORT, new DNBroadcastRequestCreator()));
		// Check whether the notifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the request. 11/27/2014, Bing Li
			notifier.disseminate(request);
			// Collect the notifier. 11/25/2016, Bing Li
			this.dnRequestChildBroadcastorPool.collect(notifier);
		}
	}

	/*
	 * Disseminate the notification of StopDNMultiNotification to the children node of the local one. 11/27/2014, Bing Li
	 */
	public void disseminateStopDNMultiNotification(StopDNMultiNotification notification) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		// Get an instance of StopDNNotificationMulticastor from the pool. 11/27/2014, Bing Li
		StopDNNotificationMulticastor notifier = this.broadcastStopDNChildNotifierPool.get(new StopDNNotificationMulticastorSource(SubClientPool.SERVER().getPool(), ClientConfig.MULTICAST_BRANCH_COUNT, ServerConfig.COORDINATOR_DN_PORT, new StopDNMultiNotificationCreator()));
		// Check whether the notifier is valid. 11/26/2014, Bing Li
		if (notifier != null)
		{
			// Disseminate the notification. 11/27/2014, Bing Li
			notifier.disseminate(notification);
			// Collect the notifier. 11/25/2016, Bing Li
			this.broadcastStopDNChildNotifierPool.collect(notifier);
		}
	}
}
