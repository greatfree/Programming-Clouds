package ca.multicast.search.root;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.multicast.search.message.SearchQueryResponse;

// Created: 03/15/2020, Bing Li
class SearchQueryResponseThreadCreator implements NotificationThreadCreatable<SearchQueryResponse, SearchQueryResponseThread>
{

	@Override
	public SearchQueryResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new SearchQueryResponseThread(taskSize);
	}

}
