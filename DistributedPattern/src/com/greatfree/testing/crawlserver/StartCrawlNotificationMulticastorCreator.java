package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.HashCreatable;

/*
 * The class intends to create the interface of StartCrawlNotificationMulticastor. It is used by the resource pool to manage the children multicastors efficiently. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlNotificationMulticastorCreator implements HashCreatable<StartCrawlNotificationMulticastorSource, StartCrawlNotificationMulticastor>
{
	/*
	 * Define the method to create the instances of StartCrawlNotificationMulticastor upon the source, StartCrawlNotificationMulticastorSource. 11/27/2014, Bing Li
	 */
	@Override
	public StartCrawlNotificationMulticastor createResourceInstance(StartCrawlNotificationMulticastorSource source)
	{
		return new StartCrawlNotificationMulticastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}
}
