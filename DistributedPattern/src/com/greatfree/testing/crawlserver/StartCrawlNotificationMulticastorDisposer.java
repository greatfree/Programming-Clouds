package com.greatfree.testing.crawlserver;

import com.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StartCrawlNotificationMulticastor. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StartCrawlNotificationMulticastorDisposer implements HashDisposable<StartCrawlNotificationMulticastor>
{
	@Override
	public void dispose(StartCrawlNotificationMulticastor t)
	{
		t.dispose();
	}
}
