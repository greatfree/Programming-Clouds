package org.greatfree.testing.cluster.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the resource of a broadcastor. It is used by the resource pool to save resources. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestChildBroadcastorDisposer implements HashDisposable<DNBroadcastRequestChildBroadcastor>
{

	@Override
	public void dispose(DNBroadcastRequestChildBroadcastor t)
	{
		t.dispose();
	}

}
