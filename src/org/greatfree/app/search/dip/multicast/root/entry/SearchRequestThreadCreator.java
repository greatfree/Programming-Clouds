package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.SearchRequest;
import org.greatfree.app.search.dip.multicast.message.SearchResponse;
import org.greatfree.app.search.dip.multicast.message.SearchStream;
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
