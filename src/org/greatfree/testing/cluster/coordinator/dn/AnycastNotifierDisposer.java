package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the instance of AnycastNotifier. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotifierDisposer implements HashDisposable<AnycastNotifier>
{

	@Override
	public void dispose(AnycastNotifier t)
	{
		t.dispose();
	}

}
