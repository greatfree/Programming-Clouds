package com.greatfree.testing.memory;

import com.greatfree.multicast.ChildMessageCreatorGettable;
import com.greatfree.multicast.ChildMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The source contains the arguments to initialize a new broadcastor to forward the request of SearchKeywordBroadcastRequest. It is used by the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequestChildBroadcastorSource extends ChildMulticastorSource<SearchKeywordBroadcastRequest, SearchKeywordBroadcastRequestCreator> implements ChildMessageCreatorGettable<SearchKeywordBroadcastRequest>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public SearchKeywordRequestChildBroadcastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, SearchKeywordBroadcastRequestCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}

	/*
	 * Expose the request creator. 11/29/2014, Bing Li
	 */
	@Override
	public SearchKeywordBroadcastRequestCreator getMessageCreator()
	{
		return super.getMessageCreator();
	}
}
