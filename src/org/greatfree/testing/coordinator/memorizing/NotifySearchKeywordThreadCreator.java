package org.greatfree.testing.coordinator.memorizing;

import org.greatfree.concurrency.reactive.NotificationQueueCreator;
import org.greatfree.testing.message.SearchKeywordBroadcastResponse;

/*
 * The creator here attempts to create instances of NotifySearchKeywordThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/28/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class NotifySearchKeywordThreadCreator implements NotificationQueueCreator<SearchKeywordBroadcastResponse, NotifySearchKeywordThread>
{
	@Override
	public NotifySearchKeywordThread createInstance(int taskSize)
	{
		return new NotifySearchKeywordThread(taskSize);
	}
}
