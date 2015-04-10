package com.greatfree.testing.memory;

import com.greatfree.multicast.ChildMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The child broadcastor forwards the instance of SearchKeywordBroadcastRequest to the local node's children. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequestChildBroadcastor extends ChildMulticastor<SearchKeywordBroadcastRequest, SearchKeywordBroadcastRequestCreator>
{
	public SearchKeywordRequestChildBroadcastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, SearchKeywordBroadcastRequestCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}
}
