package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StopDNNotificationMulticastor. It is used by the resource pool to manage the children multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNNotificationMulticastorCreator implements HashCreatable<StopDNNotificationMulticastorSource, StopDNNotificationMulticastor>
{
	/*
	 * Define the method to create the instances of StopDNNotificationMulticastor upon the source, StopDNNotificationMulticastorCreator. 11/27/2014, Bing Li
	 */
	@Override
	public StopDNNotificationMulticastor createResourceInstance(StopDNNotificationMulticastorSource source)
	{
		return new StopDNNotificationMulticastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}

}
