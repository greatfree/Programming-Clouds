package com.greatfree.testing.coordinator.searching;

import com.greatfree.multicast.RootBroadcastReaderSource;
import com.greatfree.multicast.RootBroadcastRequestCreatorGettable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The source contains all of the arguments to create the instance of broadcast reader, SearchKeywordBroadcastReader. It is used by the resource pool that manages the instances of SearchKeywordBroadcastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastReaderSource extends RootBroadcastReaderSource<String, SearchKeywordBroadcastRequest, SearchKeywordBroadcastRequestCreator> implements RootBroadcastRequestCreatorGettable<SearchKeywordBroadcastRequest, String>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, SearchKeywordBroadcastRequestCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}

	/*
	 * Expose the broadcast request creator. 11/29/2014, Bing Li
	 */
	@Override
	public SearchKeywordBroadcastRequestCreator getRequestCreator()
	{
		return super.getCreator();
	}
}
