package org.greatfree.testing.cluster.coordinator.admin;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StopDNMulticastor. It is used by the resource pool to manage the multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMulticastorCreator implements HashCreatable<StopDNMulticastorSource, StopDNMulticastor>
{
	/*
	 * Define the method to create the instances of StopDNMulticastor upon the source, ShutdownDNMulticastorSource. 11/27/2014, Bing Li
	 */
	@Override
	public StopDNMulticastor createResourceInstance(StopDNMulticastorSource source)
	{
		return new StopDNMulticastor(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
