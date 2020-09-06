package org.greatfree.dsf.old.multicast.child;

import java.io.IOException;

import org.greatfree.client.FreeClientPool;
import org.greatfree.dsf.multicast.MulticastConfig;
import org.greatfree.dsf.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.multicast.child.abandoned.ClusterChildBroadcastNotifier;
import org.greatfree.util.IPAddress;

// Created; 07/19/2017, Bing Li
class ClusterChildSubstrate
{
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private IPAddress rootAddress;

	// The root IP address should be broadcast to each node in the cluster before broading requesting/responding can be performed. 05/20/2017, Bing Li
	private ClusterChildBroadcastNotifier<OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator> rootIPBroadcastNotifier;

	private ClusterChildSubstrate()
	{
	}
	
	private static ClusterChildSubstrate instance = new ClusterChildSubstrate();
	
	public static ClusterChildSubstrate CLUSTER()
	{
		if (instance == null)
		{
			instance = new ClusterChildSubstrate();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException
	{
		this.rootIPBroadcastNotifier.dispose();
	}
	
	public void init(String localIPKey, FreeClientPool clientPool)
	{
		// Initialize the root IP broadcast notifier to forward the root IP address to its children. 06/15/2017, Bing Li
		this.rootIPBroadcastNotifier = new ClusterChildBroadcastNotifier<OldRootIPAddressBroadcastNotification, RootIPAddressBroadcastNotificationCreator>(localIPKey, clientPool, MulticastConfig.MULTICASTOR_POOL_SIZE, MulticastConfig.RESOURCE_WAIT_TIME, new RootIPAddressBroadcastNotificationCreator());
	}
	
	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}

	/*
	 * Expose the root IP. 05/20/2017, Bing Li
	 */
	public String getRootIP()
	{
		return this.rootAddress.getIP();
	}

	/*
	 * Expose the root port. 05/20/2017, Bing Li
	 */
	public int getRootPort()
	{
		return this.rootAddress.getPort();
	}
	
	/*
	 * Broadcast the root IP address notification. 05/19/2017, Bing Li
	 */
	public void broadcastRootIP(OldRootIPAddressBroadcastNotification notification, int subBranchCount) throws InstantiationException, IllegalAccessException, IOException, InterruptedException
	{
		this.rootIPBroadcastNotifier.notifiy(notification, subBranchCount);
	}
}
