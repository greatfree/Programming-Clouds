package com.greatfree.testing.coordinator.searching;

import com.greatfree.reuse.HashCreatable;

/*
 * The creator initializes the instances of SearchKeywordBroadcastReader for the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastReaderCreator implements HashCreatable<SearchKeywordBroadcastReaderSource, SearchKeywordBroadcastReader>
{
	@Override
	public SearchKeywordBroadcastReader createResourceInstance(SearchKeywordBroadcastReaderSource source)
	{
		return new SearchKeywordBroadcastReader(source.getClientPool(), source.getRootBranchCount(), source.getTreeBranchCount(), source.getCreator());
	}
}
