package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of BroadcastNotifier. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifierDisposer implements HashDisposable<BroadcastNotifier>
{

	@Override
	public void dispose(BroadcastNotifier t)
	{
		t.dispose();
	}

}
