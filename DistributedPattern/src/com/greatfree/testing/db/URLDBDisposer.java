package com.greatfree.testing.db;

import com.greatfree.reuse.Disposable;

/*
 * A disposer that collects instances of URLDB. It works with the URLDBPool to manage the instances of URLDB. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class URLDBDisposer implements Disposable<URLDB>
{
	@Override
	public void dispose(URLDB rsc)
	{
		rsc.dispose();
	}
}
