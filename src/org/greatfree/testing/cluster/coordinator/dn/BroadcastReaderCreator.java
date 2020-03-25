package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of DNBroadcastReader for the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastReaderCreator implements HashCreatable<BroadcastReaderSource, BroadcastReader>
{
	/*
	 * Initialize the broadcastor. 11/29/2014, Bing Li
	 */
	@Override
	public BroadcastReader createResourceInstance(BroadcastReaderSource source)
	{
		return new BroadcastReader(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
