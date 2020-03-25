package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the instance of AnycastNotifier. It is used by the resource pool to manage the anycastors efficiently. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotifierCreator implements HashCreatable<AnycastNotifierSource, AnycastNotifier>
{
	/*
	 * Define the method to create the instances of AnycastNotifier upon the source, AnycastNotifierSource. 11/26/2014, Bing Li
	 */
	@Override
	public AnycastNotifier createResourceInstance(AnycastNotifierSource source)
	{
		return new AnycastNotifier(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
