package com.greatfree.testing.coordinator.searching;

import com.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the anycast reader, IsPublisherExistedAnycastReader. It is used by the resource pool for IsPublisherExistedAnycastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedAnycastReaderDisposer implements HashDisposable<IsPublisherExistedAnycastReader>
{
	@Override
	public void dispose(IsPublisherExistedAnycastReader t)
	{
		t.dispose();
	}
}
