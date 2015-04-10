package com.greatfree.testing.coordinator.searching;

import com.greatfree.reuse.HashDisposable;

/*
 * This is a disposer that collects the resources of the broadcast reader, SearchKeywordBroadcastReader. It is used by the resource pool for SearchKeywordBroadcastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastReaderDisposer implements HashDisposable<SearchKeywordBroadcastReader>
{
	@Override
	public void dispose(SearchKeywordBroadcastReader t)
	{
		t.dispose();
	}
}
