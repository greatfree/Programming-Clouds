package org.greatfree.dsf.cps.cache.coordinator.postfetching;

import org.greatfree.concurrency.reactive.NotificationObjectThreadCreatable;
import org.greatfree.dsf.cps.cache.message.FetchMyCacheTimingNotification;

// Created: 08/19/2018, Bing Li
public class PostfetchMyCacheTimingThreadCreator implements NotificationObjectThreadCreatable<FetchMyCacheTimingNotification, PostfetchMyCacheTimingThread>
{

	@Override
	public PostfetchMyCacheTimingThread createNotificationThreadInstance(int taskSize)
	{
		return new PostfetchMyCacheTimingThread(taskSize);
	}

}
