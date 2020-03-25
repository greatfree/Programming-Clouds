package org.greatfree.testing.cluster.coordinator.admin;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StopCrawlMulticastor. 11/26/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMulticastorDisposer implements HashDisposable<StopDNMulticastor>
{

	@Override
	public void dispose(StopDNMulticastor t)
	{
		t.dispose();
	}

}
