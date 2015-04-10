package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StopCrawlNotificationMulticastor. It is used by the resource pool to manage the children multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlNotificationMulticastorCreator implements HashCreatable<StopCrawlNotificationMulticastorSource, StopCrawlNotificationMulticastor>
{
	/*
	 * Define the method to create the instances of StartCrawlNotificationMulticastor upon the source, StopCrawlNotificationMulticastorCreator. 11/27/2014, Bing Li
	 */
	@Override
	public StopCrawlNotificationMulticastor createResourceInstance(StopCrawlNotificationMulticastorSource source)
	{
		return new StopCrawlNotificationMulticastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}
}
