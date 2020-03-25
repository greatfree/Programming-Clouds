package org.greatfree.dip.streaming.subscriber;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.streaming.message.SearchRequest;
import org.greatfree.dip.streaming.message.SearchResponse;
import org.greatfree.dip.streaming.message.SearchStream;

// Created: 03/21/2020, Bing Li
class SearchRequestThreadCreator implements RequestThreadCreatable<SearchRequest, SearchStream, SearchResponse, SearchRequestThread>
{

	@Override
	public SearchRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SearchRequestThread(taskSize);
	}

}
