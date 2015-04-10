package com.greatfree.testing.coordinator.searching;

import com.greatfree.multicast.RootRequestBroadcastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.coordinator.CoorConfig;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * The reader is derived from the RootRequestBroadcastor. It attempts to retrieve data in the way of broadcast among the cluster of memory nodes. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastReader extends RootRequestBroadcastor<String, SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, SearchKeywordBroadcastRequestCreator>
{
	/*
	 * Initialize the broadcastor. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, SearchKeywordBroadcastRequestCreator requestCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, CoorConfig.BROADCAST_REQUEST_WAIT_TIME);
	}
}
