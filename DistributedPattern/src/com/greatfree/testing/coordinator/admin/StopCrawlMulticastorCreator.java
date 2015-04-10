package com.greatfree.testing.coordinator.admin;

import com.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StopCrawlMulticastor. It is used by the resource pool to manage the multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMulticastorCreator implements HashCreatable<StopCrawlMulticastorSource, StopCrawlMulticastor>
{
	/*
	 * Define the method to create the instances of StopCrawlMulticastor upon the source, StopCrawlMulticastorSource. 11/27/2014, Bing Li
	 */
	@Override
	public StopCrawlMulticastor createResourceInstance(StopCrawlMulticastorSource source)
	{
		return new StopCrawlMulticastor(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
