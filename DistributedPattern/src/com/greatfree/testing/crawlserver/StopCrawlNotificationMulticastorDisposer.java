package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StopCrawlNotificationMulticastor. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlNotificationMulticastorDisposer implements HashDisposable<StopCrawlNotificationMulticastor>
{
	@Override
	public void dispose(StopCrawlNotificationMulticastor t)
	{
		t.dispose();
	}
}
