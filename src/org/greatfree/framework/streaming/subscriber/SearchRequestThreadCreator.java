package org.greatfree.framework.streaming.subscriber;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.streaming.message.SearchRequest;
import org.greatfree.framework.streaming.message.SearchResponse;
import org.greatfree.framework.streaming.message.SearchStream;

// Created: 03/21/2020, Bing Li
public class SearchRequestThreadCreator implements RequestQueueCreator<SearchRequest, SearchStream, SearchResponse, SearchRequestThread>
{

	@Override
	public SearchRequestThread createInstance(int taskSize)
	{
		return new SearchRequestThread(taskSize);
	}

}
