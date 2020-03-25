package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the broadcast reader, DNBroadcastReader. It is used by the resource pool for DNBroadcastReader. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastReaderDisposer implements HashDisposable<BroadcastReader>
{

	@Override
	public void dispose(BroadcastReader t)
	{
		t.dispose();
	}

}
