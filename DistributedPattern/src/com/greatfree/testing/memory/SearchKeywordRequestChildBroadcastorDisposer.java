package com.greatfree.testing.memory;

import com.greatfree.reuse.HashDisposable;

/*
 * The disposer collects the resource of a broadcastor. It is used by the resource pool to save resources. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequestChildBroadcastorDisposer implements HashDisposable<SearchKeywordRequestChildBroadcastor>
{
	@Override
	public void dispose(SearchKeywordRequestChildBroadcastor t)
	{
		t.dispose();
	}
}
