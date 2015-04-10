package com.greatfree.testing.coordinator.admin;

import com.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StopCrawlMulticastor. 11/26/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class StopCrawlMulticastorDisposer implements HashDisposable<StopCrawlMulticastor>
{
	@Override
	public void dispose(StopCrawlMulticastor t)
	{
		t.dispose();
	}
}
