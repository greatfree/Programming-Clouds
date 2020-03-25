package org.greatfree.testing.cluster.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastor;
import org.greatfree.testing.message.BroadcastNotification;

/*
 * The multicastor is derived from ChildMulticastor to transfer the notification, BroadcastNotification, to the children nodes. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifier extends ChildMulticastor<BroadcastNotification, BroadcastNotificationCreator>
{

	public BroadcastNotifier(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, BroadcastNotificationCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}

}
