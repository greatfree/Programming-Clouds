package org.greatfree.app.search.multicast.root.entry;

import org.greatfree.app.search.multicast.message.SearchRequest;
import org.greatfree.app.search.multicast.message.SearchResponse;
import org.greatfree.app.search.multicast.message.SearchStream;
import org.greatfree.concurrency.reactive.RequestQueueCreator;

// Created: 09/28/2018, Bing Li
class SearchRequestThreadCreator implements RequestQueueCreator<SearchRequest, SearchStream, SearchResponse, SearchRequestThread>
{

	@Override
	public SearchRequestThread createInstance(int taskSize)
	{
		return new SearchRequestThread(taskSize);
	}

}
