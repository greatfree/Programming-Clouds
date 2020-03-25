package org.greatfree.testing.coordinator.searching;

import org.greatfree.client.FreeClientPool;
import org.greatfree.multicast.RootBroadcastReaderSource;
import org.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The source contains all of the arguments to create the instance of broadcast reader, SearchKeywordBroadcastReader. It is used by the resource pool that manages the instances of SearchKeywordBroadcastReader. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordBroadcastReaderSource extends RootBroadcastReaderSource<String, SearchKeywordBroadcastRequest, SearchKeywordBroadcastRequestCreator>
{

	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public SearchKeywordBroadcastReaderSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, SearchKeywordBroadcastRequestCreator creator, long waitTime)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator, waitTime);
	}
}
