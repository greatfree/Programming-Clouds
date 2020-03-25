package org.greatfree.testing.coordinator.crawling;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StartCrawlMulticastor. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
public class StartCrawlMulticastorDisposer implements HashDisposable<StartCrawlMulticastor>
{
	@Override
	public void dispose(StartCrawlMulticastor t)
	{
		t.dispose();
	}
}
