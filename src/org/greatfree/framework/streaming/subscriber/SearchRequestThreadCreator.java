package org.greatfree.framework.streaming.subscriber;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.framework.streaming.message.SearchRequest;
import org.greatfree.framework.streaming.message.SearchResponse;
import org.greatfree.framework.streaming.message.SearchStream;

// Created: 03/21/2020, Bing Li
public class SearchRequestThreadCreator implements RequestThreadCreatable<SearchRequest, SearchStream, SearchResponse, SearchRequestThread>
{

	@Override
	public SearchRequestThread createRequestThreadInstance(int taskSize)
	{
		return new SearchRequestThread(taskSize);
	}

}
