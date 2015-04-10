package com.greatfree.testing.memory;

import com.greatfree.concurrency.BoundBroadcastRequestThreadCreatable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;
import com.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * The creator is responsible for initializing an instance of SearchKeywordThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordThreadCreator implements BoundBroadcastRequestThreadCreatable<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread>
{
	@Override
	public SearchKeywordThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<SearchKeywordBroadcastRequest> reqBinder)
	{
		return new SearchKeywordThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}
}
