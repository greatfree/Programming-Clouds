package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of UnicastNotifier. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotifierDisposer implements HashDisposable<UnicastNotifier>
{

	@Override
	public void dispose(UnicastNotifier t)
	{
		t.dispose();
	}

}
