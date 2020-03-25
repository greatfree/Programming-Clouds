package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of StopDNNotificationMulticastor. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNNotificationMulticastorDisposer implements HashDisposable<StopDNNotificationMulticastor>
{

	@Override
	public void dispose(StopDNNotificationMulticastor t)
	{
		t.dispose();
	}

}
