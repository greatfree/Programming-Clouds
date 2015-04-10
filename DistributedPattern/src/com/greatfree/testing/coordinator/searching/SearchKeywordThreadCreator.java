package com.greatfree.testing.coordinator.searching;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.SearchKeywordRequest;
import com.greatfree.testing.message.SearchKeywordResponse;
import com.greatfree.testing.message.SearchKeywordStream;

/*
 * A creator to initialize instances of SearchKeywordThread. It is used in the instance of RequestDispatcher. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchKeywordThreadCreator implements RequestThreadCreatable<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread>
{
	@Override
	public SearchKeywordThread createRequestThreadInstance(int taskSize)
	{
		return new SearchKeywordThread(taskSize);
	}
}
