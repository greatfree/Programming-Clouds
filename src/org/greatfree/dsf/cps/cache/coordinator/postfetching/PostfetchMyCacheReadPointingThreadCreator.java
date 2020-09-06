package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.FetchMyCachePointingNotification;

// Created: 08/23/2018, Bing Li
public class PostfetchMyCacheReadPointingThreadCreator implements NotificationObjectThreadCreatable<FetchMyCachePointingNotification, PostfetchMyCacheReadPointingThread>
{

	@Override
	public PostfetchMyCacheReadPointingThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyCacheReadPointingThread(taskSize);
	}

}
