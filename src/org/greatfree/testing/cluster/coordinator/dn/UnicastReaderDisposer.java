package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the unicast reader, UnicastRequestReader. It is used by the resource pool for UnicastRequestReader. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastReaderDisposer implements HashDisposable<UnicastReader>
{

	@Override
	public void dispose(UnicastReader t)
	{
		t.dispose();
	}

}
