package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootMulticastorSource;
import org.greatfree.testing.message.UnicastNotification;

/*
 * The class provides the pool with the initial values to create a UnicastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create unicastors. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotifierSource extends RootMulticastorSource<String, UnicastNotification, UnicastNotificationCreator>
{

	public UnicastNotifierSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, UnicastNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}
