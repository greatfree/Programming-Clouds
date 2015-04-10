package com.greatfree.testing.coordinator.crawling;

import com.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StartCrawlMulticastor. It is used by the resource pool to manage the multicastors efficiently. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMulticastorCreator implements HashCreatable<StartCrawlMulticastorSource, StartCrawlMulticastor>
{
	/*
	 * Define the method to create the instances of StartCrawlMulticastor upon the source, StartCrawlMulticastorSource. 11/26/2014, Bing Li
	 */
	@Override
	public StartCrawlMulticastor createResourceInstance(StartCrawlMulticastorSource source)
	{
		return new StartCrawlMulticastor(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
