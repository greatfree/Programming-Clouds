package ca.multicast.search.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import ca.multicast.search.message.SearchQueryRequest;

// Created: 03/16/2020, Bing Li
class SearchQueryBroadcastRequestThreadCreator implements NotificationThreadCreatable<SearchQueryRequest, SearchQueryBroadcastRequestThread>
{

	@Override
	public SearchQueryBroadcastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new SearchQueryBroadcastRequestThread(taskSize);
	}

}
