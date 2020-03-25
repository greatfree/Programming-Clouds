package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of BroadcastNotifier. It is used by the resource pool to manage the multicastors efficiently. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifierCreator implements HashCreatable<BroadcastNotifierSource, BroadcastNotifier>
{
	/*
	 * Define the method to create the instances of BroadcastNotifier upon the source, BroadcastNotifierSource. 11/26/2014, Bing Li
	 */
	@Override
	public BroadcastNotifier createResourceInstance(BroadcastNotifierSource source)
	{
		return new BroadcastNotifier(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
