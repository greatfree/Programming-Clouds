package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootMulticastorSource;
import org.greatfree.testing.message.BroadcastNotification;

/*
 * The class provides the pool with the initial values to create a BroadcastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifierSource extends RootMulticastorSource<String, BroadcastNotification, BroadcastNotificationCreator>
{

	public BroadcastNotifierSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, BroadcastNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}
