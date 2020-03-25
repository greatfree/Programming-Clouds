package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of UnicastRequestReader for the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastReaderCreator implements HashCreatable<UnicastReaderSource, UnicastReader>
{
	/*
	 * Initialize the unicastor. 11/29/2014, Bing Li
	 */
	@Override
	public UnicastReader createResourceInstance(UnicastReaderSource source)
	{
		return new UnicastReader(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
