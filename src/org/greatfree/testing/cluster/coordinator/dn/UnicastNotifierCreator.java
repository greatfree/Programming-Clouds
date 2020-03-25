package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of UnicastNotifier. It is used by the resource pool to manage the unicastors efficiently. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotifierCreator implements HashCreatable<UnicastNotifierSource, UnicastNotifier>
{
	/*
	 * Define the method to create the instances of UnicastNotifier upon the source, UnicastNotifierSource. 11/26/2014, Bing Li
	 */
	@Override
	public UnicastNotifier createResourceInstance(UnicastNotifierSource source)
	{
		return new UnicastNotifier(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
