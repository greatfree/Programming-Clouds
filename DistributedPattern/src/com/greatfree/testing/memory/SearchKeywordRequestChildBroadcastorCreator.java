package com.greatfree.testing.memory;

import com.greatfree.reuse.HashCreatable;

/*
 * The creator initializes an instance of SearchKeywordRequestChildBroadcastor. It works with the resource pool to utilize the broadcastor efficiently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequestChildBroadcastorCreator implements HashCreatable<SearchKeywordRequestChildBroadcastorSource, SearchKeywordRequestChildBroadcastor>
{
	@Override
	public SearchKeywordRequestChildBroadcastor createResourceInstance(SearchKeywordRequestChildBroadcastorSource source)
	{
		return new SearchKeywordRequestChildBroadcastor(source.getClientPool(), source.getTreeBranchCount(), source.getServerPort(), source.getMessageCreator());
	}
}
