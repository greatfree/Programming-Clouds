package org.greatfree.app.search.dip.multicast.root.entry;

import org.greatfree.app.search.dip.multicast.message.SearchMultiResponse;
import org.greatfree.concurrency.reactive.NotificationQueueCreator;

// Created: 10/08/2018, Bing Li
class SearchMultiResponseThreadCreator implements NotificationQueueCreator<SearchMultiResponse, SearchMultiResponseThread>
{

	@Override
	public SearchMultiResponseThread createInstance(int taskSize)
	{
		return new SearchMultiResponseThread(taskSize);
	}

}
