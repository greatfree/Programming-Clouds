package org.greatfree.testing.cluster.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastorSource;
import org.greatfree.testing.message.BroadcastNotification;

/*
 * The sources that are needed to create an instance of ChildMulticastor are enclosed in the class. That is required by the pool to create children multicastors. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifierSource extends ChildMulticastorSource<BroadcastNotification, BroadcastNotificationCreator>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public BroadcastNotifierSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, BroadcastNotificationCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}
}
