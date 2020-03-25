package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of BroadcastNotifier. It is used by the resource pool to manage the children multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/23/2016, Bing Li
public class BroadcastNotifierCreator implements HashCreatable<BroadcastNotifierSource, BroadcastNotifier>
{

	@Override
	public BroadcastNotifier createResourceInstance(BroadcastNotifierSource source)
	{
		return new BroadcastNotifier(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}

}
