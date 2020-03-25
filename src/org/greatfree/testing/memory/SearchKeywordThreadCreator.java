package org.greatfree.testing.memory;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.IPResource;
import org.greatfree.concurrency.reactive.BoundBroadcastRequestThreadCreatable;
import org.greatfree.message.MulticastMessageDisposer;
import org.greatfree.testing.message.SearchKeywordBroadcastRequest;
import org.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * The creator is responsible for initializing an instance of SearchKeywordThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordThreadCreator implements BoundBroadcastRequestThreadCreatable<SearchKeywordBroadcastRequest, SearchKeywordBroadcastResponse, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, SearchKeywordThread>
{
	@Override
	public SearchKeywordThread createRequestThreadInstance(IPResource ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<SearchKeywordBroadcastRequest> reqBinder)
	{
		return new SearchKeywordThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}
}
