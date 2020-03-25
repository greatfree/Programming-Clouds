package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of AnycastRequestReader for the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastReaderCreator implements HashCreatable<AnycastReaderSource, AnycastReader>
{
	/*
	 * Initialize the anycastor. 11/29/2014, Bing Li
	 */
	@Override
	public AnycastReader createResourceInstance(AnycastReaderSource source)
	{
		return new AnycastReader(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}

}
