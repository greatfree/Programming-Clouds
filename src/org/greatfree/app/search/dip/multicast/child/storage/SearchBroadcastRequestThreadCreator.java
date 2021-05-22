package org.greatfree.app.search.dip.multicast.child.storage;

import org.greatfree.app.search.dip.multicast.message.SearchMultiRequest;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 09/28/2018, Bing Li
class SearchBroadcastRequestThreadCreator implements NotificationQueueCreator<SearchMultiRequest, SearchBroadcastRequestThread>
{

	@Override
	public SearchBroadcastRequestThread createInstance(int taskSize)
	{
		return new SearchBroadcastRequestThread(taskSize);
	}

}
