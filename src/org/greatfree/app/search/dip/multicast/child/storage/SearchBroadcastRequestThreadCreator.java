package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.SearchMultiRequest;
import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

// Created: 09/28/2018, Bing Li
class SearchBroadcastRequestThreadCreator implements NotificationThreadCreatable<SearchMultiRequest, SearchBroadcastRequestThread>
{

	@Override
	public SearchBroadcastRequestThread createNotificationThreadInstance(int taskSize)
	{
		return new SearchBroadcastRequestThread(taskSize);
	}

}
