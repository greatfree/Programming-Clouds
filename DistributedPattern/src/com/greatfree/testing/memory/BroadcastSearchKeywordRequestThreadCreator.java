package com.greatfree.testing.memory;

import com.greatfree.concurrency.BoundNotificationThreadCreatable;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.SearchKeywordBroadcastRequest;

/*
 * The creator initialize the instance of the thread, BroadcastSearchKeywordRequestThread. The BoundNotificationDispatcher can schedule tasks to the thread. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class BroadcastSearchKeywordRequestThreadCreator implements BoundNotificationThreadCreatable<SearchKeywordBroadcastRequest, MulticastMessageDisposer<SearchKeywordBroadcastRequest>, BroadcastSearchKeywordRequestThread>
{
	@Override
	public BroadcastSearchKeywordRequestThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<SearchKeywordBroadcastRequest> binder)
	{
		return new BroadcastSearchKeywordRequestThread(taskSize, dispatcherKey, binder);
	}
}
