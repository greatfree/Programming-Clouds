package org.greatfree.dsf.streaming.subscriber;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.streaming.message.SearchRequest;
import org.greatfree.dsf.streaming.message.SearchResponse;
import org.greatfree.dsf.streaming.message.SearchStream;

// Created: 03/21/2020, Bing Li
public class SearchRequestThreadCreator implements RequestThreadCreatable<SearchRequest, SearchStream, SearchResponse, SearchRequestThread>
{

	@Override
	public SearchRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SearchRequestThread(taskSize);
	}

}
