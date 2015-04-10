package com.greatfree.testing.memory;

import com.greatfree.reuse.HashDisposable;

/*
 * The disposer to collect the resource in the instance of IsPublisherExistedChildAnycastor. It is also used by the resource pool to utilize the anycastors efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class IsPublisherExistedChildAnycastorDisposer implements HashDisposable<IsPublisherExistedChildAnycastor>
{
	@Override
	public void dispose(IsPublisherExistedChildAnycastor t)
	{
		t.dispose();
	}
}
