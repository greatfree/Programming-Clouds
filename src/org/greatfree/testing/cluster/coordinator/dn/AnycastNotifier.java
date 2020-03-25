package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootObjectMulticastor;
import org.greatfree.testing.message.AnycastNotification;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of AnycastNotification to any of the DNs. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotifier extends RootObjectMulticastor<String, AnycastNotification, AnycastNotificationCreator>
{
	/*
	 * Initialize the anycastor. 11/26/2014, Bing Li
	 */
	public AnycastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, AnycastNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}
