package org.greatfree.testing.memory;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.ChildMulticastorSource;
import org.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The source contains the arguments to initialize a new broadcastor to forward the request of SearchKeywordBroadcastRequest. It is used by the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordRequestChildBroadcastorSource extends ChildMulticastorSource<SearchKeywordBroadcastRequest, SearchKeywordBroadcastRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public SearchKeywordRequestChildBroadcastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, SearchKeywordBroadcastRequestCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}
}
