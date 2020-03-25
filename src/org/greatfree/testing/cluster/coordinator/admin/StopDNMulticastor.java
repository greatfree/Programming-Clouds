package org.greatfree.testing.cluster.coordinator.admin;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootObjectMulticastor;
import org.greatfree.testing.message.StopDNMultiNotification;
import org.greatfree.util.NullObject;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of StopDNMultiNotification to all of the crawlers. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMulticastor extends RootObjectMulticastor<NullObject, StopDNMultiNotification, StopDNNotificationCreator>
{

	public StopDNMulticastor(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StopDNNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}
