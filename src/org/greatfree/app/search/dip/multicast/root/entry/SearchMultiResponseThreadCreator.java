package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.SearchMultiResponse;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 10/08/2018, Bing Li
class SearchMultiResponseThreadCreator implements NotificationThreadCreatable<SearchMultiResponse, SearchMultiResponseThread>
{

	@Override
	public SearchMultiResponseThread createNotificationThreadInstance(int taskSize)
	{
		return new SearchMultiResponseThread(taskSize);
	}

}
