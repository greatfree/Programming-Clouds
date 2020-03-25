package org.greatfree.testing.cluster.coordinator.dn;

import org.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the anycast reader, AnycastRequestReader. It is used by the resource pool for AnycastRequestReader. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastReaderDisposer implements HashDisposable<AnycastReader>
{

	@Override
	public void dispose(AnycastReader t)
	{
		t.dispose();
	}

}
